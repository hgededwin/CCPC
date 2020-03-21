package com.cacaopaycard.cacaopay.AdminCuenta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Adapters.TarjetaAdapter;
import com.cacaopaycard.cacaopay.AdminCuenta.Tarjetas.AgregarTarjetaActivity;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Modelos.DialogFragmentPIN;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.cacaopaycard.cacaopay.Constantes.AGREGAR_TARJETA;
import static com.cacaopaycard.cacaopay.Constantes.APP_ID;


public class TarjetasFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView rvTarjetas;
    private TarjetaAdapter tarjetaAdapter;
    private List<Tarjeta> tarjetas = new ArrayList<>();
    private Toolbar toolbar;
    private Boolean editando = false;
    //private Button btnEditarTarjetas;
    private TextView btnAgregarTarjeta;
    private ImageView imgAgregarTarjetas;

    // Selección de tarjetas
    private TarjetaAdapter adaptadorSeleccion;
    private Singleton singleton;
    private RequestQueue requestQueue;
    private Usuario usuario;

    private TarjetaAdapter.EditionCardListener listener = new TarjetaAdapter.EditionCardListener() {
        @Override
        public void eliminarTarjeta(Context context, final int indice, final String numeroTarjeta) {
            //removeCard(numeroTarjeta, indice);
            boolean confirmElim = false;
            new MaterialDialog.Builder(getContext())
                    .positiveText(getString(R.string.str_aceptar))
                    .negativeText(getString(R.string.str_cancelar))
                    .content("¿Desea eliminar la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "?")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            System.out.println("removing");
                            //removeCard(numeroTarjeta, indice);
                            removeCard(numeroTarjeta,indice);

                        }
                    })
                    .show();


        }

        @Override
        public void lockUnlockCard(Context context, final int indice, final String numeroTarjeta, final boolean isBlocked) {
            System.out.println("lock/unlock");
            //bloqueoDesbloqueoTarjeta(numeroTarjeta,indice,isBlocked);
            lockUnlock(numeroTarjeta,indice,isBlocked);


        }
    };

    public void lockUnlockcard(final boolean isLock, String currentCard, final int indice){

        String lockUnlockDesired = isLock ? "00": "28";
        final Peticion peticionLockunlock = new Peticion(view.getContext(), requestQueue);
        peticionLockunlock.addParams(getString(R.string.card_number_param), currentCard);
        peticionLockunlock.addParams(getString(R.string.status_desired_param), lockUnlockDesired);
        peticionLockunlock.stringRequest(Request.Method.POST, getString(R.string.url_lock_unlock), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionLockunlock.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        Log.i(Constantes.TAG, message);
                        System.out.println("bloqueo desbloqueo exitoso");


                        tarjetas.get(indice).setEstaBloqueada(!isLock);
                        tarjetaAdapter.notifyDataSetChanged();

                    } else {
                        Log.e(Constantes.TAG, message);
                        new MaterialDialog.Builder(view.getContext())
                                .content(message)
                                .positiveText("OK")
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();


                }

            }
        });
    }

    public TarjetasFragment() {
        // Required empty public constructor
    }


    public static TarjetasFragment newInstance(String param1, String param2) {
        TarjetasFragment fragment = new TarjetasFragment();
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
        view = inflater.inflate(R.layout.fragment_tarjetas, container, false);

        singleton = Singleton.getInstance(view.getContext());
        requestQueue = singleton.getmRequestQueue();
        usuario = new Usuario(view.getContext());

        //toolbar = view.findViewById(R.id.toolbar_mis_tarjetas);
        rvTarjetas = view.findViewById(R.id.rv_tarjetas);
        btnAgregarTarjeta = view.findViewById(R.id.txt_agregar_tarjetas);
        imgAgregarTarjetas = view.findViewById(R.id.img_agregar_tarjetas);
        btnAgregarTarjeta.getDrawingRect(new Rect(20,20,20,20));
        imgAgregarTarjetas.setOnClickListener(this);
        btnAgregarTarjeta.setOnClickListener(this);

        rvTarjetas.setHasFixedSize(true);
        // servicio para llenar tarjetas.


        tarjetaAdapter = new TarjetaAdapter(tarjetas, listener, getContext());
        rvTarjetas.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvTarjetas.setAdapter(tarjetaAdapter);

        getUserData();


        return view;
    }


    public void llenarTarjetas(){
        tarjetas.clear();
        Tarjeta tarjeta1 = new Tarjeta("10000.30","1234 1234 1234 4321","MXN", "00");
        Tarjeta tarjeta2 = new Tarjeta("1800.40","1234 1234 1234 1234","MXN", "28");
        Tarjeta tarjeta3 = new Tarjeta("20000.50","1234 1234 1234 9875","MXN", "00");

        tarjetas.add(tarjeta1);
        tarjetas.add(tarjeta2);
        tarjetas.add(tarjeta3);


        tarjetaAdapter = new TarjetaAdapter(tarjetas, listener, getContext());
        rvTarjetas.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvTarjetas.setAdapter(tarjetaAdapter);
    }



    public void onClickAgregarTarjetas(View view) {



        Intent intentAddCard = new Intent(view.getContext(), AgregarTarjetaActivity.class);
        startActivityForResult(intentAddCard,AGREGAR_TARJETA);
        ((Activity) getContext()).overridePendingTransition(R.anim.left_in,R.anim.left_out);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == AGREGAR_TARJETA && resultCode == RESULT_OK){

            getUserData();

            if(data != null) {
                /*System.out.println("Se recibió tarjeta.....");
                //updateControl(false);
                Tarjeta tarjetaAgregada = (Tarjeta) data.getSerializableExtra("nueva_tarjeta");

                tarjetas.add(tarjetaAgregada);
                System.out.println("....tarjetas" + tarjetas);

                tarjetaAdapter = new TarjetaAdapter(tarjetas, listener);
                rvTarjetas.setLayoutManager(new LinearLayoutManager(view.getContext()));
                rvTarjetas.setAdapter(tarjetaAdapter);*/
            }

            //finish();
        }
    }

    public void onClickRemoverTarjeta(View view) {
        editando = true;
        updateControl(editando);

    }

    public void onClickEliminarTarjetasSeleccionadas(View view) {

        ArrayList<Tarjeta> tarjetasMarcadas = adaptadorSeleccion.obtenerSeleccionados();
        if(tarjetasMarcadas.size() != 0){
            for(Tarjeta card : tarjetasMarcadas){
                // obtener datos de las tarjetas selccionadas
                String saldo = card.getSaldo();
                System.out.println("Saldo:" + saldo);

                // mandar a llamar servicio para eliminar tarjetas.
            }
        }
    }

    public void onClickCancelarEliminacion(View view) {
        editando = false;
        updateControl(editando);
    }

    public void updateControl(Boolean editar){
        if(editar){
            rvTarjetas.setAdapter(new TarjetaAdapter(tarjetas, getContext()));
            //btnEditarTarjetas.setText(R.string.str_editar);
            editando = false;
        } else {


            adaptadorSeleccion = new TarjetaAdapter(tarjetas, listener, getContext());
            rvTarjetas.setAdapter(adaptadorSeleccion);
            //btnEditarTarjetas.setText(R.string.str_cancelar);
            editando = true;

        }
    }

    public void removeCard(String numTarjeta, final int indice){

        final Peticion peticionRemove = new Peticion(view.getContext(),requestQueue);
        peticionRemove.addParams(getString(R.string.numero_tarjeta_param),numTarjeta);
        peticionRemove.addParams(getString(R.string.phone_params),usuario.getTelefono());
        peticionRemove.addParams(getString(R.string.app_id_params),APP_ID);
        peticionRemove.stringRequest(Request.Method.POST, getString(R.string.url_delete_card), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionRemove.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");
                    if(success == 1){

                        Log.i(Constantes.TAG, message);
                        System.out.println(jsonObject);
                        tarjetas.remove(indice);
                        tarjetaAdapter.notifyDataSetChanged();

                        getUserData();
                        /*tarjetas.remove(indice);
                        tarjetaAdapter.notifyDataSetChanged();*/
                        // se eliminó tarjeta
                    } else {
                        Log.e(Constantes.TAG, message);


                    }
                    /*tarjetas.remove(indice);
                    tarjetaAdapter.notifyDataSetChanged();*/

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
                    JSONObject jsonObject = new JSONObject(response);
                    int succes = jsonObject.getInt("succes");

                    if(succes == 1) {
                        Log.i( Constantes.TAG,"SUCCESSFUL...");
                        System.out.println("Get user data...");
                        System.out.println(jsonObject);
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

                            getCardBalance(numCard, tarjeta);

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


    public void getCardBalance(final String  numCard, final Tarjeta tarjeta){

        tarjetas.clear();
        final Peticion requestCardBalance = new Peticion(view.getContext(), requestQueue);
        requestCardBalance.addParams(getString(R.string.card_number_param),numCard);

        requestCardBalance.stringRequest(Request.Method.POST, getString(R.string.url_card_balance), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestCardBalance.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    System.out.println("Solicitud exitosa, respuesta en proceso.....");

                    if(success == 1){
                        Log.i(Constantes.TAG, "CardBalance");
                        String saldo = jsonObject.getString("saldo");
                        String estado = jsonObject.getString("estado");
                        String stp = jsonObject.getString("stp");

                        //finishRequestCards.onFinishRequestCard(saldo,estado,stp);
                        tarjeta.setSaldo(saldo);
                        tarjeta.setEstado(estado);
                        tarjeta.setStp(stp);
                        tarjetas.add(tarjeta);
                        tarjetaAdapter.notifyDataSetChanged();

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


    }


    public void lockUnlock(final String numeroTarjeta, final int indice, final boolean isLock){

        if(isLock){
            new MaterialDialog.Builder(view.getContext())
                    .title("Desbloquear tarjeta")
                    .content("¿Seguro que deseas desbloquear la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "?")
                    .positiveText(R.string.str_continuar)
                    .negativeText("Deshacer")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // Servicio que debloquea tarjetas.

                            lockUnlockcard(isLock,numeroTarjeta, indice);

                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(view.getContext())
                    .title("Bloquear tarjeta")
                    .content("¿Seguro que deseas bloquear la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "?")
                    .positiveText(R.string.str_continuar)
                    .negativeText("Deshacer")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // Servicio que bloquea tarjetas.
                            lockUnlockcard(isLock,numeroTarjeta, indice);

                        }
                    })
                    .show();
        }
    }

    public void bloqueoDesbloqueoTarjeta(String numeroTarjeta, final int indice, boolean isBlocked){

        if(isBlocked){
            new MaterialDialog.Builder(view.getContext())
                    .title("Desbloquear tarjeta")
                    .content("¿Seguro que deseas desbloquear la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "?")
                    .positiveText(R.string.str_continuar)
                    .negativeText("Deshacer")
                    .negativeColorRes(R.color.blue_color_contraste)
                    .positiveColorRes(R.color.blue_color_contraste)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // Servicio que debloquea tarjetas.

                            DialogFragmentPIN dialogDesbloqueo = DialogFragmentPIN.instanceDialog(view.getContext(), new DialogFragmentPIN.DialogListener() {
                                @Override
                                public void calbackOnComplete(DialogFragment dialog, String otp) {

                                    tarjetas.get(indice).setEstaBloqueada(false);
                                    tarjetaAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }, null, "Ingrese su PIN para continuar", null);

                            dialogDesbloqueo.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), "CacaoPay");


                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(view.getContext())
                    .title("Bloquear tarjeta")
                    .content("¿Seguro que deseas bloquear la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "?")
                    .positiveText(R.string.str_continuar)
                    .negativeText("Deshacer")
                    .negativeColorRes(R.color.blue_color_contraste)
                    .positiveColorRes(R.color.blue_color_contraste)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // Servicio que bloquea tarjetas.

                            DialogFragmentPIN dialogDesbloqueo = DialogFragmentPIN.instanceDialog(view.getContext(), new DialogFragmentPIN.DialogListener() {
                                @Override
                                public void calbackOnComplete(DialogFragment dialog, String otp) {
                                    tarjetas.get(indice).setEstaBloqueada(true);
                                    tarjetaAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }, null, "Ingrese su PIN para continuar", null);

                            dialogDesbloqueo.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), "CacaoPay");


                        }
                    })
                    .show();
        }
    }

    public void onClickEditarMisTarjetas(View view) {
        updateControl(editando);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.txt_agregar_tarjetas:
                onClickAgregarTarjetas(view);
                break;
            case R.id.img_agregar_tarjetas:
                onClickAgregarTarjetas(view);
                break;

        }

    }
}
