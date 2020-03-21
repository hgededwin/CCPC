package com.cacaopaycard.cacaopay.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cacaopaycard.cacaopay.onBoarding.OnboardingFragment;

public class OnboardingAdapter extends FragmentPagerAdapter {


    public OnboardingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int indice) {
        switch (indice){
            case 0:
                return OnboardingFragment.newInstance(indice);
            case 1:
                return OnboardingFragment.newInstance(indice);
            case 2:
                return OnboardingFragment.newInstance(indice);
                default:
                    return OnboardingFragment.newInstance(indice);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
