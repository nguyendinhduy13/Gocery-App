package com.mobiledevelopment.feature.customer.checkout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.CreateOrder;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.FragmentCheckOut1Binding;

import org.json.JSONObject;

public class CheckOut1Fragment extends Fragment {
    public final String TAG = "Checkout1Fragment";
    private FragmentCheckOut1Binding binding;
    private Checkout1ViewModel checkout1ViewModel;

    public CheckOut1Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        checkout1ViewModel = new ViewModelProvider(this).get(Checkout1ViewModel.class);

        binding = FragmentCheckOut1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkout1ViewModel.initValue(getArguments());
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2554, Environment.SANDBOX);
        setOnClick();
        observeViewModel();
        setUpUi();
    }



    private void setOnClick() {
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkout1ViewModel.getPaymentMethod().equals("ZaloPay")) {
                    CreateOrder orderApi = new CreateOrder();
                    try {
                        JSONObject data = orderApi.createOrder(checkout1ViewModel.totalPayment.toString());
                        String code = data.getString("return_code");
                        if (code.equals("1")) {
                            String token = data.getString("zp_trans_token");
                            ZaloPaySDK.getInstance().payOrder(getActivity(), token, "demozpdk://app", new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {

                                }

                                @Override
                                public void onPaymentCanceled(String zpTransToken, String appTransID) {

                                }

                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {

                                }
                            });
                          requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Handler timeoutHandler = new Handler();
                                    Runnable timeoutRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            checkout1ViewModel.updateCart(getArguments().getString("docId"));
                                            Navigation.findNavController(view)
                                                    .navigate(R.id.action_checkOut1Fragment_to_checkOut2Fragment);
                                        }
                                    };
                                    timeoutHandler.postDelayed(timeoutRunnable, 500);
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    checkout1ViewModel.updateCart(getArguments().getString("docId"));
                    Navigation.findNavController(view)
                            .navigate(R.id.action_checkOut1Fragment_to_checkOut2Fragment);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModel() {
        checkout1ViewModel.orderTotalLiveData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        binding.txtOrderPrice.setText(s);
                    }
                });

        checkout1ViewModel.deliveryFeeLiveData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        binding.txtFee.setText(s);
                    }
                });

        checkout1ViewModel.totalPaymentLiveData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        binding.txtTotalPrice.setText(s);
                    }
                });
        checkout1ViewModel.destinationLiveData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        binding.txtDestination.setText(s);
                    }
                });
        checkout1ViewModel.voucherLiveData.observe(
                getViewLifecycleOwner(),
                voucher -> {
                    if (voucher.equals(Voucher.undefinedVoucher)) {
                        binding.lnrVoucherDiscount.setVisibility(View.GONE);
                    }
                    else {
                        binding.txtVoucher.setText(voucher.toFullName());
                        binding.lnrVoucherDiscount.setVisibility(View.VISIBLE);
                        String voucherDiscount = StringFormatterUtils.toCurrency((checkout1ViewModel.totalPayment * voucher.getValue() / 100));
                        binding.txtVoucherDiscount.setText("-" + voucherDiscount);
                    }
                    Log.d("ChecFrag", "voucher:" + voucher);

                });
    }

    private void setUpUi() {
        checkout1ViewModel.setPaymentMethod("Cash");
        // set up app bar
        binding.myToolbar.inflateMenu(R.menu.app_bar_cart);
        binding.myToolbar.setTitle("Payment");
        binding.lnrDestination.setOnClickListener(view -> {
            new ChooseAddressDialog(
                    checkout1ViewModel.allAddresses,
                    address -> checkout1ViewModel.destinationLiveData.postValue(address.toNormalizedString()))
                    .show(getChildFragmentManager(), ChooseAddressDialog.TAG);
        });
        binding.txtDestination.setOnClickListener(view -> {
            new ChooseAddressDialog(
                    checkout1ViewModel.allAddresses,
                    address -> checkout1ViewModel.destinationLiveData.postValue(address.toNormalizedString()))
                    .show(getChildFragmentManager(), ChooseAddressDialog.TAG);
        });

        binding.lnrVoucher.setOnClickListener(view -> {
            new ChooseVoucherDialog(
                    checkout1ViewModel.allVouchers,
                    voucher -> {
                        Log.d("CheckFrag", "dialog - voucher: " + voucher);
                        checkout1ViewModel.setVoucher(voucher);
                    })
                    .show(getChildFragmentManager(), ChooseAddressDialog.TAG);
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = group.findViewById(checkedId);
                String selectedText = selectedRadioButton.getText().toString();
                checkout1ViewModel.setPaymentMethod(selectedText);
            }
        });
        //setup text
    }
}