package com.cacaopaycard.cacaopay.onBoarding;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cacaopaycard.cacaopay.R;


public class OnboardingFragment extends Fragment {

    private int page;

    public static OnboardingFragment newInstance(int page) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();

        args.putInt("page",page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("page");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int layoutFragment;

        switch (page){
            case 0:
                layoutFragment = R.layout.fragment_onboarding;
                break;
            case 1:
                layoutFragment = R.layout.fragment_onboarding2;
                break;
            case 2:
                layoutFragment = R.layout.fragment_onboarding3;
                break;
            default:
                layoutFragment = R.layout.fragment_onboarding;
                break;
        }

        View view = inflater.inflate(layoutFragment, container, false);



        return view;

    }



}
