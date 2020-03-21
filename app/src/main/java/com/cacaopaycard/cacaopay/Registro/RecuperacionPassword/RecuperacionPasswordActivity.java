package com.cacaopaycard.cacaopay.Registro.RecuperacionPassword;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Registro.SetPasswordActivity;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.Constantes.CHANGED_PIN_CANCELED;
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;

public class RecuperacionPasswordActivity extends AppCompatActivity {

    private OtpView otpRecuperacion;
    private Singleton singleton;
    private RequestQueue requestQueue;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();

        usuario = new Usuario(this);
        setContentView(R.layout.activity_recuperacion_password);
        otpRecuperacion = findViewById(R.id.otp_codigo_recuperacion);
        otpRecuperacion.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                Intent intent = new Intent(RecuperacionPasswordActivity.this, SetPasswordActivity.class);
                intent.putExtra("flujo", RECUPERACION_PASSWORD);
                intent.putExtra("pin",otp);
                startActivityForResult(intent,RECUPERACION_PASSWORD);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);

            }
        });
    }

    public void onClickCodigoRecuperacion(View view) {
        Intent intentRecuperacion = new Intent(this, SetPasswordActivity.class);
        intentRecuperacion.putExtra("flujo", RECUPERACION_PASSWORD);
        intentRecuperacion.putExtra("otp",otpRecuperacion.getText().toString());
        startActivityForResult(intentRecuperacion,RECUPERACION_PASSWORD);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == RECUPERACION_PASSWORD ){
            setResult(RESULT_OK);
            finish();
        }
        if(requestCode == RECUPERACION_PASSWORD && resultCode == CHANGED_PIN_CANCELED) {
            setResult(CHANGED_PIN_CANCELED);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }

    public void onClickCodigoEmail(View view) {
        //reenvioPIN();
        Toast.makeText(RecuperacionPasswordActivity.this,"Se mandó el código a tu cuenta de email y teléfono.",Toast.LENGTH_SHORT).show();
        reenvioPIN();
    }



    public void onClickCancelarRecuperacionSMS(View view) {

        Intent intentRecuperacion = new Intent(this, SetPasswordActivity.class);
        intentRecuperacion.putExtra("flujo", RECUPERACION_PASSWORD);
        intentRecuperacion.putExtra("otp",otpRecuperacion.getText().toString());
        startActivityForResult(intentRecuperacion,RECUPERACION_PASSWORD);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);

        setResult(CHANGED_PIN_CANCELED);
        finish();
    }

    public void reenvioPIN(){

        final Peticion peticionPin = new Peticion(this,requestQueue);
        peticionPin.addParams(getString(R.string.phone_params), usuario.getTelefono());
        peticionPin.addParams(getString(R.string.app_id_params),APP_ID);
        peticionPin.stringRequest(Request.Method.POST, getString(R.string.url_reenvio_pin_registro), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionPin.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");

                    if(success == 1) {
                        // pin reenviado
                        Log.i(Constantes.TAG, message);
                        Toast.makeText(RecuperacionPasswordActivity.this, "Se mandó el código a su teléfono", Toast.LENGTH_SHORT).show();

                    } else
                        Log.e(Constantes.TAG, message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
