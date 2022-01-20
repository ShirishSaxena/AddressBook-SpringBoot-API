package com.assignment.addressBook.service;

import com.assignment.addressBook.handling.AddressBookErrorMessage;
import com.assignment.addressBook.handling.AddressBookSuccessMessage;
import com.assignment.addressBook.model.AddressBookEntity;
import com.assignment.addressBook.repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    /******     Get functions       ******/

    @Cacheable(value = "getAll")
    public List<AddressBookEntity> getAll() {
        return addressBookRepository.findAll();
    }

    @Cacheable(value = "getOne")
    public AddressBookEntity getByIdOrEmailOrPhoneNo(String param) {
        // param can be either Id, email or phone. And phone can also be parsed as id, in some cases thus we make use of finally.
        try {
            Long id = Long.parseLong(param);
            Optional<AddressBookEntity> record = addressBookRepository.findById(id);

            if (record.isPresent())
                return record.get();
            else
                return addressBookRepository.findByEmailOrPhoneNo(param, param);
        } catch (NumberFormatException e) {
            // if its not an id
            return addressBookRepository.findByEmailOrPhoneNo(param, param);
        }
    }

    @Cacheable(value = "getName")
    public Object getByFirst_LastName(String name) {
        // check if it's a full name if so, split and get both first and last matches
        name = name.trim();


        String[] splitName = name.split(" ");
        if (splitName.length == 2)
            return addressBookRepository.findByFirstNameAndLastName(splitName[0], splitName[1]);

        return addressBookRepository.findByFirstNameOrLastName(name, name);
    }

    @Cacheable(value = "getAddress")
    public List<AddressBookEntity> getByAddress(String address) {
        return addressBookRepository.findByAddress(address);
    }

    /***************************************/
    /******     POST functions       ******/

    @Caching(evict = {
            @CacheEvict(value = "getOne", allEntries = true),
            @CacheEvict(value = "getName", allEntries = true),
            @CacheEvict(value = "getAddress", allEntries = true),
            @CacheEvict(value = "getAll", allEntries = true)
    })
    public Object saveRecord(AddressBookEntity addressBook) {
        // check if email and phoneNo already exists in database
        // It'll query twice, so somewhat inefficient when dataset is large.
        // I'm sure it can be optimized, and I plan on doing that as soon as a working assignment is completed.
        AddressBookEntity RecordWithEmail = addressBookRepository.findByEmail(addressBook.getEmail());
        AddressBookEntity RecordWithPhoneNo = addressBookRepository.findByPhoneNo(addressBook.getPhoneNo());

        if (RecordWithEmail == null && RecordWithPhoneNo == null)
            return addressBookRepository.save(addressBook);
        else if (RecordWithEmail != null && RecordWithPhoneNo == null)
            return new AddressBookErrorMessage(1, List.of("P112 : Duplicate email found."));
        else if (RecordWithEmail == null && RecordWithPhoneNo != null)
            return new AddressBookErrorMessage(1, List.of("P121 : Duplicate PhoneNo found."));
        else
            return new AddressBookErrorMessage(2, List.of("P112 : Duplicate email found.", "P121 : Duplicate PhoneNo found."));
    }

    @Caching(evict = {
            @CacheEvict(value = "getOne", allEntries = true),
            @CacheEvict(value = "getName", allEntries = true),
            @CacheEvict(value = "getAddress", allEntries = true),
            @CacheEvict(value = "getAll", allEntries = true)
    })
    public Object saveAll(List<AddressBookEntity> addressBookEntityList) {
        Long startTime = System.currentTimeMillis();
        // Validation for List of records {for any errors just return errorlist, doesn't add records}
        List<AddressBookErrorMessage> errorMessageList = validateSaveAllList(addressBookEntityList);
        if (errorMessageList.size() > 0)
            return errorMessageList;

        // A sanity check for dups in list.
        List<AddressBookEntity> cleanedList = checkDupsInList(addressBookEntityList);

        // if List Body is correct for every element, check for duplicate email/phone in currect SQL
        errorMessageList = checkDupsInSQL(cleanedList); // bit inefficient but uh ... :/
        if (errorMessageList.size() > 0)
            return errorMessageList;

        addressBookRepository.saveAll(cleanedList);

        // Long totalRecords, Long startTime, Long endTime, String comment
        return new AddressBookSuccessMessage(Long.valueOf(addressBookEntityList.size()), startTime, System.currentTimeMillis(), cleanedList.size() + " new records added");
    }


    /***************************************/
    /******      PUT functions        ******/

    @Caching(evict = {
            @CacheEvict(value = "getOne", allEntries = true),
            @CacheEvict(value = "getName", allEntries = true),
            @CacheEvict(value = "getAddress", allEntries = true),
            @CacheEvict(value = "getAll", allEntries = true)
    })
    public Object updateRecord(AddressBookEntity addressBook) {
        Long timeStart = System.currentTimeMillis();
        if (addressBook.getId() != null) {
            addressBookRepository.save(addressBook);
            // Long totalRecords, Long startTime, Long endTime, String comment
            return new AddressBookSuccessMessage(1L, timeStart, System.currentTimeMillis(), "Updated record with id'" + addressBook.getId());
        }
        if (addressBook.getEmail() != null) {
            AddressBookEntity currRecord = addressBookRepository.findByEmail(addressBook.getEmail());
            addressBook.setId(currRecord.getId());
            addressBookRepository.save(addressBook);
            return new AddressBookSuccessMessage(1L, timeStart, System.currentTimeMillis(), "Updated record with email'" + addressBook.getEmail());
        }
        return new AddressBookErrorMessage(1, List.of("U100 : No record found to update [id/email]."));
    }


    /***************************************/
    /******      DELETE functions        ******/

    @Caching(evict = {
            @CacheEvict(value = "getOne", key = "#param"),
            @CacheEvict(value = "getName", allEntries = true),
            @CacheEvict(value = "getAddress", allEntries = true),
            @CacheEvict(value = "getAll", allEntries = true)
    })
    public Object delete(String param) {
        Long timeStart = System.currentTimeMillis();
        // delete either by id or email.
        AddressBookEntity currBook = getByIdOrEmailOrPhoneNo(param);
        if (currBook != null) {
            addressBookRepository.deleteById(currBook.getId());
            try {
                Integer n = Integer.parseInt(param);
                return new AddressBookSuccessMessage(1L, timeStart, System.currentTimeMillis(), "deleted record with id'" + n);
            } catch (NumberFormatException e) {
                return new AddressBookSuccessMessage(1L, timeStart, System.currentTimeMillis(), "deleted record with email'" + param);
            }
        } else
            return new AddressBookErrorMessage(1, List.of("D100 : Parameter not in id or email."));
    }


    // Helper functions

    private List<AddressBookEntity> checkDupsInList(List<AddressBookEntity> addressBookEntityList) {
        List<AddressBookEntity> cleanedList = new ArrayList<>();

        HashSet<String> checkedParam = new HashSet<>();

        for (AddressBookEntity record : addressBookEntityList) {
            if (checkedParam.add(record.getEmail()) && checkedParam.add(record.getPhoneNo()))
                cleanedList.add(record);
        }

        return cleanedList;
    }

    private List<AddressBookErrorMessage> checkDupsInSQL(List<AddressBookEntity> addressBookEntityList) {
        List<AddressBookErrorMessage> errorMessageList = new ArrayList<>();
        //HashSet<String> checkDups = new HashSet<>();

        int list_counter = 0;
/*        // save already queried result in HashMap to improve time complexity.
        HashMap<String, AddressBookEntity> hasSeen = new HashMap<>();*/
        for (AddressBookEntity record : addressBookEntityList) {

            AddressBookEntity RecordWithEmail = addressBookRepository.findByEmail(record.getEmail());
            AddressBookEntity RecordWithPhoneNo = addressBookRepository.findByPhoneNo(record.getPhoneNo());

            if (RecordWithEmail == null && RecordWithPhoneNo == null)
                continue;
            else if (RecordWithEmail != null && RecordWithPhoneNo == null)
                errorMessageList.add(new AddressBookErrorMessage(list_counter, 1, List.of("P112 : Duplicate email found.")));
            else if (RecordWithEmail == null && RecordWithPhoneNo != null)
                errorMessageList.add(new AddressBookErrorMessage(list_counter, 1, List.of("P121 : Duplicate PhoneNo found.")));
            else
                errorMessageList.add(new AddressBookErrorMessage(list_counter, 2,
                        List.of("P112 : Duplicate email found.", "P121 : Duplicate PhoneNo found.")));

            list_counter++;
        }
        return errorMessageList;
    }


    private List<AddressBookErrorMessage> validateSaveAllList(List<AddressBookEntity> addressBookEntityList) {
        List<AddressBookErrorMessage> errorMessageList = new ArrayList<>();
        int list_counter = 0; // makes it easier to somewhat know which record in list have errors?
        // Iterate over the whole list and check if it's valid or not.
        for (AddressBookEntity record : addressBookEntityList) {
            List<String> allErrorsInRecord = new ArrayList<>();

            if (record.getFirstName() == null || record.getFirstName().length() == 0)
                allErrorsInRecord.add("P100 : firstName is required and can not be null or empty.");
            if (record.getLastName() == null || record.getLastName().length() == 0)
                allErrorsInRecord.add("P101 : lastName is required and can not be null or empty.");
            if (record.getPhoneNo() == null || record.getPhoneNo().length() == 0)
                allErrorsInRecord.add("P103 : phoneNo is required and can not be null or empty.");

            if (record.getEmail() == null || record.getEmail().length() == 0) {
                allErrorsInRecord.add("P102 : Email is required and can not be null or empty.");
            } else if (!validateEmail(record.getEmail())) {
                allErrorsInRecord.add("P111 : Not a valid email.");
            }
            if (record.getAddress() == null || record.getAddress().length() == 0)
                allErrorsInRecord.add("P104 : address is required and can not be null or empty.");

            if (allErrorsInRecord.size() > 0)
                errorMessageList.add(new AddressBookErrorMessage(list_counter, allErrorsInRecord.size(), allErrorsInRecord));

            list_counter++;
        }
        return errorMessageList;
    }


    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
        return pattern.matcher(email).matches();
    }
}
