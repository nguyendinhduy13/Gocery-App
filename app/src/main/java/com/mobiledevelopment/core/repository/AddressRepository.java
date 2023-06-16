package com.mobiledevelopment.core.repository;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobiledevelopment.BuildConfig;
import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.core.models.address.District;
import com.mobiledevelopment.core.models.address.Province;
import com.mobiledevelopment.core.models.address.Ward;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressRepository {
    public static String TAG = "AddressRepository";
    private final VietnamAddressApi vietnamAddressApi;

    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Address");

    public AddressRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        vietnamAddressApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.VN_ADDRESS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(VietnamAddressApi.class);
    }


    public void getAddressById(
            String id, OnGetAddressCompleteListener onCompleteListener) {
        collectionRef.document(id).get().addOnCompleteListener(task -> {
            Address result = Address.undefinedAddress;

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "getAddressById() - id = " + id + "; Data:" + document.getData());
                    result = document.toObject(Address.class);
                } else {
                    Log.d(TAG, "getAddressById() - id = " + id + "; No such document");
                }
            }
            else {
                Log.d(
                        TAG,
                        "getAddressById() - id = " + id + "; Failed with exception:",
                        task.getException());
            }
            onCompleteListener.onComplete(result);
        });
    }

    public void getAddressesByUserId(
            String userId, OnGetAddressesCompleteListener onCompleteListener) {

        collectionRef.whereEqualTo("idUser", userId)
                .get().addOnCompleteListener(task -> {
                    List<Address> result = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            if (doc.get("idUser") != null) {
                                result.add(doc.toObject(Address.class));
                            }
                        }
                    }
                    else {
                        Log.d(
                                TAG,
                                "getAddressesByUserId() - userId = " + userId + "; Failed with exception:",
                                task.getException());
                    }

                    Log.d(TAG, "getAddressesByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }
    public ListenerRegistration getAddressesStreamByUserId(
            String userId, OnGetAddressesCompleteListener onCompleteListener) {

        return collectionRef.whereEqualTo("idUser", userId)
                .addSnapshotListener((querySnapshot, exception) -> {
                    List<Address> result = new ArrayList<>();

                    if (exception != null) {
                        Log.w(
                                TAG,
                                "getAllAddressesByAddressId() - Failed to add listener. Exception: ",
                                exception);
                        onCompleteListener.onComplete(result);
                        return;
                    }

                    assert querySnapshot != null;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.get("idUser") != null) {
                            result.add(doc.toObject(Address.class));
                        }
                    }
                    Log.d(TAG, "getAddressesStreamByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    public void addAddress(Address address, OnAddAddressCompleteListener onCompleteListener) {

        if (address.isPrimary()) {
            markAllOtherAddressesNonPrimary(address);
        }
        collectionRef.add(address).addOnSuccessListener(documentReference -> {
                    Log.d(
                            TAG,
                            "addAddress() - DocumentSnapshot written with ID: " + documentReference.getId());
                    onCompleteListener.onComplete(documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w(
                        TAG,
                        "addAddress() - Error adding document; Exception:",
                        e));
    }

    public void updateAddress(Address address, OnUpdateAddressCompleteListener onCompleteListener) {
        if (address.isPrimary()) {
            markAllOtherAddressesNonPrimary(address);
        }
        collectionRef.document(address.getId()).set(address)
                .addOnSuccessListener(aVoid -> {
                    Log.d(
                            TAG,
                            "updateAddress() - addressId: " + address.getId() + " DocumentSnapshot successfully written!")
                    ;
                    onCompleteListener.onComplete();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    public Call<List<Province>> getAllProvinces() {
        return vietnamAddressApi.getAllProvinces();
    }

    public Call<District.DistrictJsonWrapper> getDistrictsByProvinceCode(Integer provinceCode) {
        Log.d(TAG, "getDistrictsByProvinceCode() - code:" + provinceCode);
        return vietnamAddressApi.getDistrictsByProvinceCode(provinceCode);
    }

    public Call<Ward.WardJsonWrapper> getWardsByDistrictCode(Integer districtCode) {
        Log.d(TAG, "getWardsByDistrictCode() - code:" + districtCode);
        return vietnamAddressApi.getWardsByDistrictCode(districtCode);
    }

    private void markAllOtherAddressesNonPrimary(Address primaryAddress) {

        collectionRef.whereEqualTo("idUser", primaryAddress.getIdUser())
                .whereNotEqualTo(FieldPath.documentId(), primaryAddress.getId()).get()
                .addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            collectionRef.document(document.getId()).update("isPrimary", false);
                        }
                    }
                }));

    }


    public interface OnGetAddressesCompleteListener {
        void onComplete(List<Address> addresses);
    }

    public interface OnGetAddressCompleteListener {
        void onComplete(Address address);
    }

    public interface OnAddAddressCompleteListener {
        void onComplete(String addressId);
    }

    public interface OnUpdateAddressCompleteListener {
        void onComplete();
    }
}
