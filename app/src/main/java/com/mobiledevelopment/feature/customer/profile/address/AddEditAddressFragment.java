package com.mobiledevelopment.feature.customer.profile.address;

import static com.mobiledevelopment.core.utils.SecondaryUiUtils.showToast;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.core.models.address.District;
import com.mobiledevelopment.core.models.address.Province;
import com.mobiledevelopment.core.models.address.Ward;
import com.mobiledevelopment.core.repository.AddressRepository;
import com.mobiledevelopment.core.utils.SecondaryUiUtils;
import com.mobiledevelopment.databinding.FragmentAddEditAddressBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddEditAddressFragment extends Fragment {

    public final String TAG = "AddEditAddressFragment";

    private AddEditAddressViewModel viewModel;
    private FragmentAddEditAddressBinding binding;

    private List<TextInputLayout> allTextInputLayouts;
    private List<EditText> addressEditTexts;
    private List<EditText> allEditTexts;
    private Address.DetailAddress currentAddress = Address.DetailAddress.undefinedDetailAddress;

    // When user's editing an existing address, province, district info should
    // be populated into the editTexts beforehand. If the user's adding new address, these
    // will be false, since no need to pre-populate
    private boolean shouldPopulateProvinceEditText = false;
    private boolean shouldPopulateDistrictEditText = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentAddEditAddressBinding.inflate(inflater, container, false);
        allTextInputLayouts = new ArrayList<>(Arrays.asList(
                binding.textinputlayoutProvince,
                binding.textinputlayoutDistrict,
                binding.textinputlayoutStreet,
                binding.textinputlayoutWard,
                binding.textinputlayoutLabel));
        addressEditTexts = new LinkedList<>(Arrays.asList(
                binding.edittextProvince,
                binding.edittextDistrict,
                binding.edittextWard,
                binding.edittextStreet));


        allEditTexts = new ArrayList<>(Arrays.asList(
                binding.edittextDistrict,
                binding.edittextLabel,
                binding.edittextProvince,
                binding.edittextStreet,
                binding.edittextWard));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new AddEditAddressViewModel.AddEditAddressViewModelFactory(new AddressRepository())).get(
                AddEditAddressViewModel.class);

        viewModel.setCurrentAddressId(requireArguments().getString("addressId"));

        // Need to set this to be able to add address
        viewModel.setCurrentUserId(requireArguments().getString("currentUserId"));
        shouldPopulateProvinceEditText = viewModel.currentEditMode == AddEditAddressViewModel.EditMode.EDIT_ADDRESS;
        shouldPopulateDistrictEditText = shouldPopulateProvinceEditText;

        setUpUI();
        observeViewModel();
    }

    private void setUpUI() {
        binding.btnBack.setOnClickListener(view -> {
            Navigation.findNavController(requireView()).popBackStack();
        });
        binding.buttonSave.setOnClickListener((view) -> {
            if (IsAllFieldsValid()) {
                SecondaryUiUtils.showBasicDialog(
                        getContext(),
                        "Confirmation",
                        "Save the information for this address: " + binding.edittextLabel.getText() + "?",
                        "Save",
                        "Cancel",
                        (dialogInterface, id) -> addOrUpdateAddress(),
                        (dialogInterface, id) -> {});
            }
            else {
                showToast(getContext(), "Please fill out all fields with valid input!");
            }
        });

        binding.edittextLabel.addTextChangedListener(getEmptyTextViewWatcher(binding.textinputlayoutLabel));

        binding.edittextProvince.addTextChangedListener(getEmptyTextViewWatcher(binding.textinputlayoutProvince));

        binding.edittextDistrict.addTextChangedListener(getEmptyTextViewWatcher(binding.textinputlayoutDistrict));

        binding.edittextWard.addTextChangedListener(getEmptyTextViewWatcher(binding.textinputlayoutWard));

        binding.edittextStreet.addTextChangedListener(getEmptyTextViewWatcher(binding.textinputlayoutStreet));
    }

    private TextWatcher getEmptyTextViewWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    textInputLayout.setError("Label can't be empty!");
                }
                else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), uiState -> {
            if (uiState instanceof AddEditAddressUiState.FetchDataSuccess) {
                showDefaultUi();

                Address.DetailAddress address = (Address.DetailAddress) ((AddEditAddressUiState.FetchDataSuccess) uiState).getData();

                binding.checkboxIsPrimary.setChecked(address.isPrimary());
                if (address.isPrimary()) {
                    binding.checkboxIsPrimary.setOnCheckedChangeListener((compoundButton, b) -> binding.checkboxIsPrimary.setChecked(
                            true));

                    binding.checkboxIsPrimary.setOnClickListener(view -> showToast(
                            getContext(),
                            "Please choose another address as primary!"));
                }
                binding.edittextLabel.setText(address.getLabel());
                binding.edittextProvince.setText(address.getProvince());
                binding.edittextDistrict.setText(address.getDistrict());
                binding.edittextWard.setText(address.getWard());
                binding.edittextStreet.setText(address.getStreet());

                currentAddress = address;
                Log.d(TAG, "viewModel.currentAddress.observe() - currentAddress: " + address);
            }
            else if (uiState instanceof AddEditAddressUiState.InAddMode) {
                showDefaultUi();
            }
            else if (uiState instanceof AddEditAddressUiState.Loading) {
                showLoadingUi();
            }
            else if (uiState instanceof AddEditAddressUiState.EditSuccess) {
                showToast(getContext(), "Address updated!");
                NavHostFragment.findNavController(this).navigateUp();
            }
            else if (uiState instanceof AddEditAddressUiState.AddSuccess) {
                showToast(getContext(), "New address added!");
                NavHostFragment.findNavController(this).navigateUp();
            }
            else {
                throw new IllegalStateException(TAG + "-observeViewModel(): Unknown uiState: " + uiState.getClass());
            }
        });
        viewModel.allProvinces.observe(getViewLifecycleOwner(), provinces -> {
            Log.d(TAG, "observeViewModel() - provinces:" + provinces);

            binding.edittextProvince.setAdapter(new ArrayAdapter<>(
                    this.getContext(),
                    R.layout.item_dropdown_simpletext,
                    (List<String>) provinces.stream().map(Province::getName)
                            .collect(Collectors.toList())));
            binding.edittextProvince.setOnItemClickListener((adapterView, view, i, l) -> {
                binding.edittextDistrict.getText().clear();
                binding.edittextWard.getText().clear();
                viewModel.selectProvince(
                        provinces.get(i).getCode());
            });
            if (shouldPopulateProvinceEditText && !provinces.isEmpty()) {
                shouldPopulateProvinceEditText = false;
                Address.DetailAddress detailAddress = (Address.DetailAddress) currentAddress;
                Province currentProvince = provinces.stream()
                        .filter(province -> province.getName().equals(detailAddress.getProvince()))
                        .findAny().orElse(null);
                assert currentProvince != null;
                viewModel.selectProvince(currentProvince.getCode());
            }
        });
        viewModel.districts.observe(getViewLifecycleOwner(), districts -> {
            Log.d(TAG, "observeViewModel() - districts:" + districts);
            binding.edittextDistrict.setAdapter(new ArrayAdapter<>(
                    this.getContext(),
                    R.layout.item_dropdown_simpletext,
                    (List<String>) districts.stream().map(District::getName)
                            .collect(Collectors.toList())));

            binding.edittextDistrict.setOnItemClickListener((adapterView, view, i, l) ->
            {
                viewModel.selectWard(
                        districts.get(i).getCode());
                binding.edittextWard.getText().clear();
            });

            if (shouldPopulateDistrictEditText && !districts.isEmpty()) {
                shouldPopulateDistrictEditText = false;
                Address.DetailAddress detailAddress = (Address.DetailAddress) currentAddress;
                Log.d(TAG, "detailAddress: " + detailAddress);
                District currentDistrict = districts.stream()
                        .filter(district -> district.getName().equals(detailAddress.getDistrict()))
                        .findAny()
                        .orElse(null);
                assert currentDistrict != null;
                viewModel.selectWard(currentDistrict.getCode());
            }
        });
        viewModel.wards.observe(getViewLifecycleOwner(), wards -> {
            Log.d(TAG, "observeViewModel() - wards:" + wards);
            binding.edittextWard.setAdapter(new ArrayAdapter<>(
                    this.getContext(),
                    R.layout.item_dropdown_simpletext,
                    (List<String>) wards.stream().map(Ward::getName).collect(Collectors.toList())));

        });
    }

    private boolean IsAllFieldsValid() {
        // Need to check the editTexts since Fragment could be in ADD_MODE, and user just
        // clicks button without filling the fields
        final boolean[] result = {true};
        allTextInputLayouts.forEach(textInputLayout -> result[0] = result[0] && textInputLayout.getError() == null);
        allEditTexts.forEach(editText -> result[0] = result[0] && !Objects.requireNonNull(editText.getText())
                .toString().trim().isEmpty());
        return result[0];
    }

    private void showLoadingUi() {
        binding.wholeLayoutWrapper.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.VISIBLE);
    }

    private void showDefaultUi() {
        binding.wholeLayoutWrapper.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.GONE);
    }

    private void addOrUpdateAddress() {
        StringBuilder detailAddress = new StringBuilder();
        addressEditTexts.forEach(editText -> {
            detailAddress.append(Objects.requireNonNull(editText.getText()));
            if (addressEditTexts.indexOf(editText) != addressEditTexts.size() - 1) {
                detailAddress.append(", ");
            }
        });
        viewModel.addOrUpdateAddress(new Address(
                currentAddress.getId(),
                currentAddress.getIdUser(),
                detailAddress.toString(),
                Objects.requireNonNull(binding.edittextLabel.getText()).toString(),
                binding.checkboxIsPrimary.isChecked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}