package com.mobiledevelopment.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.util.Date;


public class Transaction implements Parcelable {
    @DocumentId

    private String id;
    private Date date;
    private String user_name;
    private String progress;
    private Double total_payment;

    public Transaction(String id, Date date, String user_name, String progress, Double total_payment) {
        this.id = id;
        this.date = date;
        this.user_name = user_name;
        this.progress = progress;
        this.total_payment = total_payment;
    }


    protected Transaction(Parcel in) {
        id = in.readString();
        user_name = in.readString();
        progress = in.readString();
        if (in.readByte() == 0) {
            total_payment = null;
        } else {
            total_payment = in.readDouble();
        }
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser_name() {
        return user_name;
    }

    public static Transaction from(QueryDocumentSnapshot document) {
        return new Transaction(
                document.getId(),
                document.getDate("date"),
                document.getString("user_name"),
                document.getString("progress"),
                document.getDouble("total_payment")
        );

    }


    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Double getTotal_payment() {
        return total_payment;
    }

    public void setTotal_payment(Double total_payment) {
        this.total_payment = total_payment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_name);
        dest.writeString(progress);
        if (total_payment == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total_payment);
        }
    }
}
