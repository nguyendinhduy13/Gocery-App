package com.mobiledevelopment.core.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobiledevelopment.core.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class StatisticRepository {
    public static String TAG = "StatisticRepository";

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Transaction");


    public ListenerRegistration getTransactions(
            StatisticRepository.OnGetTransactionCompleteListener onCompleteListener) {
        return collectionRef
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Transaction> result = new ArrayList<>();

                    if (exception != null) {
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        result.add(Transaction.from(doc));
                    }
                    onCompleteListener.onComplete(result);
                });
    }

    public interface OnGetTransactionCompleteListener {
        void onComplete(List<Transaction> transactions);
    }
}
