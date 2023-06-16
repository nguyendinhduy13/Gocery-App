package com.mobiledevelopment.core.models;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class CartTest {
    @DocumentId
    private String idCart;
    private String idUser;
    private List<Product.ProductInCart> cart = new ArrayList<>();

    public CartTest() {}

    public CartTest(String idCart, String idUser, List<Product.ProductInCart> cart) {
        this.idCart = idCart;
        this.idUser = idUser;
        this.cart = cart;
    }


    public String getIdUser() {
        return idUser;
    }

    public List<Product.ProductInCart> getCart() {
        return cart;
    }

}
