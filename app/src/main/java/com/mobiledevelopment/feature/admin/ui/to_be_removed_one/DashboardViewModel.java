package com.mobiledevelopment.feature.admin.ui.to_be_removed_one;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.TransactionRepository;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.feature.admin.category.DetailCategoryViewModel;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Dashboard;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.DashboardUIState;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    private MutableLiveData<List<Dashboard>> _listTransactionMutableLiveData = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Dashboard>> listTransactionLiveData = _listTransactionMutableLiveData;

    private MutableLiveData<DashboardUIState> _state = new MutableLiveData<>(DashboardUIState.LOADING);
    public LiveData<DashboardUIState> state = _state;

    public DashboardViewModel(TransactionRepository transactionRepository, UserRepository userRepository){
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }
    public void getTransactionFromDb()
    {
        transactionRepository.getTransactions(new TransactionRepository.OnGetTransactionsCompleteListener() {
            @Override
            public void onComplete(List<Dashboard> transactions) {
                _listTransactionMutableLiveData.postValue(transactions);
                _state.postValue(DashboardUIState.LOADED);
            }
        });
//        db.collection("Transaction")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<Dashboard> list = new ArrayList<>();
//                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
//                        {
//                            list.add( new Dashboard(
//                                    doc.getId(),
//                                    doc.get("progress").toString(),
//                                    doc.get("user_name").toString(),
//                                    doc.getDouble("total_payment"),
//                                    StringFormatterUtils.format(doc.getDate("date"))
//                            ));
//                        }
//
//                    }
//                });
    }

    public void sendNotification(Dashboard transaction, String status){
        userRepository.getUserToken(transaction.getUserName(), new UserRepository.OnGetUserTokenCompleteListener() {
            @Override
            public void onComplete(String token) {
                transactionRepository.sendNotication(transaction, token, status, new TransactionRepository.OnUpdateTransactionListener() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }
        });

    }

    public static class DashboardViewModelFactory implements ViewModelProvider.Factory {
        private final TransactionRepository transactionRepository;
        private final UserRepository userRepository;

        public DashboardViewModelFactory(TransactionRepository transactionRepository, UserRepository userRepository) {
            this.transactionRepository = transactionRepository;
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new DashboardViewModel(transactionRepository,userRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditAddressViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}
