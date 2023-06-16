package com.mobiledevelopment.core.repository;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobiledevelopment.BuildConfig;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.models.Voucher;
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

public class VoucherRepository {
    public static String TAG = "VoucherRepository";
    private final NotificationApi notificationApi;

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Voucher");

    public VoucherRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        notificationApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.SEND_NOTIFICATION_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(NotificationApi.class);
    }

    public ListenerRegistration getVouchersStreamByUserId(
            String userId,
            VoucherRepository.OnGetVouchersCompleteListener onCompleteListener) {
        return collectionRef
                .whereEqualTo("idUser", userId)
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Voucher> result = new ArrayList<>();

                    if (exception != null) {
                        Log.w(
                                TAG,
                                "getAllVouchersByUserId() - Failed to add listener. Exception: ",
                                exception);
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.get("idUser") != null) {
                            result.add(doc.toObject(Voucher.class));
                        }
                    }
                    Log.d(TAG, "getVouchersStreamByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    public void getVouchersByUserId(
            String userId,
            VoucherRepository.OnGetVouchersCompleteListener onCompleteListener) {
        collectionRef
                .whereEqualTo("idUser", userId)
                .get().addOnCompleteListener(task -> {
                    List<Voucher> result = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            if (doc.get("idUser") != null) {
                                result.add(doc.toObject(Voucher.class));
                            }
                        }
                    }
                    else {
                        Log.d(
                                TAG,
                                "getVouchersByUserId() - userId = " + userId + "; Failed with exception:",
                                task.getException());
                    }

                    Log.d(TAG, "getVouchersByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    public void sendNotication(String token) {
        Data data = new Data();
        String body = "A new voucher has been added";
        String title = "Gocery Notification";

        data.setBody(body);
        data.setTitle(title);

        NotificationRequest notificationRequest = new NotificationRequest(data, token);
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

    }

    public void addVoucher(VoucherRepository.OnAddVouchersCompleteListener onVoucherListener,
                           Voucher voucher) {
        try {
            collectionRef.add(voucher)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(
                                TAG,
                                "addVoucher() - DocumentSnapshot written with ID: " + documentReference.getId());
                        onVoucherListener.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                onVoucherListener.onError(e.getMessage());

                                Log.w(
                                        TAG,
                                        "addVoucher() - Error adding document; Exception:",
                                        e);
                            });
                }
        catch (Exception e){

        }
    }

    public interface OnGetVouchersCompleteListener {
        void onComplete(List<Voucher> vouchers);
    }

    public interface OnAddVouchersCompleteListener {
        void onComplete();
        void onError(String errorMessage);
    }
}