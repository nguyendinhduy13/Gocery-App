package com.mobiledevelopment.feature.customer.order;

import com.mobiledevelopment.core.models.Product.*;

import java.util.List;

public interface OnGetProductsInOrderListener {
    void onComplete(List<ProductInCart> products);
}
