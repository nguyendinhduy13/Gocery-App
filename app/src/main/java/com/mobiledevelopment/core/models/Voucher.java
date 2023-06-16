package com.mobiledevelopment.core.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Voucher {
    public static Voucher undefinedVoucher = new Voucher(
            "Undefined",
            "Undefined",
            -1L,
            new Date(),
            "Undefined"
    );

    @DocumentId
    private final String id;
    private final String idUser;
    private final Long value;
    private final Date dateExpired;
    private final String description;


    // Need to have this for Firebase's toObject() method to work
    public Voucher() {
        this.id = undefinedVoucher.id;
        this.idUser = undefinedVoucher.idUser;
        this.value = undefinedVoucher.value;
        this.dateExpired = undefinedVoucher.dateExpired;
        this.description = undefinedVoucher.description;
    }


    public Voucher(
            String id,
            String idUser,
            Long value,
            Date expiredDate,
            String description) {
        this.id = id;
        this.idUser = idUser;
        this.value = value;
        this.dateExpired = expiredDate;
        this.description = description;
    }



    public Long getValue() {
        return value;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String toFullName() {
        return "Voucher " + value + "%";
    }

    @NonNull
    @Override
    public String toString() {
        return "id= " + getId() + "; expiredDate= " + dateExpired + "; idUser= " + idUser + "; value: " + value + "; description: " + description;
    }
}
