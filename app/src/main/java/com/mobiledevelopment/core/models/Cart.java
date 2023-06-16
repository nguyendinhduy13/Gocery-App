package com.mobiledevelopment.core.models;

import com.google.firebase.firestore.DocumentId;

import java.util.Map;

public class Cart {
    private String idCart;
    private String idUser;
    private Map<Product, Integer> cart;

    public Cart(){}

    public Cart(String idCart, String idUser, Map<Product, Integer> cart) {
        this.idCart = idCart;
        this.idUser = idUser;
        this.cart = cart;
    }

    public String getIdCart() {
        return idCart;
    }

    public void setIdCart(String idCart) {
        this.idCart = idCart;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Map<Product, Integer> getCart() {
        return cart;
    }

    public void setCart(Map<Product, Integer> cart) {
        this.cart = cart;
    }
}
