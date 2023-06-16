package com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model;

public class NotificationRequest {
    private Data data;
    private String to;

    public NotificationRequest(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

