package com.mobiledevelopment.feature.admin.ui.statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;
import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.OrderRepository;
import com.mobiledevelopment.core.repository.StatisticRepository;
import com.mobiledevelopment.feature.customer.shop.CategoryViewModel;

import java.util.List;

public class StatisticViewModel extends ViewModel {
    private MutableLiveData<List<Order>> _orderLiveData;
    private OrderRepository _orderRepository;

    public StatisticViewModel(){
        _orderLiveData = new MutableLiveData<>();
        _orderRepository = new OrderRepository();
        // get data from firebase store
        initData();
    }

    public MutableLiveData<List<Order>> get_orderLiveData() {
        return _orderLiveData;
    }

    void initData(){
        _orderRepository.getOrders(new OrderRepository.OnGetOrdersCompleteListener() {
            @Override
            public void onComplete(List<Order> addresses) {
                _orderLiveData.setValue(addresses);
            }
        });
    }
    public static class OrderViewModelFactory implements ViewModelProvider.Factory {
        private final OrderRepository orderRepository;

        public OrderViewModelFactory(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new StatisticViewModel();
        }
    }
}
