package com.cacaopaycard.cacaopay.Adapters;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.mvp.cardInfo.CardFragment;

import java.util.ArrayList;
import java.util.List;

public class TarjetasDashboardAdapter extends FragmentStatePagerAdapter {


    private List<Tarjeta> tarjetas;
    private List<CardFragment> cardFragments = new ArrayList<>();

    public TarjetasDashboardAdapter(FragmentManager fm, List<Tarjeta> tarjetas){
        super(fm);
        this.tarjetas = tarjetas;
    }


    @Override
    public Fragment getItem(int indice) {

        Log.e("TAG","getitem");
        Bundle bundle = new Bundle();
        bundle.putSerializable("card",tarjetas.get(indice));
        CardFragment cardFragment = new CardFragment();
        cardFragment.setArguments(bundle);
        cardFragments.add(cardFragment);
        return cardFragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return tarjetas.size();
    }

    public CardFragment getCardFragmentAt(int position) {
        return cardFragments.get(position);
    }
}
