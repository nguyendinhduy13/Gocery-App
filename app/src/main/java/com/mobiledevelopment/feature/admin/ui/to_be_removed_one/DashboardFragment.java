package com.mobiledevelopment.feature.admin.ui.to_be_removed_one;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.TransactionRepository;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.databinding.FragmentDashboardBinding;
import com.mobiledevelopment.databinding.FragmentTransactionBinding;
import com.mobiledevelopment.feature.admin.category.DetailCategoryViewModel;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.DashboardAdapter;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Dashboard;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.DashboardUIState;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.to_be_removed_three.TransactionDetailBottomSheet;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding _binding;
    private DashboardViewModel viewModel;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(
                requireActivity(),
                new DashboardViewModel.DashboardViewModelFactory(
                        new TransactionRepository(),
                        new UserRepository())).get(DashboardViewModel.class);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
        getData();
        setupAdapter();
    }

    private void getData() {
        viewModel.getTransactionFromDb();
    }

    private void observeViewModel() {
        viewModel.state.observe(getViewLifecycleOwner(), new Observer<DashboardUIState>() {
            @Override
            public void onChanged(DashboardUIState transactionUIState) {
                handleState(transactionUIState);
            }
        });
    }

    private void handleState(DashboardUIState transactionUIState) {
        if (transactionUIState.name().equals("LOADED")) {
            setupAdapter();
        }
    }

    private void notifyDataChanged() {
        _binding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void setupAdapter() {
        //Toast.makeText(getContext(), "set up adapter", Toast.LENGTH_SHORT).show();
        _binding.recyclerView.setAdapter(new DashboardAdapter(
                viewModel.listTransactionLiveData.getValue(),
                requireContext(),
                new DashboardAdapter.OnClickItemMenuTransaction() {
                    @Override
                    public void onClick(Dashboard transaction, String status) {
                        viewModel.sendNotification(transaction, status);
                    }
                },
                new OnTransactionItemClickListener() {
                    @Override
                    public void onClick(Dashboard transaction) {
                        new TransactionDetailBottomSheet(transaction).show(
                                getChildFragmentManager(),
                                TransactionDetailBottomSheet.TAG);
                    }
                }));
    }
}