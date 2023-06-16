package com.mobiledevelopment.core.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobiledevelopment.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order implements Parcelable {
    public static Order undefinedOrder = new Order(
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            "Undefined",
            new HashMap<>(),
            -1L,
            -1L,
            "Undefined",
            Calendar.getInstance().getTime(),
            Calendar.getInstance().getTime());
    @DocumentId
    private final String id;
    private final String idUser;
    private final String email;
    private final String address;
    private final String idVoucher;
    private final Map<String, Long> amountOfProducts;
    private final Long totalCost;
    private final Long shippingCost;
    private final String status;
    private final Date createdAt;
    private final Date deliveryDate;

    // Need to have an empty constructor for Firebase's toObject() method to work
    public Order(
            String id,
            String idUser,
            String email, String address,
            String idVoucher,
            Map<String, Long> amountOfProducts,
            Long totalCost,
            Long shippingCost,
            String status,
            Date createdAt,
            Date deliverDate) {

        this.id = id;
        this.idUser = idUser;
        this.email = email;
        this.address = address;
        this.idVoucher = idVoucher;
        this.amountOfProducts = amountOfProducts;
        this.totalCost = totalCost;
        this.shippingCost = shippingCost;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveryDate = deliverDate;
    }
    public Order(
            String idUser,
            String email, String address,
            String idVoucher,
            Map<String, Long> amountOfProducts,
            Long totalCost,
            Long shippingCost,
            String status,
            Date createdAt,
            Date deliverDate) {
        this.email = email;
        this.id = "Undefined";
        this.idUser = idUser;
        this.address = address;
        this.idVoucher = idVoucher;
        this.amountOfProducts = amountOfProducts;
        this.totalCost = totalCost;
        this.shippingCost = shippingCost;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveryDate = deliverDate;
        }
    @SuppressWarnings("unchecked")
    public static Order from(QueryDocumentSnapshot document) {
        return new Order(
                document.getId(),
                document.getString("idUser"),
                document.getString("email"),
                document.getString("address"),
                document.getString("idVoucher"),
                (Map<String, Long>) document.get("amountOfProducts"),
                document.getLong("totalCost"),
                document.getLong("shippingCost"),
                document.getString("status"),
                document.getDate("createdAt"),
                document.getDate("deliveryDate"));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getAddress() {
        return address;
    }

    public String getIdVoucher() {
        return idVoucher;
    }

    public Map<String, Long> getAmountOfProducts() {
        return amountOfProducts;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public Long getShippingCost() {
        return shippingCost;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String getEmail() {
        return email;
    }

    @NonNull
    @Override
    public String toString() {
        return "id= " + getId() + "; idUser= " + getIdUser() + "; status = " + status;
    }

    public enum OrderStatus {
        WAITING_FOR_PAYMENT(
                "Waiting for Payment",
                R.color.waiting_payment_background,
                R.color.waiting_payment_text
        ),

        IN_PROGRESS(
                "In Progress",
                R.color.in_progress_background,
                R.color.in_progress_text),

        COMPLETED(
                "Completed",
                R.color.completed_background,
                R.color.completed_text
        ),

        CANCELLED(
                "Cancelled",
                R.color.cancelled_background,
                R.color.cancelled_text
        );

        public final String databaseFieldValue;
        public final int backgroundColorResId;
        public final int textColorResId;

        OrderStatus(String databaseFieldValue, int backgroundColorResId, int textColorResId) {
            this.databaseFieldValue = databaseFieldValue;
            this.backgroundColorResId = backgroundColorResId;
            this.textColorResId = textColorResId;
        }

        public static OrderStatus findByFieldValue(String databaseFieldValue) {
            for (OrderStatus status : values()) {
                if (status.databaseFieldValue.equals(databaseFieldValue)) {
                    return status;
                }
            }
            return null;
        }
    }
}
