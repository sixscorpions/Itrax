package com.itrax.models;

/**
 * Created by Shankar on 12/21/2017.
 */

public class CreateSalesDataModel {
    private String coordinates;
    private String area;
    private String time;
    private String note;
    private String customername;
    private String customermobile;
    private String duedate;
    private String IsOtpVerified;
    private String InitiatedDate;
    private String InitiatedTime;

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomermobile() {
        return customermobile;
    }

    public void setCustomermobile(String customermobile) {
        this.customermobile = customermobile;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getIsOtpVerified() {
        return IsOtpVerified;
    }

    public void setIsOtpVerified(String isOtpVerified) {
        IsOtpVerified = isOtpVerified;
    }

    public String getInitiatedDate() {
        return InitiatedDate;
    }

    public void setInitiatedDate(String initiatedDate) {
        InitiatedDate = initiatedDate;
    }

    public String getInitiatedTime() {
        return InitiatedTime;
    }

    public void setInitiatedTime(String initiatedTime) {
        InitiatedTime = initiatedTime;
    }
}
