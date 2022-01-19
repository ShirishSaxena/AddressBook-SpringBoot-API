package com.assignment.addressBook.handling;

import java.sql.Timestamp;

public class AddressBookSuccessMessage {
    private String status = "success";

    private Long totalRecords;

    private Float requestPerSecond;
    private Float totalTime_Seconds;

    private String comment;

    public AddressBookSuccessMessage(Long totalRecords, Long startTime, Long endTime, String comment){
        this.totalRecords = totalRecords;
        this.totalTime_Seconds = (Float.valueOf(endTime - startTime) / 1000);
        this.requestPerSecond = totalRecords / totalTime_Seconds;
        this.comment = comment;
    }

    public AddressBookSuccessMessage(String status, Long totalRecords, Long startTime, Long endTime, String comment){
        this.status = status;
        this.totalRecords = totalRecords;
        this.totalTime_Seconds = (Float.valueOf(endTime - startTime) / 1000);
        this.requestPerSecond = totalRecords / totalTime_Seconds;
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Float getRequestPerSecond() {
        return requestPerSecond;
    }

    public void setRequestPerSecond(Float requestPerSecond) {
        this.requestPerSecond = requestPerSecond;
    }

    public Float getTotalTime_Seconds() {
        return totalTime_Seconds;
    }

    public void setTotalTime_Seconds(Float totalTime_Seconds) {
        this.totalTime_Seconds = totalTime_Seconds;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
