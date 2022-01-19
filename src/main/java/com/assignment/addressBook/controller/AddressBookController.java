package com.assignment.addressBook.controller;


import com.assignment.addressBook.model.AddressBookEntity;
import com.assignment.addressBook.handling.AddressBookErrorMessage;
import com.assignment.addressBook.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(path = "/api")

public class AddressBookController {

    @Autowired
    private AddressBookService addressBook;

    @GetMapping("/getAll")
    public List<AddressBookEntity> getAllRecords(){
        return addressBook.getAll();
    }

    @GetMapping("/get/{param}")
    public AddressBookEntity getByIdOrEmail(@PathVariable String param){
        return addressBook.getByIdOrEmail(param);
    }

    @PostMapping
    public Object AddRecord (@RequestBody @Valid AddressBookEntity addressRecord, Errors errors) {
        if (errors.hasFieldErrors()) {
            List<FieldError> fieldErrors = errors.getFieldErrors();
            return new AddressBookErrorMessage(fieldErrors);
        }
        return addressBook.saveRecord(addressRecord);
    }

    @PostMapping("/saveAll")
    public Object AddRecords(@RequestBody List<AddressBookEntity> addressRecords){
        return addressBook.saveAll(addressRecords);
    }

    @PutMapping()
    public Object updateRecord (@RequestBody @Valid AddressBookEntity addressRecord, Errors errors) {
        if (errors.hasFieldErrors()) {
            List<FieldError> fieldErrors = errors.getFieldErrors();
            return new AddressBookErrorMessage(fieldErrors);
        }

        return addressBook.updateRecord(addressRecord);
    }

    @DeleteMapping("{param}")
    public Object deleteRecord(@PathVariable String param){
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
