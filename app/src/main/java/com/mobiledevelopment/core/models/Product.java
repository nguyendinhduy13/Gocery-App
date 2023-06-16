package com.mobiledevelopment.core.models;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Product {
    @DocumentId
    private String id;
    private String name;
    private String idCategory;
    private List<String> images;
    private double discount;
    private double price;
    private double quantity;
    private String unit;
    private String description;

    public Product() {}

    public Product(
            String id, String name, String idCategory, List<String> images,
            double discount, double price, double quantity, String unit, String description) {
        this.id = id;
        this.name = name;
        this.idCategory = idCategory;
        this.images = images;
        this.discount = discount;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This class wraps around a Product object and has a "quantity" field to indicate
     * the amount of the the wrapped product in a cart
     */
    public static class ProductInCart {
        /**
         * A ProductInCart object is stored as a Map in firestore, not as a document,
         * so Product's "id" field won't be parsed since it's annotated with @DocumentId
         */
        private String id = "Undefined product id";
        private final Product product;
        private Integer quantity = 0;

        @SuppressWarnings("ConstantConditions")
        @RequiresApi(api = Build.VERSION_CODES.N)
        public static List<ProductInCart> from(
                List<Product> productsInfo,
                Map<String, Long> amountOfProducts) {
            List<ProductInCart> result = new ArrayList<>();
            for (Product product : productsInfo) {
                result.add(
                        new ProductInCart(
                                product,
                                amountOfProducts.getOrDefault(product.getId(), 1L).intValue()
                        )
                );
            }
            return result;
        }

        public ProductInCart() {
            product = new Product();
        }

        public ProductInCart(Product product, Integer quantity) {
            this.id = product.getId();
            this.product = product;
            this.quantity = quantity;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Product getProduct() {
            return product;
        }


        public void setId(String id) {
            this.id = id;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        @NonNull
        @Override
        public String toString() {
            return "productId: " + product.getId() + "; productName: " + product.getName() + "; quantity in cart:" + quantity;
        }

        public String getId() {
            return id;
        }
    }
}
