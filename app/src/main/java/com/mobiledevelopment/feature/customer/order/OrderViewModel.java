package com.mobiledevelopment.feature.customer.order;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.OrderRepository;
import com.mobiledevelopment.core.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderViewModel extends ViewModel {
    public static final String TAG = "OrderViewModel";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MutableLiveData<String> _currentUserId = new MutableLiveData<>("");

    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public OrderViewModel(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<List<Order>> getOrdersGroupedByStatus(String status) {
        return Transformations.switchMap(_orders, orders -> {
                    List<Order> result = orders.stream()
                            .filter(order -> order.getStatus().contentEquals(status))
                            .collect(Collectors.toList());
                    //Log.d(TAG, "getOrdersGroupedByStatus() - status: " + status + "; orders[input]: " + orders + ";  orders[output]:" + result);
                    return new MutableLiveData<>(result);
                }
        );
    }

    public String getCurrentUserId() {
        return _currentUserId.getValue();
    }

    public void setCurrentUserId(String id) {
        if (!Objects.equals(_currentUserId.getValue(), id)) {
            _currentUserId.setValue(id);
            resetOrders();
        }
        Log.d(
                TAG,
                "setCurrentUserId() - inputId: " + id + "; _currentUserId.value = " + _currentUserId.getValue());
    }

    private void resetOrders() {
        ListenerRegistration snapshotRegistration = orderRepository.getOrdersStreamByUserId(
                _currentUserId.getValue(),
                _orders::setValue);
        snapshotListeners.add(snapshotRegistration);

        Log.d(
                TAG,
                "resetOrders() - _orders.value = " + _orders.getValue());
    }

    public void clearSnapshotListeners() {
        Log.d(
                TAG,
                "clearSnapshotListeners() - snapshotListeners.size = " + snapshotListeners.size());
        for (ListenerRegistration listener : snapshotListeners) {
            listener.remove();
        }
        snapshotListeners.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getProductsInOrder(
            Order order,
            OnGetProductsInOrderListener completeListener) {
        productRepository.getProductsByIds(
                new ArrayList<>(order.getAmountOfProducts().keySet()),
                products -> {
                    Log.d("OrderViewModel",
                            "getProductsInOrder() - order: " + order + ", products: " + products);
                    completeListener.onComplete(Product.ProductInCart.from(
                            products,
                            order.getAmountOfProducts()));
                });
    }

    public static class OrderViewModelFactory implements ViewModelProvider.Factory {
        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;

        public OrderViewModelFactory(
                OrderRepository orderRepository,
                ProductRepository productRepository) {
            this.orderRepository = orderRepository;
            this.productRepository = productRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            @SuppressWarnings("unchecked")
            T t = (T) new OrderViewModel(orderRepository, productRepository);
            return t;
        }
    }
}
