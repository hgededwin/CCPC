package com.cacaopaycard.cacaopay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Adapters.FechaAdapter;
import com.cacaopaycard.cacaopay.Adapters.TarjetasDashboardAdapter;
import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.Transferencias.RecibirDineroActivity;
import com.cacaopaycard.cacaopay.Transferencias.TransferenciaActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import me.relex.circleindicator.CircleIndicator;
import static com.cacaopaycard.cacaopay.Constantes.APP_ID;


public class ConsultasFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = "ConsultasFragment";

    private Button btnTransferencia,btnRecibirPago;
    private RecyclerView rvMovimietosFecha;
    //private MovimientosAdapter movimientosAdapter;
    private FechaAdapter fechaAdapter;
    private ViewPager pager;
    private CircleIndicator circleIndicator;
    private List<Movimiento> movimientos = new ArrayList<>();
    private List<Tarjeta> tarjetas = new ArrayList<>();
    private List<Fecha> fechas = new ArrayList<>();
    private List<String> fechasTemp = new ArrayList<>();
    private TarjetasDashboardAdapter tarjetasDashboardAdapter;
    private static ConsultasFragment instance = null;
    private String saldo, currentCard;
    private Tarjeta tarjeta;
    private Usuario usuario;
    private View view;
    private TextView txtSinMovimientos;
    private RequestQueue requestQueue;
    private Singleton singleton;
    private static OnScrollBottomListener listenerScroll;
    private OnListenerRequest finishRequestCards;

    public interface OnScrollBottomListener{
        void onViewScrolled(int dx, int dy);
    }
    public interface OnListenerRequest{
        void onFinishRequestCard(String saldo, String estado, String stp);
    }

    public static ConsultasFragment newInstance(OnScrollBottomListener listenerScroll) {
        ConsultasFragment fragment = new ConsultasFragment();
        Bundle args = new Bundle();

        ConsultasFragment.listenerScroll = listenerScroll;

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_consultas, container, false);

        btnRecibirPago = view.findViewById(R.id.btn_recibir_transferencias_db);
        btnTransferencia = view.findViewById(R.id.btn_transferencia_db);
        usuario = new Usuario(view.getContext());
        singleton = Singleton.getInstance(view.getContext());
        requestQueue = singleton.getmRequestQueue();

        ConsultasFragment.instance = this;

        pager = view.findViewById(R.id.pager_menu);
        circleIndicator = view.findViewById(R.id.indicator);
        txtSinMovimientos = view.findViewById(R.id.txt_sin_movimientos);
        rvMovimietosFecha = view.findViewById(R.id.rcv_movimientos_consultas);
        rvMovimietosFecha.setHasFixedSize(true);

        btnRecibirPago.setOnClickListener(this);
        btnTransferencia.setOnClickListener(this);

        // configurando adapter del viewPager
        //configurarTarjetas();
        getUserData();

        tarjetasDashboardAdapter = new TarjetasDashboardAdapter(getChildFragmentManager(), tarjetas);
        pager.setAdapter(tarjetasDashboardAdapter);
        circleIndicator.setViewPager(pager);

        fechaAdapter = new FechaAdapter(movimientos,fechas);
        rvMovimietosFecha.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvMovimietosFecha.setAdapter(fechaAdapter);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int indice) {
                currentCard = tarjetas.get(indice).getNumeroCuenta();
                getMovimientos(currentCard);
                System.out.println("número de cuenta..." + currentCard + "["+indice+"]");

                enableDisableUI(!tarjetas.get(indice).isEstaBloqueada());

            }

            @Override
            public void onPageScrollStateChanged(int indice) {

            }
        });




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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(Constantes.TAG, "onResume");
    }

    public void onClickTransferencias(View view) {

        System.out.println("...tarjeta emisora:" + tarjetas.get(pager.getCurrentItem()).getNumeroCuenta());
        Intent intentTransferencias = new Intent(view.getContext(), TransferenciaActivity.class);
        intentTransferencias.putExtra("telefono_transfer", usuario.getTelefono());
        intentTransferencias.putExtra("num_tarjeta_emisora",tarjetas.get(pager.getCurrentItem()).getNumeroCuenta());
        intentTransferencias.putExtra("saldo", tarjetas.get(pager.getCurrentItem()).getSaldo());
        startActivity(intentTransferencias);
    }

    public void onClickRecibirPago(View view) {

        Intent intentRecibirDinero = new Intent(view.getContext(), RecibirDineroActivity.class);
        System.out.println("...num_cuenta:" + tarjetas.get(pager.getCurrentItem()).getCardMask());
        System.out.println("...CLABE:" + tarjetas.get(pager.getCurrentItem()).getStp());
        System.out.println("...Nombre:" + usuario.getNombreUsuario());


        intentRecibirDinero.putExtra("clabe", tarjetas.get(pager.getCurrentItem()).getStp());
        intentRecibirDinero.putExtra("num_cuenta", tarjetas.get(pager.getCurrentItem()).getCardMask());
        intentRecibirDinero.putExtra("nombre", usuario.getNombreUsuario());

        startActivity(intentRecibirDinero);

    }

    // WS Methods

    public void lockUnlockcard(final boolean isLock){

        String lockUnlockDesired = isLock ? "00": "28";
        final Peticion peticionLockunlock = new Peticion(view.getContext(), requestQueue);
        peticionLockunlock.addParams(getString(R.string.card_number_param), tarjetas.get(pager.getCurrentItem()).getNumeroCuenta());
        peticionLockunlock.addParams(getString(R.string.status_desired_param), lockUnlockDesired);
        peticionLockunlock.stringRequest(Request.Method.POST, getString(R.string.url_lock_unlock), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionLockunlock.dismissProgressDialog();
                Log.e("LockUnlock",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        Log.i(Constantes.TAG, message);
                        System.out.println("bloqueo desbloqueo exitoso");

                        enableDisableUI(isLock);


                    } else {
                        Log.e(Constantes.TAG, message);
                        new MaterialDialog.Builder(view.getContext())
                                .positiveText("Ok")
                                .content(message)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }

            }
        });
    }

    public void getMovimientos(String currentNumberCard){

        movimientos.clear();
        fechas.clear();
        //System.out.println("...Card:" + currentNumberCard);
        final Peticion peticionMovimientos = new Peticion(view.getContext(),requestQueue);
        peticionMovimientos.addParams(getString(R.string.card_number_param),currentNumberCard);
        peticionMovimientos.stringRequest(Request.Method.POST, getString(R.string.url_moves), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                peticionMovimientos.dismissProgressDialog();
                try {
                    Log.e("Movimientos",response);
                    JSONObject jsonObject = new JSONObject(response);

                    int success = jsonObject.getInt("succes");
                    JSONArray transito = jsonObject.getJSONArray("transito");

                    if(success == 1) {
                        //String message = jsonObject.getString("message");
                        Log.i( Constantes.TAG,"SUCCESSFUL");


                        JSONArray respMoviemientos = jsonObject.getJSONObject("message")
                                .getJSONObject("response")
                                .getJSONObject("consulta_movimientos_response")
                                .getJSONObject("return")
                                .getJSONArray("respuesta_movimientos");

                        if(respMoviemientos.length() == 0){
                            rvMovimietosFecha.setVisibility(View.GONE);
                            txtSinMovimientos.setVisibility(View.VISIBLE);
                            txtSinMovimientos.setText(getString(R.string.str_sin_movimientos));

                        } else {

                            for (int i = 0; i < respMoviemientos.length(); i++) {

                                String monto = respMoviemientos.getJSONObject(i).getString("monto");
                                String tipo = respMoviemientos.getJSONObject(i).getString("tipo");
                                String fechaObject = respMoviemientos.getJSONObject(i).getString("fecha");
                                String dia = fechaObject.substring(6);
                                String mes = fechaObject.substring(4,6);
                                String anio = fechaObject.substring(0,4);
                                boolean transferRecibida = tipo.contains("Abono");

                                System.out.println("Tipo["+ i +"]... " + transferRecibida);

                                Fecha fecha = new Fecha(dia, mes, anio);

                                if (!fechasTemp.contains(fechaObject)) {
                                    //System.out.println("agregando nueva fecha");
                                    fechasTemp.add(fechaObject);
                                    fechas.add(fecha);
                                }

                                Movimiento movimiento = new Movimiento(tipo, fecha, monto, transferRecibida);
                                movimientos.add(movimiento);

                            }
                        }

                        fechaAdapter.notifyDataSetChanged();


                    } else{
                        String message = jsonObject.getString("message");
                        Log.e(Constantes.TAG, message);

                        //llenarMovimientos();

                        if(transito.length() == 0){
                            rvMovimietosFecha.setVisibility(View.GONE);
                            txtSinMovimientos.setVisibility(View.VISIBLE);
                            txtSinMovimientos.setText(getString(R.string.str_sin_movimientos));
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });

    }

    public void getUserData(){

        final Peticion petGetUserData = new Peticion(view.getContext(), requestQueue);
        petGetUserData.addParams("phone",usuario.getTelefono());
        petGetUserData.addParams("id_app",APP_ID);
        petGetUserData.stringRequest(Request.Method.POST, getString(R.string.url_get_user_data), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                petGetUserData.dismissProgressDialog();

                try {

                    Log.e("UserData", response);

                    JSONObject jsonObject = new JSONObject(response);
                    int succes = jsonObject.getInt("succes");


                    if(succes == 1) {
                        //System.out.println("Get user data...");
                        //System.out.println(jsonObject);
                        /*if(jsonObject.getString("avatar") != null) {
                            String avatarUrl = jsonObject.getString("avatar");
                            Bitmap bitmap = getImageURL(avatarUrl);
                        }*/

                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("message");

                        final JSONArray cards = jsonObject.getJSONArray("cards");

                        usuario.setTelefono(jsonObjectMessage.getString("phone"));


                        for(int cardCount = 0; cardCount < cards.length(); cardCount++) {
                            final int finalCardCount = cardCount;

                            JSONObject card = cards.getJSONObject(finalCardCount);
                            String numCard = card.getString("card");
                            String nickname = card.getString("nickname");
                            String name = card.getString("name");
                            final Tarjeta tarjeta = new Tarjeta(numCard,nickname,name);

                            getCardBalance(numCard, new OnListenerRequest() {
                                @Override
                                public void onFinishRequestCard(String saldo, String estado, String stp) {

                                    //System.out.println("contador" + finalCardCount + "cards.length: " + cards.length());

                                    tarjeta.setSaldo(saldo);
                                    tarjeta.setEstado(estado);
                                    tarjeta.setStp(stp);
                                    tarjetas.add(tarjeta);


                                    if (finalCardCount == (cards.length() - 1) ) {
                                        /*System.out.println("creando adaptadoe");
                                        tarjetasDashboardAdapter = new TarjetasDashboardAdapter(getChildFragmentManager(), tarjetas);
                                        pager.setAdapter(tarjetasDashboardAdapter);
                                        circleIndicator.setViewPager(pager);*/

                                        pager.getAdapter().notifyDataSetChanged();
                                        circleIndicator.setViewPager(pager);
                                        getMovimientos(tarjetas.get(pager.getCurrentItem()).getNumeroCuenta());

                                    }
                                }
                            });
                        }


                    } else {
                        String message = jsonObject.getString("message");
                        Log.e(Constantes.TAG, message);

                        new MaterialDialog.Builder(view.getContext())
                                .positiveText("OK")
                                .content(message)
                                .show();

                        // configurarTarjetas();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //configurarTarjetas();

    }

    public void getCardBalance(String  numCard, final OnListenerRequest finishRequestCards){

        final Peticion requestCardBalance = new Peticion(view.getContext(), requestQueue);
        requestCardBalance.addParams(getString(R.string.card_number_param),numCard);

        requestCardBalance.stringRequest(Request.Method.POST, getString(R.string.url_card_balance), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestCardBalance.dismissProgressDialog();
                try {
                    Log.e("CardBalance",response);
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");

                    if(success == 1){
                        Log.i(Constantes.TAG, "CardBalance");
                        String saldo = jsonObject.getString("saldo");
                        String estado = jsonObject.getString("estado");
                        String stp = jsonObject.getString("stp");

                        finishRequestCards.onFinishRequestCard(saldo,estado,stp);



                    } else{
                        String message = jsonObject.getString("message");
                        Log.e(Constantes.TAG, "Error CardBalance");
                        new MaterialDialog.Builder(view.getContext())
                                .positiveText("OK")
                                .content(message)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        /*if(i == (cards.length() - 1)){

                if(finishRequestCards != null){
                    finishRequestCards.onFinishRequest();
                }
            }*/
        /*for(int i = 0; i < cards.length(); i++) {
ç


        }*/

    }

    public Bitmap getImageURL(String url) {
        Bitmap bm = null;
        try {
            URL _url = new URL(url);
            URLConnection con = _url.openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {

        }
        return bm;
    }

    public static ConsultasFragment getInstance(){
        return instance;
    }

    public void enableDisableUI(boolean isEnable){
        Log.e("instance parent", "instancia desde fragment secundario");

        if(!isEnable){
            // bloqueando tarjeta
            tarjetas.get(pager.getCurrentItem()).setEstaBloqueada(true);
            btnRecibirPago.setEnabled(false);
            btnRecibirPago.setBackgroundResource(R.drawable.button_disable);
            btnTransferencia.setEnabled(false);
            btnTransferencia.setBackgroundResource(R.drawable.button_disable);
            rvMovimietosFecha.setVisibility(View.GONE);
            txtSinMovimientos.setVisibility(View.VISIBLE);
            txtSinMovimientos.setText(getString(R.string.str_tarjeta_bloqueada));

            // bloquear
            //lockUnlockcard(false);
        } else {
            // desbloqueando tarjeta

            tarjetas.get(pager.getCurrentItem()).setEstaBloqueada(false);
            btnRecibirPago.setEnabled(true);
            btnTransferencia.setEnabled(true);
            btnRecibirPago.setBackgroundResource(R.drawable.button_dashboard_recibir);
            btnTransferencia.setBackgroundResource(R.drawable.button_dashboard_mandar);
            rvMovimietosFecha.setVisibility(View.VISIBLE);
            txtSinMovimientos.setVisibility(View.GONE);
            //lockUnlockcard(false);
            //getMovimientos(currentCard);
            //tarjetasDashboardAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_recibir_transferencias_db:
                onClickRecibirPago(btnRecibirPago);
                break;
            case R.id.btn_transferencia_db:
                onClickTransferencias(btnTransferencia);
                break;
        }

    }

}
