package com.cacaopaycard.cacaopay.Registro.RecuperacionPassword;

import android.content.Intent;
import androidx.annotation.Nullable;
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
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;

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
        if(!edtxtEmailPhone.getText().toString().matches("[0-9]{10}")){

            tilTelefono.setError("teléfono no válido");

        } else if(edtxtEmail.getText().toString().isEmpty()) {
            tilEmail.setError("Debe ingresar el correo");
        } else {

            telefonoTemporal = edtxtEmailPhone.getText().toString();
            forgotPass(PHONE);
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


    public void forgotPass(final int emailPhone){

        final Peticion peticion = new Peticion(this, requestQueue);
        peticion.addParams(getString(R.string.email_phone_params), edtxtEmailPhone.getText().toString());
        peticion.addParams(getString(R.string.app_id_params), APP_ID);

        peticion.stringRequest(Request.Method.POST, getString(R.string.url_forgot_pass), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticion.dismissProgressDialog();
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

                    /*Intent intent = new Intent(RecuperacionPassInPhoneActivity.this, RecuperacionPasswordActivity.class);
                    startActivityForResult(intent,RECUPERACION_PASSWORD);
                    overridePendingTransition(R.anim.left_in,R.anim.left_out);
*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
