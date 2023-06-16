package com.mobiledevelopment.feature.admin.ui.statistic.chart;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChartViewModel extends ViewModel {
    private final MutableLiveData<String> _monthSelected = new MutableLiveData<>();
    private final MutableLiveData<String> _yearSelected = new MutableLiveData<>();
    private final MutableLiveData<String> _dateSelected = new MutableLiveData<>();

    public ChartViewModel() {
    }

    public void set_dateSelected(String date){
        _dateSelected.setValue(date);
    }

    public void set_monthSelected(String month) {
        _monthSelected.setValue(month);
    }

    public void set_yearSelected(String year){
        _yearSelected.setValue(year);
    }

    public MutableLiveData<String> get_monthSelected() {
        return _monthSelected;
    }

    public MutableLiveData<String> get_yearSelected() {
        return _yearSelected;
    }

    public MutableLiveData<String> get_dateSelected() {
        return _dateSelected;
    }
}
