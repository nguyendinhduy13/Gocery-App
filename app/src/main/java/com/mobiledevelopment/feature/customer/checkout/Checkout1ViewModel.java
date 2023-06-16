package com.mobiledevelopment.feature.customer.checkout;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mobiledevelopment.BuildConfig;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.core.repository.AddressRepository;
import com.mobiledevelopment.core.repository.NotificationApi;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.core.repository.VoucherRepository;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Data;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationRequest;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationResult;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Checkout1ViewModel extends ViewModel {

    MutableLiveData<String> orderTotalLiveData = new MutableLiveData<>();
    MutableLiveData<String> deliveryFeeLiveData = new MutableLiveData<>();
    MutableLiveData<String> totalPaymentLiveData = new MutableLiveData<>();
    MutableLiveData<String> destinationLiveData = new MutableLiveData<>();

    MutableLiveData<Voucher> voucherLiveData = new MutableLiveData<>(Voucher.undefinedVoucher);
    List<Address> allAddresses;
    List<Voucher> allVouchers;

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    String PaymentMethod;
    Long totalPayment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    AddressRepository addressRepository = new AddressRepository();
    VoucherRepository voucherRepository = new VoucherRepository();
    UserRepository userRepository = new UserRepository();

    private NotificationApi notificationApi;

    private MutableLiveData<CartTest> list;

    public MutableLiveData<CartTest> getList() {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        return list;
    }

    public Checkout1ViewModel() {

    }

    void initValue(Bundle bundle) {
        String orderTotal = bundle.getString("total_cost");
        orderTotalLiveData.setValue(orderTotal);
        deliveryFeeLiveData.setValue("5 ₫");

        totalPayment = StringFormatterUtils.VNDToLong(orderTotalLiveData.getValue())
                + StringFormatterUtils.VNDToLong(deliveryFeeLiveData.getValue());

        totalPaymentLiveData.setValue(StringFormatterUtils.toCurrency(totalPayment));
        addressRepository.getAddressesByUserId(
                Objects.requireNonNull(auth.getCurrentUser()).getUid(),
                addresses -> {
                    allAddresses = addresses;
                    Log.d("Checkout1ViewModel", "allAddresses" + addresses);

                });
        voucherRepository.getVouchersByUserId(
                Objects.requireNonNull(auth.getCurrentUser()).getUid(),
                vouchers -> {
                    allVouchers = vouchers;
                    Log.d("Checkout1ViewModel", "allVouchers" + allVouchers);

                });

        db.collection("Address")
                .whereEqualTo("isPrimary", true)
                .whereEqualTo("idUser", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            destinationLiveData.postValue(Address.toNormalizedString(document.getString(
                                    "detailAddress")));
                        }
                    }
                });
    }

    public void updateCart(String id) {
        if (!id.isEmpty()) {
            db.collection("Cart")
                    .document(id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                        CartTest listFromDb = documentSnapshot.toObject(CartTest.class);
                        getList().postValue(listFromDb);

                        Map<String, Long> products = new HashMap<>();

                        for (Product.ProductInCart product : listFromDb.getCart()) {
                            products.put(product.getId(), product.getQuantity().longValue());
                        }
                        String voucherId = "";
                        if (voucherLiveData.getValue() != null) {
                            voucherId = voucherLiveData.getValue().getId();
                        }
                        Order newOrder = new Order(
                                auth.getCurrentUser().getUid(),
                                auth.getCurrentUser().getEmail(),
                                destinationLiveData.getValue(),
                                voucherId,
                                products,
                                StringFormatterUtils.VNDToLong(totalPaymentLiveData.getValue()),
                                StringFormatterUtils.VNDToLong(deliveryFeeLiveData.getValue()),
                                "In Progress",
                                Calendar.getInstance().getTime(),
                                Calendar.getInstance().getTime());
                        db.collection("Order").add(newOrder)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        userRepository.getUserToken("0PpvpSm2N1MuAdfZ7Y7qlFr29qE2", new UserRepository.OnGetUserTokenCompleteListener() {
                                            @Override
                                            public void onComplete(String token) {
                                                sendNotication(token);
                                            }
                                        });
                                    }
                                });


                        db.collection("Cart")
                                .document(id).set(
                                        Collections.singletonMap("cart", Collections.emptyList()),
                                        SetOptions.merge());
                    });

        }
    }

    private void sendNotication(String token) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        notificationApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.SEND_NOTIFICATION_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(NotificationApi.class);

        Data data = new Data();
        String body = "A new order has been added";
        String title = "Gocery Notification";

        data.setBody(body);
        data.setTitle(title);

        NotificationRequest notificationRequest = new NotificationRequest(data, token);
        notificationApi.sendNotification(notificationRequest).enqueue(new Callback<NotificationResult>() {
            @Override
            public void onResponse(Call<NotificationResult> call, Response<NotificationResult> response) {
            }

            @Override
            public void onFailure(Call<NotificationResult> call, Throwable t) {
            }
        });

    }

    public void setVoucher(Voucher voucher) {
        voucherLiveData.setValue(voucher);
        totalPayment = (long) (totalPayment * ((100.0 - voucher.getValue()) / 100.0));
        totalPaymentLiveData.setValue(StringFormatterUtils.toCurrency(totalPayment));
    }
}
