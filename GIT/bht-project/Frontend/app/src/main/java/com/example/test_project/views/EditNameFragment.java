package com.example.test_project.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test_project.R;
import com.example.test_project.viewHelper.UserSessionManager;
import com.example.test_project.viewModel.ZoneDetailViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class EditNameFragment extends Fragment {
    private ZoneDetailViewModel zoneDetailViewModel;
    private Integer hubId;
    private Integer zoneId;
    private TextView errorEdit;
    private TextInputEditText editNameInput;

    private static final String TAG = "ZoneDeleteFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);

        //UserSession
        new UserSessionManager(requireContext());

        //MVVM
        zoneDetailViewModel = new ViewModelProvider(this).get(ZoneDetailViewModel.class);

        // get hub_id, zone_id and zone_name from ZoneDetail
        Bundle bundle = this.getArguments();
        assert bundle != null;
        hubId = (Integer) bundle.get("hub_id");
        zoneId = (Integer) bundle.get("zone_id");
        Boolean auto = (Boolean) bundle.get("auto");

        Log.d(TAG, "hub ID: " + hubId + "; zone ID: " + zoneId);

        // get the layout elements
        Button editButton = view.findViewById(R.id.editButton);
        CardView cardViewEdit = view.findViewById(R.id.cardViewEdit);
        ImageView editFooter = view.findViewById(R.id.editFooter);
        editNameInput = view.findViewById(R.id.editNameInput);
        errorEdit = view.findViewById(R.id.errorEdit);
        errorEdit.setVisibility(View.INVISIBLE);
        TextView deleteZoneText = view.findViewById(R.id.deleteZone);

        if (auto) {
            int lightBlue = ContextCompat.getColor(requireContext(), R.color.light_blue);

            // change the color of background card to lightBlue if auto is on
            cardViewEdit.setCardBackgroundColor(lightBlue);
            editFooter.setImageResource(R.drawable.visual_footer_detail_auto);
        }

        // change color of edit button to dark blue if field is filled
        editNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(editNameInput.getText()).toString())) {
                    editButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.custom_button));
                    int white = ContextCompat.getColor(requireActivity(), R.color.white);
                    editButton.setTextColor(white);
                }
            }
        });

        // if edit Button pressed, update Name on Server
        editButton.setOnClickListener(v -> {
            Log.d(TAG, "EditButton clicked");
            String newName = String.valueOf(editNameInput.getText());
            // if password not equals pattern show error message and change color of inputs
            if (newName.length() > 12) {
                errorEdit.setVisibility(View.VISIBLE);
                errorEdit.setText(R.string.edit_name_error);
                int red = ContextCompat.getColor(requireActivity(), R.color.red);
                editNameInput.setTextColor(red);
            } else {
                updateName(newName);
            }
        });

        // if delete Zone Text is clicked open Delete Zone Fragment
        deleteZoneText.setOnClickListener(v -> {
            Log.d(TAG, "Delete Zone clicked");

            ZoneDeleteFragment zoneDeleteFragment = new ZoneDeleteFragment();
            zoneDeleteFragment.setArguments(bundle);

            // Replace Layout of Activity with ZoneDeleteFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.constraintLayout, zoneDeleteFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Updates name with ViewModel
     * goes back to Zone Detail Activity if zone changed
     */
    private void updateName(String name) {

        zoneDetailViewModel.getZoneObserver().observe(requireActivity(), zoneModel -> {
            if (zoneModel != null) {
                getParentFragmentManager().popBackStack();
            } else {
                Log.d(TAG, "No ZoneModel!");
            }
        });
        zoneDetailViewModel.updateName(getActivity(), hubId, zoneId, name);
    }
}