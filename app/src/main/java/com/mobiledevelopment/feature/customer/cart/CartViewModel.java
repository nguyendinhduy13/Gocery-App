package com.mobiledevelopment.feature.customer.cart;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartViewModel extends ViewModel {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String docId = "";

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
    private MutableLiveData<CartState> state;

    public MutableLiveData<CartState> getState() {
        if (state == null) {
            state = new MutableLiveData<>();
        }
        return state;
    }

    private MutableLiveData<CartTest> list;

    public MutableLiveData<CartTest> getList() {
        if (list == null) {
            list = new MutableLiveData<>();
        }
        return list;
    }

    public void getUnpaidCart()
    {
        db.collection("Cart")
                .whereEqualTo("idUser", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty())
                            {
                                Log.d("test", task.getResult().getDocuments().toString());
                                List<CartTest> listFromDb = new ArrayList<>();
                                listFromDb = task.getResult().toObjects(CartTest.class);
                                getList().postValue(listFromDb.get(0));

                                docId = task.getResult().getDocuments().get(0).getId();
                                Log.d("test", task.getResult().getDocuments().toString());
                            }
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateQuantity(int quantity, String id) {
        list.getValue().getCart().stream().filter(item -> item.getId() == id).findFirst().get().setQuantity(quantity);
    }

    public void updateDatabase() {
        db.collection("Cart")
                .document(docId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("Cart")
                                .add(list.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        state.postValue(CartState.DONE);
                                    }
                                });
                    }
                });
    }
}
