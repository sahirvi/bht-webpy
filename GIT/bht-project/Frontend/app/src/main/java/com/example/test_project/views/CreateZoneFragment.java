package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.test_project.R;
import com.example.test_project.api.BloomCall;
import com.example.test_project.viewHelper.RetrofitClient;
import com.example.test_project.viewHelper.UserSessionManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateZoneFragment extends Fragment {
    View view;

    private static final String TAG = "Create Zone Fragment";

    FrameLayout contentstart;
    RelativeLayout footer_flower;
    AppCompatButton btn_create_next;
    TextView tv_create_cancel;
    ImageView create_zone_back;
    TextView create_zone_title;
    TextView ventil_text;
    TextView ventil_title;

    UserSessionManager session;

    int hub_Id, zone_count;
    int increment_zone_count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_addzone, container, false);



        //UserSession

        session = new UserSessionManager(getContext());
        //session.logoutUser();


        //Bundle
        Bundle bundle = this.getArguments();
        hub_Id = bundle.getInt("hubId");
        zone_count = bundle.getInt("zoneCount");
        Log.d(TAG, "ADD ZONE COUNT: " + zone_count);


        //init views
        btn_create_next = view.findViewById(R.id.btn_create_next);
        tv_create_cancel = view.findViewById(R.id.btn_create_cancel);
        create_zone_back = view.findViewById(R.id.create_zone_back);
        create_zone_title = view.findViewById(R.id.create_zone_title);
        ventil_text = view.findViewById(R.id.ventil_text);
        ventil_title = view.findViewById(R.id.ventil_title);




        //Hide Parent Content
        contentstart = getActivity().findViewById(R.id.scroll_layout_dashboard);
        footer_flower = getActivity().findViewById(R.id.footer_flower);

        contentstart.setVisibility(View.INVISIBLE);
        footer_flower.setVisibility(View.GONE);

        //Start Zone Count at 1
        increment_zone_count = zone_count;
        if (zone_count < 4){
            increment_zone_count += 1;
        }


        //Init Title
        create_zone_title.setText("Gießzone " + increment_zone_count + " einrichten");

        ventil_title.setText("Erst Schlauch an Ventil " + increment_zone_count + " stecken");
        ventil_text.setText("Sobald der Sensor verbunden ist, kann Wasser aus Ventil " + increment_zone_count + " fließen");


        //Actions

        //Next Button
        btn_create_next.setOnClickListener(v -> {
            CreateZoneNextFragment nextFragment = new CreateZoneNextFragment();

            Bundle bundle2 = new Bundle();
            bundle2.putInt("zoneId", increment_zone_count);
            bundle2.putInt("hubId", hub_Id);
            nextFragment.setArguments(bundle2);

            getParentFragmentManager().beginTransaction().remove(this).commit();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_dashboard, nextFragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        });


        //Abbrechen Link
        tv_create_cancel.setOnClickListener(v -> {
            contentstart.setVisibility(View.VISIBLE);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // Back Button
        create_zone_back.setOnClickListener(v -> {
            contentstart.setVisibility(View.VISIBLE);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        contentstart.setVisibility(View.VISIBLE);
        footer_flower.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        contentstart.setVisibility(View.VISIBLE);
        footer_flower.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contentstart.setVisibility(View.VISIBLE);
        footer_flower.setVisibility(View.VISIBLE);
    }




}