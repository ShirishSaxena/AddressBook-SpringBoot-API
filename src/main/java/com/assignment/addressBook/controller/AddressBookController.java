package com.assignment.addressBook.controller;


import com.assignment.addressBook.handling.AddressBookErrorMessage;
import com.assignment.addressBook.model.AddressBookEntity;
import com.assignment.addressBook.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/api")

public class AddressBookController {

    @Autowired
    private AddressBookService addressBook;

    @GetMapping("/getAll")
    public List<AddressBookEntity> getAllRecords() {
        return addressBook.getAll();
    }

    @GetMapping("{param}")
    public Object getByIdOrEmail(@PathVariable String param) {
        AddressBookEntity record = addressBook.getByIdOrEmailOrPhoneNo(param);
        if (record == null)
            return new AddressBookErrorMessage(3, List.of("G100 : No records by id.",
                    "G101 : No records by Email.", "G102 : No records by PhoneNo."));

        return record;
    }

    @GetMapping("/getName/{param}")
    public Object getByFirstOrLastName(@PathVariable String param) {
        return addressBook.getByFirst_LastName(param);
    }

    @GetMapping("/getAddress/{param}")
    public Object getByAddress(@PathVariable String param) {
        return addressBook.getByAddress(param);
    }


    @PostMapping
    public Object AddRecord(@RequestBody @Valid AddressBookEntity addressRecord, Errors errors) {
        if (errors.hasFieldErrors()) {
            List<FieldError> fieldErrors = errors.getFieldErrors();
            return new AddressBookErrorMessage(fieldErrors);
        }
        return addressBook.saveRecord(addressRecord);
    }

    @PostMapping("/saveAll")
    public Object AddRecords(@RequestBody List<AddressBookEntity> addressRecords) {
        return addressBook.saveAll(addressRecords);
    }

    @PutMapping()
    public Object updateRecord(@RequestBody @Valid AddressBookEntity addressRecord, Errors errors) {
        if (errors.hasFieldErrors()) {
            List<FieldError> fieldErrors = errors.getFieldErrors();
            return new AddressBookErrorMessage(fieldErrors);
        }

        return addressBook.updateRecord(addressRecord);
    }

    @DeleteMapping("{param}")
    public Object deleteRecord(@PathVariable String param) {
        return addressBook.delete(param);
    }


    /* */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
