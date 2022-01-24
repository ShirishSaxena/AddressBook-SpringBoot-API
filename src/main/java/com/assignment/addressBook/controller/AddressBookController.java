package com.assignment.addressBook.controller;


import com.assignment.addressBook.handling.AddressBookErrorMessage;
import com.assignment.addressBook.model.AddressBookEntity;
import com.assignment.addressBook.service.AddressBookService;
import com.assignment.addressBook.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
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
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private AddressBookService addressBook;

    @Autowired
    private KafkaTemplate<String, Object> KafkaSender;

    private static String topic_rec_add = "ab_added";
    private static String topic_rec_update = "ab_updated";
    private static String topic_rec_delete = "ab_deleted";


    @GetMapping("/analytics")
    public String getAnalytics(){
        return kafkaConsumerService.toString();
    }

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
            AddressBookErrorMessage addressBookErrorMessage =  new AddressBookErrorMessage(fieldErrors);
            KafkaSender.send(topic_rec_add, addressBookErrorMessage);
            return addressBookErrorMessage;

        }
        Object saveOutput = addressBook.saveRecord(addressRecord);
        KafkaSender.send(topic_rec_add, saveOutput);
        return saveOutput;
    }

    @PostMapping("/saveAll")
    public Object AddRecords(@RequestBody List<AddressBookEntity> addressRecords) {
        Object getOut = addressBook.saveAll(addressRecords);
        KafkaSender.send(topic_rec_add, getOut);
        return getOut;
    }

    @PutMapping()
    public Object updateRecord(@RequestBody @Valid AddressBookEntity addressRecord, Errors errors) {
        if (errors.hasFieldErrors()) {
            List<FieldError> fieldErrors = errors.getFieldErrors();
            AddressBookErrorMessage addressBookErrorMessage = new AddressBookErrorMessage(fieldErrors);
            KafkaSender.send(topic_rec_update, addressBookErrorMessage);
            return addressBookErrorMessage;
        }
        Object getOut = addressBook.updateRecord(addressRecord);
        KafkaSender.send(topic_rec_update, getOut);
        return getOut;
    }

    @DeleteMapping("{param}")
    public Object deleteRecord(@PathVariable String param) {
        Object getOut = addressBook.delete(param);
        KafkaSender.send(topic_rec_delete, getOut);
        return getOut;
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
