package com.mobiledevelopment.core.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CartRepository {
    public static String TAG = "CartRepository";
    private final CollectionReference collectionRef = FirebaseFirestore.getInstance()
            .collection("Cart");

    public ListenerRegistration getCartStreamByUserId(
            String idUser, CartRepository.OnGetCartCompleteListener onCompleteListener) {
        return collectionRef.whereEqualTo("idUser", idUser)
                .addSnapshotListener((querySnapshot, exception) -> {
                    CartTest result = new CartTest();
                    if (exception != null) {
                        Log.e(
                                TAG,
                                "getCartStreamByUserId() - Failed to add listener. Exception: ",
                                exception);
                        onCompleteListener.onComplete(result);
                        return;
                    }
                    assert querySnapshot != null;
                    if (querySnapshot.getDocuments().size() > 0) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Log.d(
                                TAG,
                                "getCartStreamByUserId() - idUser = " + idUser + "; Data:" + document.getData());
                        result = document.toObject(CartTest.class);
                    }
                    else {
                        Log.d(
                                TAG,
                                "getCartByUserId() - idUser = " + idUser + "; No such document");
                    }
                    Log.d(TAG, "getCartStreamByUserId() - result: " + result);
                    onCompleteListener.onComplete(result);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addProductToCart(
            String idUser,
            Product.ProductInCart productToAdd,
            CartRepository.OnAddProductToCartCompleteListener onCompleteListener) {
        collectionRef.whereEqualTo("idUser", idUser).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                if (task.getResult().getDocuments().size() > 0) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    CartTest currentCart = document.toObject(CartTest.class);
                    assert currentCart != null;

                    List<Product.ProductInCart> newProductsList = new ArrayList<>();

                    boolean isProductToAddInCart = false;
                    for (Product.ProductInCart productInCart : currentCart.getCart()) {
                        if (Objects.equals(
                                productInCart.getId(),
                                productToAdd.getProduct().getId())) {
                            isProductToAddInCart = true;

                            Integer newQuantity = productInCart.getQuantity() + productToAdd.getQuantity();

                            newProductsList.add(new Product.ProductInCart(
                                    productToAdd.getProduct(),
                                    newQuantity));
                        }
                        else {
                            newProductsList.add(productInCart);
                        }
                    }
                    if (!isProductToAddInCart) {
                        newProductsList.add(productToAdd);
                    }

                    Log.d(TAG, "getCartByUserId() - newProductsList: " + newProductsList);
                    collectionRef.document(document.getId()).set(
                            new CartTest("", idUser, newProductsList));
                }
                else {
                    collectionRef.add(new CartTest(
                            "",
                            idUser,
                            Collections.singletonList(productToAdd)));
                    Log.d(TAG, "addProductToCart() - idUser = " + idUser + "; No such document");
                }
            }
            else {
                Log.d(
                        TAG,
                        "addProductToCart() - idUser = " + idUser + "; Failed with exception:",
                        task.getException());
            }
            onCompleteListener.onComplete();
        });
    }


    public interface OnGetCartCompleteListener {
        void onComplete(CartTest cart);
    }

    public interface OnAddProductToCartCompleteListener {
        void onComplete();
    }

}
