package com.cacaopaycard.cacaopay.AdminCuenta.Tarjetas;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Registro.RegistroExitosoActivity;
import com.cacaopaycard.cacaopay.Utils.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cacaopaycard.cacaopay.Constantes.AGREGAR_TARJETA;
import static com.cacaopaycard.cacaopay.Constantes.APP_ID;

public class AgregarTarjetaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout tilNombre, tilNumeroCuenta, tilPin;
    private MaskEditText maskNumeroTarjeta;
    private EditText edtxtNombreTarjeta, setPin;
    private RequestQueue requestQueue;
    private Singleton singleton;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarjeta);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();
        usuario = new Usuario(this);

        toolbar = findViewById(R.id.toolbar_agregar_tarjeta);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        maskNumeroTarjeta = findViewById(R.id.maskedtxt_agregar_nombre_tarjeta);
        edtxtNombreTarjeta = findViewById(R.id.edtxt_agregar_numero_tarjeta);
        setPin = findViewById(R.id.edtxt_set_pin);
        tilNombre = findViewById(R.id.til_nombre_tarjeta_agregar);
        tilNumeroCuenta = findViewById(R.id.til_numero_tarjeta_agregar);
        tilPin = findViewById(R.id.til_set_pin);

        maskNumeroTarjeta.completeEntry = new MaskEditText.CompleteEntry() {
            @Override
            public void isCardComplete(String textFinal, boolean cardIsCorrect) {
                tilNumeroCuenta.setError(null);
            }
        };

        edtxtNombreTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilNombre.setError(null);
            }
        });

        setPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilPin.setError(null);
            }
        });






    }

    public void onClickConfirmacionAgregar(View view) {

        if(!maskNumeroTarjeta.getRowText().matches("[0-9]{16}")){
            // validación número de cuenta vacío

            System.out.println(".....tarjeta \"" + maskNumeroTarjeta.getRowText() + "\"");
            Toast.makeText(this, "Error al llenar datos " ,Toast.LENGTH_SHORT).show();
            Log.e(Constantes.TAG, maskNumeroTarjeta.getText().toString());

            tilNumeroCuenta.setError("Debe ingresar una tarjeta valida");

        } else if(setPin.getText().toString().isEmpty()){
            tilPin.setError("Debe ingresar un PIN de 6 dígitos para continuar");

        } else {

            // petición para agregar nueva tarjeta.
            addCardRequest(maskNumeroTarjeta.getRowText());

            /*Intent intentAddCardsuccess = new Intent(this, RegistroExitosoActivity.class);
            intentAddCardsuccess.putExtra("padre",AGREGAR_TARJETA);
            startActivityForResult(intentAddCardsuccess,AGREGAR_TARJETA);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);*/


        }



    }

    public void addCardRequest(final String cardNumber){
        Log.e(Constantes.TAG,"Agregando tarjeta");
        final Peticion peticionAddCard = new Peticion(this,requestQueue);
        peticionAddCard.addParams(getString(R.string.card_number_param),cardNumber);
        peticionAddCard.addParams(getString(R.string.phone_params),usuario.getTelefono());
        peticionAddCard.addParams(getString(R.string.nickname_param),edtxtNombreTarjeta.getText().toString());
        peticionAddCard.addParams(getString(R.string.app_id_params),APP_ID);

        peticionAddCard.stringRequest(Request.Method.POST, getString(R.string.url_add_card), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionAddCard.dismissProgressDialog();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    if(success == 1) {
                        Log.i(Constantes.TAG, "Tarjeta agregada");
                        JSONObject objectCard = jsonObject.getJSONObject("card");
                        String nickname = objectCard.getString("nickname");
                        String card = objectCard.getString("card");

                        // regresar nueva tarjeta a "mis tarjetas"

                        changeNipCard(cardNumber);
                        /*Intent intentAdicionExitosa = new Intent(AgregarTarjetaActivity.this, RegistroExitosoActivity.class);
                        intentAdicionExitosa.putExtra("padre", AGREGAR_TARJETA);
                        startActivityForResult(intentAdicionExitosa,AGREGAR_TARJETA);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);*/

                        /*Uri dato = Uri.parse("content://agregar/");
                        Intent intent = new Intent(null, dato);

                        Tarjeta nuevaTarjeta = new Tarjeta("$x.xx", maskNumeroTarjeta.getText().toString(), "MXN", false);
                        nuevaTarjeta.setNombre(edtxtNombreTarjeta.getText().toString());

                        intent.putExtra("nueva_tarjeta", nuevaTarjeta);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);*/

                    } else {
                        String message = jsonObject.getString("message");
                        Log.e(Constantes.TAG, message);

                        new MaterialDialog.Builder(AgregarTarjetaActivity.this)
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


    public void changeNipCard(final String numeroTarjeta){
        final Peticion requestChangeNIP  =  new Peticion(this, requestQueue);
        requestChangeNIP.addParams(getString(R.string.card_number_param), numeroTarjeta);
        requestChangeNIP.addParams(getString(R.string.nip_param), setPin.getText().toString());
        requestChangeNIP.addParams(getString(R.string.nip_confirmation_param), setPin.getText().toString());
        requestChangeNIP.addParams(getString(R.string.nickname_param), edtxtNombreTarjeta.getText().toString().isEmpty() ? " " : edtxtNombreTarjeta.getText().toString());

        requestChangeNIP.stringRequest(Request.Method.POST, getString(R.string.url_change_nip), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestChangeNIP.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");
                    System.out.println("Card edited:"+ numeroTarjeta);
                    System.out.println(jsonObject);
                    if(success == 1){
                        Log.i(Constantes.TAG, message);

                        Intent intentAdicionExitosa = new Intent(AgregarTarjetaActivity.this, RegistroExitosoActivity.class);
                        intentAdicionExitosa.putExtra("padre", AGREGAR_TARJETA);
                        startActivityForResult(intentAdicionExitosa,AGREGAR_TARJETA);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);


                    } else{
                        Log.e(Constantes.TAG, "Error en la petición al cambiar el nip tarjeta.");
                        Log.e(Constantes.TAG,message);
                        new MaterialDialog.Builder(AgregarTarjetaActivity.this)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AGREGAR_TARJETA && resultCode == RESULT_OK) {

            Uri dato = Uri.parse("content://agregar/");

            Intent intent = new Intent(null, dato);
/*
            Tarjeta nuevaTarjeta = new Tarjeta("0.00", maskNumeroTarjeta.getText().toString(), "MXN", "00");
            nuevaTarjeta.setNombre(edtxtNombreTarjeta.getText().toString());*/

            //intent.putExtra("nueva_tarjeta", nuevaTarjeta);
            setResult(RESULT_OK, intent);
            finish();

            //overridePendingTransition(R.anim.right_in,R.anim.right_out);

        }


    }

    public void onClickCancelarAgregacion(View view) {
        onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }


}
