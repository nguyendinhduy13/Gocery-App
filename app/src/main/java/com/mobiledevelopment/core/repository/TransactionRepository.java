package com.mobiledevelopment.core.repository;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobiledevelopment.BuildConfig;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Dashboard;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Data;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationRequest;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationResult;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionRepository {
    public static String TAG = "TransactionRepository";
    private final NotificationApi notificationApi;

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Order");

    public TransactionRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        notificationApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.SEND_NOTIFICATION_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(NotificationApi.class);
    }

    public ListenerRegistration getTransactions(OnGetTransactionsCompleteListener onCompleteListener) {
        return collectionRef
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Dashboard> result = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        result.add(new Dashboard(
                                doc.getId(),
                                doc.get("status").toString(),
                                doc.get("idUser").toString(),
                                doc.getDouble("totalCost"),
                                StringFormatterUtils.format(doc.getDate("createdAt"))
                        ));
                    }

                    onCompleteListener.onComplete(result);
                });
    }

    public void sendNotication(Dashboard transaction, String token, String status, OnUpdateTransactionListener onUpdateListener) {
        Data data = new Data();
        String body = "";
        String title = "Gocery Notification";

        if(status.compareTo("Completed") == 0){
            body = "Your order has been completed";
        }else if(status.compareTo("Cancelled") == 0){
            body = "Your order has been cancelled";
        }else{
            body = "Your order is in progress";
        }

        data.setBody(body);
        data.setTitle(title);

        NotificationRequest notificationRequest = new NotificationRequest(data, token);

        collectionRef.document(transaction.getId()).update("status", status)
                .addOnSuccessListener(aVoid -> {
                    notificationApi.sendNotification(notificationRequest).enqueue(new Callback<NotificationResult>() {
                        @Override
                        public void onResponse(Call<NotificationResult> call, Response<NotificationResult> response) {
                            Log.d(TAG, "success");
                        }

                        @Override
                        public void onFailure(Call<NotificationResult> call, Throwable t) {
                            Log.d(TAG, t.getMessage());
                        }
                    });

                    Log.d(
                            TAG,
                            "updateTransaction() - transactionId: " + transaction.getId() + " DocumentSnapshot successfully written!")
                    ;
                    onUpdateListener.onComplete();
                })
                .addOnFailureListener(e -> {
                    onUpdateListener.onFailure(e.toString());
                    Log.w(TAG, "Error writing document", e);
                });


    }

    public interface OnGetTransactionsCompleteListener {
        void onComplete(List<Dashboard> transactions);
    }

    public interface OnUpdateTransactionListener {
        void onComplete();

        void onFailure(String errorMessage);
    }
}
