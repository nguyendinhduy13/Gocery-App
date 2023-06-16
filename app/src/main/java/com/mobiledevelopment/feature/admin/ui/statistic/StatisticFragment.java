package com.mobiledevelopment.feature.admin.ui.statistic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.OrderRepository;
import com.mobiledevelopment.core.repository.StatisticRepository;
import com.mobiledevelopment.databinding.FragmentStatisticBinding;
import com.mobiledevelopment.feature.customer.shop.CategoryViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class StatisticFragment extends Fragment {
    private FragmentStatisticBinding binding;
    private StatisticViewModel statisticViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statisticViewModel = new ViewModelProvider(
                this,
                new StatisticViewModel.OrderViewModelFactory(new OrderRepository())).get(StatisticViewModel.class);
        observeViewModel();
        onClickListener();
    }

    private void onClickListener(){
        binding.btnViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chart_day", (ArrayList<? extends Parcelable>) new ArrayList<>(statisticViewModel.get_orderLiveData().getValue()));
                Navigation.findNavController(view).navigate(R.id.action_navigation_statistic_to_navigation_chart_day,bundle);
            }
        });

        binding.btnViewMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chart_month", (ArrayList<? extends Parcelable>) new ArrayList<>(statisticViewModel.get_orderLiveData().getValue()));
                Navigation.findNavController(view).navigate(R.id.action_navigation_statistic_to_navigation_chart_month,bundle);
            }
        });

        binding.btnViewYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chart_year", (ArrayList<? extends Parcelable>) new ArrayList<>(statisticViewModel.get_orderLiveData().getValue()));
                Navigation.findNavController(view).navigate(R.id.action_navigation_statistic_to_navigatiton_chart_year,bundle);
            }
        });
    }

    private void observeViewModel() {
       statisticViewModel.get_orderLiveData().observe(getViewLifecycleOwner(),(statistic)->{
           int size=statistic.size();
           binding.amountTransaction.setText(String.valueOf(size));
           int inprogress=0;
           int finished=0;
           int cancelled=0;
           Long total_revenue= Long.valueOf(0);
           HashSet<String> set = new HashSet<String>();
           Long amountofproduct= Long.valueOf(0);
           int amoutofUser=0;
           for (int i = 0; i < statistic.size(); i++) {
               Order item = statistic.get(i);
               total_revenue=total_revenue+item.getTotalCost();
               if(item.getStatus().equals("In progress")){
                   inprogress+=1;
               }
               if(item.getStatus().equals("Cancelled")){
                   cancelled+=1;
               }
               if(item.getStatus().equals("Finished")){
                   finished+=1;
               }
               set.add(item.getIdUser());

               Map<String, Long> amountOfProducts = item.getAmountOfProducts();
               for (Map.Entry<String, Long> entry : amountOfProducts.entrySet()) {
                   Long productAmount = entry.getValue();
                   amountofproduct+=productAmount;
               }
           }
           amoutofUser=set.size();
           binding.shopRevenue.setText(total_revenue.toString());
           binding.inprogressAmount.setText(String.valueOf(inprogress));
           binding.cancelledAmount.setText(String.valueOf(cancelled));
           binding.finishedAmount.setText(String.valueOf(finished));
           binding.amountProducts.setText(String.valueOf(amountofproduct));
           binding.userAmountStatistic.setText(String.valueOf(amoutofUser));
       });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}