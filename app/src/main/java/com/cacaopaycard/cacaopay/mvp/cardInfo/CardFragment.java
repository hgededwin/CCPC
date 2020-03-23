package com.cacaopaycard.cacaopay.mvp.cardInfo;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.Volley;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.R;

public class CardFragment extends Fragment implements com.cacaopaycard.cacaopay.mvp.cardInfo.CardView, CompoundButton.OnCheckedChangeListener {


    private TextView saldo;
    private TextView numCuenta, txtLock;
    private SwitchCompat switchCompat;
    private RelativeLayout relativeLayout;
    private boolean estaBloqueada;
    private boolean isSettingPreviousState = false;
    private CardPresenter cardPresenter;
    private Tarjeta card;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_slider_menu, container,false);
        saldo = view.findViewById(R.id.txt_saldo_consulta_cv);
        numCuenta = view.findViewById(R.id.txt_number_cuenta_consulta_cv);
        relativeLayout = view.findViewById(R.id.relative_consultas);
        switchCompat = view.findViewById(R.id.switch_consulta_cv);
        txtLock = view.findViewById(R.id.txt_bloquear_tarjeta_cv);
        cardPresenter = new CardPresenter(
                this,
                new CardInteractor(Volley.newRequestQueue(getContext())
                ));


        card = (Tarjeta) getArguments().getSerializable("card");

        switchCompat.setOnCheckedChangeListener(this);
        return view;
    }

    public void getCardBalance(){
        cardPresenter.getCardBalance(card);
    }


    @Override
    public void showCardInfo(Tarjeta card) {

        Log.e("TAG","showing card info");
        saldo.setText(card.getSaldo());
        numCuenta.setText(card.getTarjetaOfuscada());
        switchCompat.setChecked(card.isEstaBloqueada());

        if(card.isEstaBloqueada()) showCardLocked();
        else showCardUnLocked();
    }

    @Override
    public void showError(String message) {
        new MaterialDialog.Builder(getContext())
                .positiveText("Entendido")
                .cancelable(false)
                .content(message)
                .show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }



    public Tarjeta getCard() {
        return card;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (!isSettingPreviousState){
            //  On switch click
            int content = estaBloqueada ? R.string.str_confirmacion_desbloqueo : R.string.str_confirmacion_bloqueo;

            new MaterialDialog.Builder(getContext())
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            cardPresenter.lockUnlockCard(!estaBloqueada, card.getNumeroCuenta());
                        }
                    })
                    .cancelable(false)
                    .positiveText(R.string.str_aceptar)
                    .negativeText(R.string.str_cancelar)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            setSwitchPreviousState();
                        }
                    })
                    .content(content)
                    .show();
        } else {
            isSettingPreviousState = false;


        }

    }

    @Override
    public void showCardLocked() {
        // tarjeta bloqueada
        estaBloqueada = true;
        relativeLayout.setBackgroundColor(Color.rgb(152,156,161));
        txtLock.setText("Desbloquear tarjeta");
        ConsultasFragment.getInstance().showCardLocked();
    }

    @Override
    public void showCardUnLocked() {
        // tarjeta desbloqueada
        estaBloqueada = false;
        relativeLayout.setBackgroundColor(Color.rgb(0,38,61));
        txtLock.setText("Bloquear tarjeta");
        ConsultasFragment.getInstance().showCardUnlocked();
    }

    @Override
    public void setSwitchPreviousState() {
        isSettingPreviousState = true;
        switchCompat.setChecked(estaBloqueada);
    }
}