package com.mobiledevelopment.core.repository;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public static String TAG = "OrderRepository";

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Order");

    public ListenerRegistration getOrdersStreamByUserId(
            String userId, OrderRepository.OnGetOrdersCompleteListener onCompleteListener) {

        return collectionRef.whereEqualTo("idUser", userId)
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Order> result = new ArrayList<>();

                    if (exception != null) {
                        Log.w(
                                TAG,
                                "getOrdersStreamByUserId() - Failed to add listener. Exception: ",
                                exception);
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.get("idUser") != null) {
                            result.add(Order.from(doc));
                        }
                    }
                    Log.d(TAG, "getOrdersStreamByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    public ListenerRegistration getOrders(OrderRepository.OnGetOrdersCompleteListener onCompleteListener) {

        return collectionRef
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Order> result = new ArrayList<>();

                    if (exception != null) {
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        result.add(Order.from(doc));
                    }
                    Log.d("ddd",result+"");
                    onCompleteListener.onComplete(result);
                });
    }


    public interface OnGetOrdersCompleteListener {
        void onComplete(List<Order> addresses);
    }
}
