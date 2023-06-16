package com.mobiledevelopment.feature.admin.ui.statistic.chart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;
import com.mobiledevelopment.databinding.FragmentChartMonthBinding;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class chartMonthFragment extends Fragment {
    FragmentChartMonthBinding binding;
    ChartViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChartMonthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel=new ChartViewModel();
        onClickListener();
        onItemSelected();
        observeViewModel();
        addDataSpinner();
        addDataInChart();
    }

    public void onClickListener(){
        binding.arrowBackChartMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private void onItemSelected (){
        binding.monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = (String) parent.getSelectedItem();
                viewModel.set_monthSelected(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getSelectedItem();
                viewModel.set_yearSelected(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addDataSpinner(){
        String[] monthArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        String[] yearArray = new String[]{"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022","2023"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, monthArray);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, yearArray);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);
    }


    public void addDataInChart (){
        ArrayList<BarEntry> visitors=new ArrayList<>();
        List<Order> transaction = new ArrayList<>(getArguments().<Order>getParcelableArrayList("chart_month"));
        Map<String, Long> totalsByDate = new HashMap<>();
            for (int x = 0; x < transaction.size(); x++) {
                Date dateOne = transaction.get(x).getCreatedAt();
                Instant instantOne = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    instantOne = dateOne.toInstant();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate localDateOne =  instantOne.atZone(ZoneId.systemDefault()).toLocalDate();
                    if (totalsByDate.containsKey(String.valueOf(localDateOne.getDayOfMonth()))) {
                        Long total = totalsByDate.get(String.valueOf(localDateOne.getDayOfMonth()));
                        totalsByDate.put(String.valueOf(localDateOne.getDayOfMonth()), total + transaction.get(x).getTotalCost());
                    } else {
                        totalsByDate.put(String.valueOf(localDateOne.getDayOfMonth()), transaction.get(x).getTotalCost());
                    }
                }
            }
        if (viewModel.get_monthSelected() != null && viewModel.get_monthSelected().getValue() != null&&viewModel.get_yearSelected()!=null&&viewModel.get_yearSelected().getValue()!=null) {
            List<Order> transactions = new ArrayList<>(getArguments().<Order>getParcelableArrayList("chart_month"));
            for (int i=0;i<transactions.size();i++){
                Date date = transactions.get(i).getCreatedAt();
                Instant instant = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    instant = date.toInstant();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                  LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                    if(String.valueOf(localDate.getMonthValue()).equals(viewModel.get_monthSelected().getValue())&&
                            String.valueOf(localDate.getYear()).equals(viewModel.get_yearSelected().getValue()
                            )){
                        for (Map.Entry<String, Long> entry : totalsByDate.entrySet()) {
                            String dateTime = entry.getKey();
                            Long total = entry.getValue();
                            if(String.valueOf(localDate.getDayOfMonth()).equals(dateTime)){
                                visitors.add(new BarEntry((float)localDate.getDayOfMonth(),Float.valueOf(total)));
                                totalsByDate.remove(dateTime);
                                break;
                            }
                        }
                    }
                }
            }
            BarDataSet barDataSet=new BarDataSet(visitors,"revenues");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);

            BarData barData=new BarData(barDataSet);
            binding.barChartMonth.setFitBars(true);
            binding.barChartMonth.setData(barData);
            binding.barChartMonth.getDescription().setText("Bar Chart");
            binding.barChartMonth.animateY(2000);
        } else {
            Log.d("ddd", "Month selected is null.");
        }
    }

    public void observeViewModel (){
        viewModel.get_monthSelected().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String selectedMonth) {
                addDataInChart();
            }
        });

        viewModel.get_yearSelected().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String selectedYear) {
                addDataInChart();
            }
        });
    }

}