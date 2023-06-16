package com.mobiledevelopment.feature.admin.ui.statistic.chart;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Transaction;
import com.mobiledevelopment.databinding.FragmentChartDayBinding;
import com.mobiledevelopment.databinding.FragmentStatisticBinding;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartDayFragment extends Fragment {
    FragmentChartDayBinding binding;
    ChartViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChartDayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel=new ChartViewModel();
        onClickListener();
        observeViewModel();
    }

    private void onClickListener(){
        binding.dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.arrowBackChartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
    }

    private void bindingDataRevenue(){

        if (viewModel.get_dateSelected() != null && viewModel.get_dateSelected().getValue() != null) {
            Long total_revenue= Long.valueOf(0);
            int count=0;
            int inprogress=0;
            int finished=0;
            int cancelled=0;
            List<Order> transactions = new ArrayList<>(getArguments().<Order>getParcelableArrayList("chart_day"));
            for (int i=0;i<transactions.size();i++){
                Date date = transactions.get(i).getCreatedAt();
                Instant instant = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    instant = date.toInstant();
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    String condition=String.valueOf(localDate.getDayOfMonth())+"/"+String.valueOf(localDate.getMonthValue())+"/"+String.valueOf(localDate.getYear());
                    if(condition.equals(viewModel.get_dateSelected().getValue())
                           ){
                        count=count+1;
                        total_revenue=total_revenue+transactions.get(i).getTotalCost();
                        if(transactions.get(i).getStatus().equals("In progress")){
                            inprogress+=1;
                        }
                        if(transactions.get(i).getStatus().equals("Cancelled")){
                            cancelled+=1;
                        }
                        if(transactions.get(i).getStatus().equals("Finished")){
                            finished+=1;
                        }
                    }
                }
            }
            binding.shopRevenue.setText(String.valueOf(total_revenue));
            binding.amountTransaction.setText(String.valueOf(count));
            binding.inprogressAmount.setText(String.valueOf(inprogress));
            binding.finishedAmount.setText(String.valueOf(finished));
            binding.cancelledAmount.setText(String.valueOf(cancelled));
            binding.informationRevenue.setVisibility(View.VISIBLE);
        } else {
            Log.d("ddd", "Month selected is null.");
        }
    }



    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // update TextView with selected date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        binding.dateTextView.setText(selectedDate);
                        viewModel.set_dateSelected(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void observeViewModel (){
        viewModel.get_dateSelected().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                bindingDataRevenue();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}