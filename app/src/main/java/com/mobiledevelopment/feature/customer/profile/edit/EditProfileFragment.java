package com.mobiledevelopment.feature.customer.profile.edit;

import static com.mobiledevelopment.core.utils.SecondaryUiUtils.showToast;
import static com.mobiledevelopment.core.utils.ValidationUtils.IsEmailValid;
import static com.mobiledevelopment.core.utils.ValidationUtils.IsPhoneNumberValid;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.core.utils.SecondaryUiUtils;
import com.mobiledevelopment.databinding.FragmentEditProfileBinding;

import java.util.Objects;

public class EditProfileFragment extends Fragment {
    public final String TAG = "EditProfileFragment";

    private EditProfileViewModel viewModel;
    private FragmentEditProfileBinding binding;

    private User currentUser = User.undefinedUser;
    private Uri avatarUri = null;

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
            new PickVisualMedia(),
            uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    avatarUri = uri;

                    Glide
                            .with(this)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user)
                            .into(binding.imageviewAvatar);
                    Log.d(
                            TAG,
                            "PhotoPicker - Selected URI: " + uri);
                } else {
                    Log.d(
                            TAG,
                            "PhotoPicker - No media selected");
                }
            });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(
                inflater,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                this,
                new EditProfileViewModel.EditProfileViewModelFactory(new UserRepository())).get(
                EditProfileViewModel.class);

        viewModel.setCurrentUserId(requireArguments().getString("currentUserId"));

        setUpUI();
        observeViewModel();
    }

    private void setUpUI() {
        binding.buttonAddAvatar.setOnClickListener((view) -> {
            // The cast is fine but IDE still shows error somehow. Probably Android's fault
            //Ref: https://stackoverflow.com/questions/73999566/how-to-construct-pickvisualmediarequest-for-activityresultlauncher
            PickVisualMedia.VisualMediaType mediaType = (PickVisualMedia.VisualMediaType) PickVisualMedia.ImageOnly.INSTANCE;

            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build();
            pickMedia.launch(request);
        });

        binding.buttonSave.setOnClickListener((view) -> {
            if (IsAllFieldsValid()) {
                SecondaryUiUtils.showBasicDialog(
                        getContext(),
                        "Confirmation",
                        "Save the information for this user: " + binding.edittextName.getText() + "?",
                        "Save",
                        "Cancel",
                        (dialogInterface, i) -> updateUser(),
                        (dialogInterface, id) -> {});
            } else {
                showToast(getContext(), "Please fill out all fields with valid input!");
            }
        });

        binding.edittextGender.setAdapter(new ArrayAdapter<>(
                requireContext(),
                R.layout.item_dropdown_simpletext,
                getResources().getStringArray(R.array.genders)));

        binding.edittextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!IsEmailValid(charSequence.toString())) {
                    binding.textinputlayoutEmail.setError("Invalid email!");
                } else if (charSequence.toString().trim().isEmpty()) {
                    binding.textinputlayoutEmail.setError("Email can't be empty!");
                } else {
                    binding.textinputlayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.edittextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    binding.textinputlayoutName.setError("Name can't be empty!");
                } else {
                    binding.textinputlayoutName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.edittextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!IsPhoneNumberValid(charSequence.toString())) {
                    binding.textinputlayoutPhone.setError("Invalid phone number!");
                } else if (charSequence.toString().trim().isEmpty()) {
                    binding.textinputlayoutPhone.setError("Phone number can't be empty!");
                } else {
                    binding.textinputlayoutPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void observeViewModel() {
        viewModel.uiState.observe(
                getViewLifecycleOwner(),
                (uiState) -> {
                    if (uiState instanceof EditProfileUiState.FetchDataSuccess) {
                        showDefaultUi();

                        User user = ((EditProfileUiState.FetchDataSuccess) uiState).getData();
                        currentUser = user;

                        if (avatarUri == null) {
                            avatarUri = Uri.parse(currentUser.getAvatar());
                        }
                        Glide
                                .with(this)
                                .load(avatarUri)
                                .placeholder(R.drawable.ic_user)
                                .centerCrop()
                                .into(binding.imageviewAvatar);

                        binding.edittextName.setText(user.getFullName());
                        binding.edittextEmail.setText(user.getEmail());
                        binding.edittextGender.setText(user.getGender(), false);
                        binding.edittextPhone.setText(user.getPhone());
                        Log.d(
                                TAG,
                                "viewModel.uiState.observe() - currentUser: " + user);
                    } else if (uiState instanceof EditProfileUiState.Loading) {
                        showLoadingUi();
                    } else if (uiState instanceof EditProfileUiState.EditSuccess) {
                        showToast(getContext(), "User updated!");
                        NavHostFragment.findNavController(this).navigateUp();
                    } else {
                        throw new IllegalStateException(TAG + "-observeViewModel(): Unknown uiState: " + uiState.getClass());
                    }
                });
    }

    private boolean IsAllFieldsValid() {
        return binding.textinputlayoutName.getError() == null &&
                binding.textinputlayoutPhone.getError() == null &&
                binding.textinputlayoutEmail.getError() == null;
    }

    private void updateUser() {
        User newUserInfo = new User(
                currentUser.getId(),
                Objects.requireNonNull(binding.edittextEmail.getText()).toString(),
                Objects.requireNonNull(binding.edittextName.getText()).toString(),
                currentUser.getPassword(),
                currentUser.getUsername(),
                Objects.requireNonNull(binding.edittextPhone.getText()).toString(),
                Objects.requireNonNull(binding.edittextGender.getText()).toString(),
                currentUser.getAge(),
                currentUser.getRoleId(),
                currentUser.getMembershipPoint(),
                currentUser.getAvatar());

        viewModel.updateUser(
                newUserInfo,
                avatarUri);
    }

    private void showLoadingUi() {
        binding.wholeLayoutWrapper.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.VISIBLE);
    }

    private void showDefaultUi() {
        binding.wholeLayoutWrapper.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Need to have this or else the dropdown on edittextGender won't show all items
        binding.edittextGender.setAdapter(new ArrayAdapter<>(
                requireContext(),
                R.layout.item_dropdown_simpletext,
                getResources().getStringArray(R.array.genders)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel.clearSnapshotListeners();
    }
}