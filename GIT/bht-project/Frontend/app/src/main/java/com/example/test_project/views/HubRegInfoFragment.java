package com.example.test_project.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.test_project.R;

/**
 * Fragment of Hub Registration
 */
public class HubRegInfoFragment extends Fragment {
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hub_reg_info, container, false);

        // get email from previous Fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        email = bundle.getString("email");

        // if yes button clicked go to hub registration
        Button yesButton = view.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> openHubRegistration());

        return view;
    }

    /**
     * opens Hub Registration Fragment
     */
    private void openHubRegistration() {
        // pass bundle with email to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("email", email);


        HubRegFragment hubRegFragment = new HubRegFragment();
        hubRegFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().remove(this).commit();

        // Replace Layout of Activity with hubRegFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcomeLayout, hubRegFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}