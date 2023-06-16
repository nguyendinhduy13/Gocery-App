package com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model;

public class Dashboard {
    private String id;
    private String status;
    private String userName;
    private Double totalPayment;
    private String date;

    public Dashboard(String id, String status, String userName, Double totalPayment, String date) {
        this.id = id;
        this.status = status;
        this.userName = userName;
        this.totalPayment = totalPayment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
