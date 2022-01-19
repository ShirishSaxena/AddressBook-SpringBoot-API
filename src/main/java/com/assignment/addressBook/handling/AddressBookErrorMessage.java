package com.assignment.addressBook.handling;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddressBookErrorMessage {

    private String status = "failed";
    private String comment = "N/A";
    private HashMap<String, String> errors;

    public AddressBookErrorMessage(List<FieldError> fieldErrorList) {
        HashMap<String, String> map = new HashMap<>();
        int err_count = 0;
        for(FieldError fieldError : fieldErrorList){
            String[] splitError = fieldError.getDefaultMessage().split(" : ");
            if(!map.containsKey(splitError[0])){
                map.put(splitError[0], splitError[1]);
                err_count++;
            }
        }

        this.errors = map;
        this.comment = err_count + " errors found.";
    }

    public AddressBookErrorMessage(int size, List<String> errorMsgs) {
        HashMap<String, String> map = new HashMap<>();
        int err_count = 0;
        this.comment = comment;
        for(String errorMsg : errorMsgs){
            String[] splitMsg = errorMsg.split(" : ");
            if(!map.containsKey(splitMsg[0])){
                map.put(splitMsg[0], splitMsg[1]);
                err_count++;
            }
        }
        this.comment = err_count + " errors found.";
        this.errors = map;
    }

    public AddressBookErrorMessage(int recordNo, int size, List<String> errorMsgs) {
        HashMap<String, String> map = new HashMap<>();
        int err_count = 0;
        this.comment = comment;
        for(String errorMsg : errorMsgs){
            String[] splitMsg = errorMsg.split(" : ");
            if(!map.containsKey(splitMsg[0])){
                map.put(splitMsg[0], splitMsg[1]);
                err_count++;
            }
        }
        this.comment = err_count + " errors found.";
        this.errors = map;
        this.status = "failed (recordNo -> " + recordNo + ")";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
