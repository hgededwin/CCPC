package com.cacaopaycard.cacaopay.Registro.RecuperacionPassword;

import android.content.Intent;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.cacaopaycard.cacaopay.Registro.RegistroExitosoActivity;
import com.cacaopaycard.cacaopay.Registro.SetPasswordActivity;
import com.cacaopaycard.cacaopay.mvp.util.URLCacao;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.Constantes.CHANGED_PIN_CANCELED;
import static com.cacaopaycard.cacaopay.Constantes.ENVIO_PASSWORD;
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;
import static com.cacaopaycard.cacaopay.Constantes.REGISTRO;

public class RecuperacionPassInPhoneActivity extends AppCompatActivity {

    private Singleton singleton;
    private RequestQueue requestQueue;
    private EditText edtxtEmailPhone, edtxtEmail;
    private Usuario usuario;
    private static String telefonoTemporal;
    private TextInputLayout tilEmail, tilTelefono;
    private static int EMAIL = 20;
    private static int PHONE = 21;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_pass_in_phone);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();

        edtxtEmailPhone = findViewById(R.id.edtxt_email_phone);
        edtxtEmail = findViewById(R.id.edtxt_email_recover);
        tilEmail = findViewById(R.id.til_email_recover);
        tilTelefono = findViewById(R.id.til_pass);

        edtxtEmailPhone.setVisibility(View.GONE);

        usuario = new Usuario(this);

        edtxtEmailPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilTelefono.setError(null);
            }
        });

        edtxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilEmail.setError(null);
            }
        });




    }

    public void onClickValidarTelefono(View view) {

        // temporal
       if(edtxtEmail.getText().toString().isEmpty()) {
            tilEmail.setError("Debe ingresar el correo");
        } else {

           // telefonoTemporal = edtxtEmailPhone.getText().toString();
            forgotPass();
        }



        /*Intent intent = new Intent(this, RecuperacionPasswordActivity.class);

        startActivityForResult(intent,RECUPERACION_PASSWORD);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);*/
        //forgotPass();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            usuario.setTelefono(telefonoTemporal);
            setResult(RESULT_OK);
            finish();
        } else if (resultCode == CHANGED_PIN_CANCELED)
            finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void onClickCancelarRecuperacionTelefono(View view) {
        onBackPressed();
    }


    public void forgotPass(){

        final Peticion peticion = new Peticion(this, requestQueue);

        peticion.addParamsString("Correo", edtxtEmail.getText().toString());

        Log.e("email -->", edtxtEmail.getText().toString());

        peticion.jsonObjectRequest(Request.Method.POST, URLCacao.URL_RECUPERAR_PASSWORD, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                peticion.dismissProgressDialog();
                Log.e(Constantes.TAG, String.valueOf(response));

                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    Log.e(Constantes.TAG, response.toString());

                    String email = jsonObject.getString("Correo");
                    String codeResponse = jsonObject.getString("ResponseCode");
                    String message = jsonObject.getString("Mensaje");

                    if(codeResponse.equals("00")){
                        Log.i(Constantes.TAG, message);

                        usuario.setCorreo(edtxtEmail.getText().toString());
                        Intent intent = new Intent(RecuperacionPassInPhoneActivity.this, RegistroExitosoActivity.class);
                        intent.putExtra("padre", ENVIO_PASSWORD);
                        startActivityForResult(intent, ENVIO_PASSWORD);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                    } else {
                        Log.e(Constantes.TAG, message);
                        new  MaterialDialog.Builder(RecuperacionPassInPhoneActivity.this)
                                .title("¡Error!")
                                .content(message)
                                .positiveText("Ok")
                                .show();
                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                peticion.dismissProgressDialog();
                Log.e("RESPONSE ERROR ..<", "error");

                new  MaterialDialog.Builder(RecuperacionPassInPhoneActivity.this)
                        .title("¡Error!")
                        .content("Error al obtener la información")
                        .positiveText("Ok")
                        .show();
            }
        });

      /*  peticion.stringRequest(Request.Method.POST, URLCacao.URL_RECUPERAR_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticion.dismissProgressDialog();
                Log.e(Constantes.TAG, response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(jsonObject);

                    int success = jsonObject.getInt("succes");
                    String message  = jsonObject.getString("message");

                    if(success == 1){
                        if(emailPhone == PHONE) {
                            Log.i(Constantes.TAG, message);
                            usuario.setTelefono(edtxtEmailPhone.getText().toString());
                            Intent intent = new Intent(RecuperacionPassInPhoneActivity.this, RecuperacionPasswordActivity.class);
                            startActivityForResult(intent, RECUPERACION_PASSWORD);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        } else {
                            usuario.setCorreo(edtxtEmail.getText().toString());
                            Intent intent = new Intent(RecuperacionPassInPhoneActivity.this, RecuperacionPasswordActivity.class);
                            startActivityForResult(intent, RECUPERACION_PASSWORD);
                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        }

                    } else {
                        if(emailPhone == EMAIL) {
                            Log.e(Constantes.TAG, message);
                            new MaterialDialog.Builder(RecuperacionPassInPhoneActivity.this)
                                    .content(message)
                                    .positiveText("Ok")
                                    .show();
                        } else
                            forgotPass(EMAIL);
                    }

                    Intent intent = new Intent(RecuperacionPassInPhoneActivity.this, RecuperacionPasswordActivity.class);
                    startActivityForResult(intent,RECUPERACION_PASSWORD);
                    overridePendingTransition(R.anim.left_in,R.anim.left_out);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });*/

    }
}
