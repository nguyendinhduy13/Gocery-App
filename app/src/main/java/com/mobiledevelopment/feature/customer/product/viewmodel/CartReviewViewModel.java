package com.mobiledevelopment.feature.customer.product.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobiledevelopment.core.models.CartTest;

import java.util.ArrayList;
import java.util.List;

public class CartReviewViewModel extends ViewModel {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String docId = "";

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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
}
