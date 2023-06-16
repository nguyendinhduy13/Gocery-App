package com.mobiledevelopment.feature.admin.voucher;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.core.repository.VoucherRepository;

import java.util.Date;
import java.util.List;

public class AddVoucherViewModel extends ViewModel {
    private MutableLiveData<List<User>> _userLiveData;
    private UserRepository _userRepository;
    private VoucherRepository voucherRepository;
    private MutableLiveData<String> documentId;
    private MutableLiveData<Date> date;

    public AddVoucherViewModel(){
        _userLiveData = new MutableLiveData<>();
        _userRepository = new UserRepository();
        voucherRepository=new VoucherRepository();
        documentId=new MutableLiveData<>();
        date=new MutableLiveData<>();
        // get data from firebase store
        initData();
    }

    public MutableLiveData<List<User>> get_userLiveData() {
        return _userLiveData;
    }

    public void setDocumentId(String id) {
        documentId.setValue(id);
    }

    public void setDate(Date dateTime){
        date.setValue(dateTime);
    }

    void initData(){
        _userRepository.getAllUsers(new UserRepository.OnGetAllUsersCompleteListener() {
            @Override
            public void onComplete(List<User> users) {
                _userLiveData.setValue(users);
            }
        });
    }

    public void AddVoucher(String description, Long value){
        Voucher voucher=new Voucher(null,documentId.getValue(),value,date.getValue(),description);
        voucherRepository.addVoucher(new VoucherRepository.OnAddVouchersCompleteListener() {
            @Override
            public void onComplete() {
                _userRepository.getUserToken(documentId.getValue(), new UserRepository.OnGetUserTokenCompleteListener() {
                    @Override
                    public void onComplete(String token) {
                        voucherRepository.sendNotication(token);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {

            }
        },voucher);

    }

    public static class AddVoucherViewModelFactory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;
        private final VoucherRepository voucherRepository;

        public AddVoucherViewModelFactory(UserRepository userRepository, VoucherRepository voucherRepository) {
            this.userRepository =  userRepository;
            this.voucherRepository = voucherRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AddVoucherViewModel();
        }
    }
}
