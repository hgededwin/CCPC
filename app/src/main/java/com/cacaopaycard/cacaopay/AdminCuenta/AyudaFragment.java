package com.cacaopaycard.cacaopay.AdminCuenta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cacaopaycard.cacaopay.R;

public class AyudaFragment extends Fragment {

    private View view;
    private Button btnContactar;

    public AyudaFragment() {
        // Required empty public constructor
    }


    public static AyudaFragment newInstance(String param1, String param2) {
        AyudaFragment fragment = new AyudaFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ayuda, container, false);
        btnContactar = view.findViewById(R.id.btn_contactar_ayuda);
        btnContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.support_email)});
                emailIntent.putExtra(Intent.EXTRA_TITLE, getResources().getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Ayuda " + getResources().getString(R.string.app_name));
                //emailIntent.setType("message/rfc822");
                //emailIntent.setType("text/plain");
                startActivity(Intent.createChooser(emailIntent,"Email ..."));
            }
        });

        return view;

    }



}
