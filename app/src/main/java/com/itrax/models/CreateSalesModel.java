package com.itrax.models;

/**
 * Created by Shankar on 12/24/2017.
 */

public class CreateSalesModel {
    private String coordinates;
    private String area;
    private String time;
    private String note;
    private String customer_name;
    private String customer_mobile;
    private String due_date;
    private String isotpverified;
    private String InitiatedDate;
    private String InitiatedTime;
    private String additional_info;

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

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getIsotpverified() {
        return isotpverified;
    }

    public void setIsotpverified(String isotpverified) {
        this.isotpverified = isotpverified;
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

    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }
}
