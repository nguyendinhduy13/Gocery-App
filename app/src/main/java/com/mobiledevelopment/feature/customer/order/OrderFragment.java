package com.mobiledevelopment.feature.customer.order;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.OrderRepository;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.databinding.FragmentOrderBinding;
import com.mobiledevelopment.databinding.FragmentOrderListBinding;
import com.mobiledevelopment.feature.customer.CustomerActivityViewModel;
import com.mobiledevelopment.feature.customer.profile.voucher.VoucherDetailFragment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFragment extends Fragment {

    public final String TAG = "OrderFragment";
    OrderListPagerAdapter orderListPagerAdapter;
    ViewPager2 viewPager;
    private OrderViewModel viewModel;
    private FragmentOrderBinding binding;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new OrderViewModel.OrderViewModelFactory(
                        new OrderRepository(),
                        new ProductRepository())).get(
                OrderViewModel.class);

        CustomerActivityViewModel customerActivityViewModel =
                new ViewModelProvider(requireActivity()).get(CustomerActivityViewModel.class);

        customerActivityViewModel.getCurrentUserId()
                .observe(getViewLifecycleOwner(), userId -> viewModel.setCurrentUserId(userId));

        setUpUi();
    }

    private void setUpUi() {
        orderListPagerAdapter = new OrderListPagerAdapter(
                this,
                Order.OrderStatus.values());
        viewPager = binding.pager;
        viewPager.setAdapter(orderListPagerAdapter);

        TabLayout tabLayout = binding.tabLayout;
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(Order.OrderStatus.values()[position].databaseFieldValue)
        ).attach();
        Log.d(
                TAG,
                "viewModel.userOrders.observe() - setUpUi: " + Arrays.toString(Order.OrderStatus.values()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        viewModel.clearSnapshotListeners();
    }

    public static class OrderListPagerAdapter extends FragmentStateAdapter {
        private final Order.OrderStatus[] statuses;

        public OrderListPagerAdapter(
                Fragment fragment,
                Order.OrderStatus[] statuses) {
            super(fragment);
            this.statuses = statuses;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = new OrdersGroupByStatusFragment();
            Bundle args = new Bundle();
            args.putString(
                    OrdersGroupByStatusFragment.ARG_ORDER_STATUS,
                    statuses[position].databaseFieldValue);
            Log.d(
                    "OrderListPagerAdapter",
                    "createFragment() - status: " + statuses[position].databaseFieldValue);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return statuses.length;
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class OrdersGroupByStatusFragment extends Fragment {
        public static final String ARG_ORDER_STATUS = "Undefined";
        private FragmentOrderListBinding binding;

        private OrderAdapter orderAdapter;
        private OrderViewModel viewModel;

        private String orderStatus;

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            binding = FragmentOrderListBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            assert args != null;
            orderStatus = args.getString(ARG_ORDER_STATUS);

            viewModel = new ViewModelProvider(
                    requireParentFragment()).get(
                    OrderViewModel.class);

            setUpUi();
            observeViewModel();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void setUpUi() {
            orderAdapter = new OrderAdapter(requireContext(), (order) -> {
                viewModel.getProductsInOrder(order, products -> {
                    new OrderDetailBottomSheet(order, products).show(
                            getChildFragmentManager(),
                            OrderDetailBottomSheet.TAG);
                });
            });
            binding.recyclerviewOrders.setAdapter(orderAdapter);
            binding.recyclerviewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.buttonShopNow.setOnClickListener((view) -> {
                Navigation.findNavController(requireView())
                        .navigate(OrderFragmentDirections.actionOrderFragmentToShopFragment());
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void observeViewModel() {
            viewModel.getOrdersGroupedByStatus(orderStatus)
                    .observe(getViewLifecycleOwner(), orders -> {
                        orderAdapter.submitList(orders);
                        if (orders.size() > 0) {
                            showOrders();
                        }
                        else {
                            showEmptyScreen();
                        }

                        //Log.d(
                        //        "OrdersStatusFragment",
                        //        "getOrdersGroupedByStatus() - status: " + orderStatus + "; orders = " + orders);
                    });
        }

        private void showEmptyScreen() {
            binding.recyclerviewOrders.setVisibility(View.GONE);
            binding.linearLayoutEmptyOrders.setVisibility(View.VISIBLE);
        }

        private void showOrders() {
            binding.recyclerviewOrders.setVisibility(View.VISIBLE);
            binding.linearLayoutEmptyOrders.setVisibility(View.GONE);
        }
    }
}

