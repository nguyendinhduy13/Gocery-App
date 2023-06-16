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
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;
import com.mobiledevelopment.databinding.FragmentChartMonthBinding;
import com.mobiledevelopment.databinding.FragmentChartYearBinding;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class chartYearFragment extends Fragment {
    FragmentChartYearBinding binding;
    ChartViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChartYearBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel=new ChartViewModel();
        onClickListener();
        addDataSpinner();
        onItemSelected();
        observeViewModel();
        addDataInChart();

    }

    private void onClickListener (){
        binding.arrowBackChartYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private void addDataSpinner(){
        String[] yearArray = new String[]{"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022","2023"};

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, yearArray);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);
    }

    public void addDataInChart (){
        ArrayList<BarEntry> visitors=new ArrayList<>();
        class Revenue {
            String month;
            Float value;

            public Revenue(String month, Float value) {
                this.month = month;
                this.value = value;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public Float getValue() {
                return value;
            }

            public void setValue(Float value) {
                this.value = value;
            }
        }
        if (viewModel.get_yearSelected()!=null&&viewModel.get_yearSelected().getValue()!=null) {
            List<Order> transactions = new ArrayList<>(getArguments().<Order>getParcelableArrayList("chart_year"));
            List<Revenue> revenues =new ArrayList<>();
            for (int i=0;i<transactions.size();i++){
                Date date = transactions.get(i).getCreatedAt();
                Instant instant = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    instant = date.toInstant();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                    if(String.valueOf(localDate.getYear()).equals(viewModel.get_yearSelected().getValue()
                            )){
                        if(revenues.size()!=0) {
                            for (int j = 0; j < revenues.size(); j++) {
                                if (revenues.get(j) != null && revenues.get(j).getMonth().equals(String.valueOf(localDate.getMonthValue()))) {
                                    revenues.get(j).setValue(revenues.get(j).getValue() + Float.valueOf(String.valueOf(transactions.get(i).getTotalCost())));
                                } else {
                                    Revenue value = new Revenue(String.valueOf(localDate.getMonthValue()), Float.valueOf(String.valueOf(transactions.get(i).getTotalCost())));
                                    revenues.add(value);
                                }
                            }
                        }
                        else{
                            Revenue value = new Revenue(String.valueOf(localDate.getMonthValue()), Float.valueOf(String.valueOf(transactions.get(i).getTotalCost())));
                            revenues.add(value);
                        }
                    }
                }
            }
            for (int k=0;k<revenues.size();k++){
                visitors.add(new BarEntry(Float.parseFloat(revenues.get(k).getMonth()),revenues.get(k).getValue()));
            }
            BarDataSet barDataSet=new BarDataSet(visitors,"revenues");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);

            BarData barData=new BarData(barDataSet);
            binding.barChartYear.setFitBars(true);
            binding.barChartYear.setData(barData);
            binding.barChartYear.getDescription().setText("Bar Chart");
            binding.barChartYear.animateY(2000);
        } else {
            Log.d("ddd", "Month selected is null.");
        }
    }


    private void onItemSelected (){
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

    public void observeViewModel (){

        viewModel.get_yearSelected().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String selectedYear) {
                addDataInChart();
            }
        });
    }
}