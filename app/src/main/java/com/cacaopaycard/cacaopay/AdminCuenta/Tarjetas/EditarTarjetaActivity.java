package com.cacaopaycard.cacaopay.AdminCuenta.Tarjetas;

import android.content.Intent;
import androidx.annotation.Nullable;

import com.cacaopaycard.cacaopay.mvp.util.URLCacao;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Registro.RegistroExitosoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cacaopaycard.cacaopay.Constantes.EDITAR_TARJETA;

public class EditarTarjetaActivity extends AppCompatActivity {

    private EditText edtxtNombreTarjeta, edtxtNip;
    private TextInputLayout tilNickname, tilNuevoNip;
    private Toolbar toolbar;
    private Singleton singleton;
    private RequestQueue requestQueue;
    private String numeroTarjeta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tarjeta);

        // se obtiene el número de tarjeta y se quitan los espacios en blanco
        numeroTarjeta = getIntent().getStringExtra("number_card").replaceAll("\\s", "");
        edtxtNombreTarjeta = findViewById(R.id.edtxt_nickname_editar);
        edtxtNip = findViewById(R.id.edtxt_nuevo_nip_editar);
        toolbar = findViewById(R.id.toolbar_editar_tarjetas);
        tilNickname = findViewById(R.id.til_nombre_tarjeta_editar);
        tilNuevoNip = findViewById(R.id.til_nuevo_nip_editar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();

        edtxtNombreTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilNickname.setError(null);
            }
        });

        edtxtNip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilNuevoNip.setError(null);
            }
        });

    }

    public void onClickGuardarCambiosTarjeta(View view) {

        if(edtxtNip.getText().toString().isEmpty()){
            tilNuevoNip.setError("Debe establecer un nuevo NIP");
        } else{
            cambiosTerjetas();
        }




    }

    public void cambiosTerjetas(){
        final Peticion requestChangeNIP  =  new Peticion(this, requestQueue);

       /* requestChangeNIP.addParams(getString(R.string.card_number_param), numeroTarjeta);
        requestChangeNIP.addParams(getString(R.string.nip_param), edtxtNip.getText().toString());
        requestChangeNIP.addParams(getString(R.string.nip_confirmation_param), edtxtNip.getText().toString());
        requestChangeNIP.addParams(getString(R.string.nickname_param), edtxtNombreTarjeta.getText().toString().isEmpty() ? " " : edtxtNombreTarjeta.getText().toString()); */


        requestChangeNIP.addParamsString("Tarjeta", numeroTarjeta);
        requestChangeNIP.addParamsString("NIPNuevo", edtxtNip.getText().toString());

        requestChangeNIP.stringRequest(Request.Method.POST, URLCacao.URL_CAMBIAR_NIP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestChangeNIP.dismissProgressDialog();
                Log.e(Constantes.TAG, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");
                    System.out.println("Card edited:"+ numeroTarjeta);

                    if(success == 1){
                        Log.i(Constantes.TAG, message);

                        Intent editarSuce = new Intent(EditarTarjetaActivity.this, RegistroExitosoActivity.class);
                        editarSuce.putExtra("padre", EDITAR_TARJETA);
                        startActivityForResult(editarSuce, EDITAR_TARJETA);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);


                    } else{
                        Log.e(Constantes.TAG, "Error en la petición al editar tarjeta.");
                        Log.e(Constantes.TAG,message);
                        new MaterialDialog.Builder(EditarTarjetaActivity.this)
                                .positiveText("Ok")
                                .content(message)
                                .show();

                    }

                    // método temporal para pasar a la siguiente vista


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDITAR_TARJETA)
            finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }

    public void onClickCancelarEdicion(View view) {
        onBackPressed();
    }
}
