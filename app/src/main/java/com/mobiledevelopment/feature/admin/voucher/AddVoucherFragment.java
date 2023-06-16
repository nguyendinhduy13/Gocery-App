package com.mobiledevelopment.feature.admin.voucher;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.repository.OrderRepository;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.core.repository.VoucherRepository;
import com.mobiledevelopment.databinding.FragmentAddVoucherBinding;
import com.mobiledevelopment.databinding.FragmentStatisticBinding;
import com.mobiledevelopment.feature.admin.ui.statistic.StatisticViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddVoucherFragment extends Fragment {
    private FragmentAddVoucherBinding binding;
    private AddVoucherViewModel viewModel;
    private ArrayAdapter<String> emailAdapter;
    private HashMap<String, String> documentIdMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddVoucherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel= new ViewModelProvider(
                this,
                new AddVoucherViewModel.AddVoucherViewModelFactory(new UserRepository(),new VoucherRepository())).get(AddVoucherViewModel.class);
        initSpinner();
        OnClickListener();
        SpinnerListener();
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
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String selected = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        binding.edtDateExpired.setText(selected);
                        // Chuyển đổi Calendar thành đối tượng Date
                        Date date = selectedDate.getTime();
                        viewModel.setDate(date);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void OnClickListener(){
        binding.btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edt_description=binding.edtDescription.getText().toString().trim();
                String edt_ValueVoucher=binding.edtValueVoucher.getText().toString().trim();
                String edt_dateExpire=binding.edtDateExpired.getText().toString().trim();
                if(edt_description.isEmpty()){
                    binding.edtDescription.setError("Empty");
                }
                if(edt_ValueVoucher.isEmpty()){
                    binding.edtValueVoucher.setError("Empty");
                }
                if(edt_dateExpire.isEmpty()){
                    binding.edtDateExpired.setError("Empty");
                }
                if(!edt_description.isEmpty()&&!edt_ValueVoucher.isEmpty()&&!edt_dateExpire.isEmpty()) {
                    Long value = Long.valueOf(String.valueOf(binding.edtValueVoucher.getText().toString()));
                    String description = binding.edtDescription.getText().toString();
                    viewModel.AddVoucher(description, value);
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        binding.btnDateExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private void initSpinner(){
        emailAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        observeViewModel();
        binding.emailSpinner.setAdapter(emailAdapter);
    }

    private void SpinnerListener(){
        binding.emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEmail = emailAdapter.getItem(position);
                if (selectedEmail != null) {
                    // Lấy documentId tương ứng với email được chọn từ HashMap
                    String documentId = documentIdMap.get(selectedEmail);
                    if (documentId != null) {
                        viewModel.setDocumentId(documentId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void observeViewModel (){
        viewModel.get_userLiveData().observe(getViewLifecycleOwner(),users -> {
            for (int i = 0; i < users.size(); i++) {
                String email=users.get(i).getEmail();
                if (email != null) {
                    // Thêm email vào ArrayAdapter
                    emailAdapter.add(email);
                    // Lưu trữ documentId và email vào HashMap
                    documentIdMap.put(email, users.get(i).getId());
                }
            }
        });
    }

}