package com.cacaopaycard.cacaopay.mvp.cardInfo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.cacaopaycard.cacaopay.Adapters.FechaAdapter;
import com.cacaopaycard.cacaopay.Adapters.TarjetasDashboardAdapter;
import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Transferencias.RecibirDineroActivity;
import com.cacaopaycard.cacaopay.Transferencias.TransferenciaActivity;


import java.util.List;

import me.relex.circleindicator.CircleIndicator;

import static android.app.Activity.RESULT_OK;

public class ConsultasFragment extends Fragment implements CardInfoView, ViewPager.OnPageChangeListener, View.OnClickListener {

    private final String TAG = "ConsultasFragment";

    private Button btnTransferencia,btnRecibirPago;
    private RecyclerView rvMovimietosFecha;
    private FechaAdapter fechaAdapter;
    private TarjetasDashboardAdapter tarjetasDashboardAdapter;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private static ConsultasFragment instance = null;
    private TextView txtSinMovimientos;
    private static OnScrollBottomListener listenerScroll;
    private CardInfoPresenter cardInfoPresenter;

    private ProgressDialog progressDialog;
    private Usuario usuario;

    public interface OnScrollBottomListener{
        void onViewScrolled(int dx, int dy);
    }

    public static ConsultasFragment newInstance(ConsultasFragment.OnScrollBottomListener listenerScroll) {
        ConsultasFragment fragment = new ConsultasFragment();
        Bundle args = new Bundle();

        ConsultasFragment.listenerScroll = listenerScroll;

        fragment.setArguments(args);
        return fragment;
    }

    public static ConsultasFragment getInstance(){
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ConsultasFragment.instance = this;

        View view = inflater.inflate(R.layout.fragment_consultas, container, false);

        btnRecibirPago = view.findViewById(R.id.btn_recibir_transferencias_db);
        btnTransferencia = view.findViewById(R.id.btn_transferencia_db);

        pager = view.findViewById(R.id.pager_menu);
        circleIndicator = view.findViewById(R.id.indicator);
        txtSinMovimientos = view.findViewById(R.id.txt_sin_movimientos);
        rvMovimietosFecha = view.findViewById(R.id.rcv_movimientos_consultas);
        rvMovimietosFecha.setHasFixedSize(true);

        rvMovimietosFecha.setLayoutManager(new LinearLayoutManager(view.getContext()));


        pager.addOnPageChangeListener(this);
        btnTransferencia.setOnClickListener(this);
        btnRecibirPago.setOnClickListener(this);

        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Cargando");


        rvMovimietosFecha.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(listenerScroll != null) {
                    listenerScroll.onViewScrolled(dx, dy);
                }


            }
        });
        usuario = new Usuario(getContext());

        cardInfoPresenter = new CardInfoPresenter(this, new CardInfoInteractor(Volley.newRequestQueue(getContext())));
        cardInfoPresenter.getCards(usuario);





        return view;
    }

    public void onClickTransferencias() {

        Intent intentTransferencias = new Intent(getContext(), TransferenciaActivity.class);
        intentTransferencias.putExtra("telefono_transfer", usuario.getTelefono());
        intentTransferencias.putExtra("num_tarjeta_emisora",getCurrentCard().getNumeroCuenta());
        intentTransferencias.putExtra("saldo", getCurrentCard().getSaldo());
        startActivityForResult(intentTransferencias,0);

    }

    public void onClickRecibirPago() {


        Intent intentRecibirDinero = new Intent(getContext(), RecibirDineroActivity.class);

        intentRecibirDinero.putExtra("clabe", getCurrentCard().getStp());
        intentRecibirDinero.putExtra("num_cuenta", getCurrentCard().getCardMask());
        intentRecibirDinero.putExtra("nombre", usuario.getNombreUsuario());

        startActivityForResult(intentRecibirDinero,0);

    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }


    @Override
    public void showCardLocked() {
        btnRecibirPago.setEnabled(false);
        btnRecibirPago.setBackgroundResource(R.drawable.button_disable);
        btnTransferencia.setEnabled(false);
        btnTransferencia.setBackgroundResource(R.drawable.button_disable);
        rvMovimietosFecha.setVisibility(View.GONE);
        txtSinMovimientos.setVisibility(View.VISIBLE);
        txtSinMovimientos.setText(getString(R.string.str_tarjeta_bloqueada));
    }

    @Override
    public void showCardUnlocked() {
        btnRecibirPago.setEnabled(true);
        btnTransferencia.setEnabled(true);
        btnRecibirPago.setBackgroundResource(R.drawable.button_dashboard_recibir);
        btnTransferencia.setBackgroundResource(R.drawable.button_dashboard_mandar);
        rvMovimietosFecha.setVisibility(View.VISIBLE);
        txtSinMovimientos.setVisibility(View.GONE);
    }

    @Override
    public void showCards(List<Tarjeta> cards) {
        tarjetasDashboardAdapter = new TarjetasDashboardAdapter(getChildFragmentManager(), cards);
        pager.setAdapter(tarjetasDashboardAdapter);
        circleIndicator.setViewPager(pager);
        onPageSelected(pager.getCurrentItem());
    }

    @Override
    public void showMovements(List<Movimiento> movimientos, List<Fecha> fechas) {
        rvMovimietosFecha.setAdapter(null);
        fechaAdapter = new FechaAdapter(movimientos,fechas);
        rvMovimietosFecha.setAdapter(fechaAdapter);
        txtSinMovimientos.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyMovements() {
        rvMovimietosFecha.setAdapter(null);
        txtSinMovimientos.setVisibility(View.VISIBLE);
        txtSinMovimientos.setText(R.string.str_sin_movimientos);
    }

    @Override
    public void showError(String error) {
        rvMovimietosFecha.setAdapter(null);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyCards() {
        btnRecibirPago.setAlpha(0.5f);
        btnTransferencia.setAlpha(0.5f);
        btnTransferencia.setEnabled(false);
        btnRecibirPago.setEnabled(false);
        rvMovimietosFecha.setAdapter(null);
        Toast.makeText(getContext(), getString(R.string.str_sin_tarjetas), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

        getCurrentCardFragment().getCardBalance();

        if (!getCurrentCard().isEstaBloqueada()){
            cardInfoPresenter.getCardMovements(getCurrentCard());
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private Tarjeta getCurrentCard(){
        return tarjetasDashboardAdapter.getCardFragmentAt(pager.getCurrentItem()).getCard();
    }

    private CardFragment getCurrentCardFragment(){
        return tarjetasDashboardAdapter.getCardFragmentAt(pager.getCurrentItem());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_recibir_transferencias_db:
                onClickRecibirPago();
                break;
            case R.id.btn_transferencia_db:
                onClickTransferencias();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK){
            onPageSelected(pager.getCurrentItem());
        }
    }
}
