package com.cacaopaycard.cacaopay.Registro;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.mvp.util.URLCacao;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cacaopaycard.cacaopay.Constantes.CHANGED_PIN_CANCELED;
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;
import static com.cacaopaycard.cacaopay.Constantes.REGISTRO;
import static com.cacaopaycard.cacaopay.Constantes.USER_REGISTERED;

public class SetPasswordActivity extends AppCompatActivity {

    TextView txtLeyendaPassword,txtCancelar, txtTituloPass;
    EditText edtxtPass;
    Button btnSetPass;
    private Singleton singleton;
    private RequestQueue requestQueue;
    //private String pin;
    int flujo;
    private Usuario usuario;
    private Bundle bundleRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();
        usuario = new Usuario(this);

        txtLeyendaPassword = findViewById(R.id.txt_mensaje_set_pass);
        txtTituloPass = findViewById(R.id.txt_titulo_nuevo_pass);
        txtCancelar = findViewById(R.id.txt_cancelar_pass);
        btnSetPass = findViewById(R.id.btn_guardar_pass);
        edtxtPass = findViewById(R.id.edtxt_set_pass);

        flujo = getIntent().getIntExtra("flujo", REGISTRO);
        bundleRegistro = getIntent().getBundleExtra("registro");
        //pin = getIntent().getStringExtra("pin");
        //System.out.println(".......PIN:" + pin);

        if(flujo == REGISTRO){

            //txtLeyendaPassword.setVisibility(View.GONE);
            txtTituloPass.setText(R.string.str_crea_password);
            txtCancelar.setVisibility(View.GONE);
            btnSetPass.setText(R.string.str_siguiente);
        } else if(flujo == RECUPERACION_PASSWORD){
            // flujo de recuperación de contraseña.
            txtLeyendaPassword.setVisibility(View.VISIBLE);
            txtTituloPass.setVisibility(View.VISIBLE);
            txtTituloPass.setText(R.string.str_titulo_crea_nuevo_pass);
            //txtLeyendaPassword.setText(R.string.str_leyenda_recuperacion);
            txtCancelar.setVisibility(View.VISIBLE);
            btnSetPass.setText(R.string.str_guardar);
        }

    }


    public void onClickCancelarSetPass(View view) {
        setResult(CHANGED_PIN_CANCELED);
        finish();
    }
    public void onClickEstablecerPwd(View view) {

        System.out.println("registrado?:" + usuario.estaRegistrado());
        if(flujo == REGISTRO && usuario.estaRegistrado()){
            new MaterialDialog.Builder(this)
                    .title("Usuario registrado")
                    .content("Ya existe un usuario registrado")
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            setResult(USER_REGISTERED);
                            finish();
                        }

                    }) .show();
        } else {
            updatePassword();
        }


        // servicios
        /*if (pin != null) {
            updatePassword();
        } else {
            //validación de pin, si existe.
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REGISTRO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
        if(resultCode == RESULT_OK && requestCode == RECUPERACION_PASSWORD){
            setResult(RESULT_OK);
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }


    public void updatePassword(){

        final Peticion setPassRequest = new Peticion(this,requestQueue);
        //setPassRequest.addParamsInteger(getString(R.string.pin_param), Integer.parseInt(pin));
        setPassRequest.addParamsString("NumeroCelular", bundleRegistro.getString("telefono"));
        setPassRequest.addParamsString("Correo", bundleRegistro.getString("correo"));
        setPassRequest.addParamsString("Tarjeta", bundleRegistro.getString("tarjeta"));
        setPassRequest.addParamsString("NombreCompleto", bundleRegistro.getString("nombre"));
        setPassRequest.addParamsString(getString(R.string.pass_param), edtxtPass.getText().toString());


        setPassRequest.jsonObjectRequest(Request.Method.POST, URLCacao.URL_CREAR_CUENTA, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setPassRequest.dismissProgressDialog();
                Log.e(Constantes.TAG, response.toString());
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    Log.e(Constantes.TAG, response.toString());

                    String email = jsonObject.getString("Correo");
                    String codeResponse = jsonObject.getString("ResponseCode");
                    String message = jsonObject.getString("Mensaje");

                    if(codeResponse.equals("00")){
                        Log.i(Constantes.TAG, message);
                        if(flujo == REGISTRO && !usuario.estaRegistrado()) {
                            usuario.registrarUsuario();
                            Intent intent = new Intent(SetPasswordActivity.this, RegistroExitosoActivity.class);
                            intent.putExtra("padre", REGISTRO);

                            usuario.setTelefono(bundleRegistro.getString("telefono"));
                            usuario.setNombreUsuario(bundleRegistro.getString("nombre"));
                            usuario.setCorreo(bundleRegistro.getString("correo"));

                            startActivityForResult(intent, REGISTRO);
                            overridePendingTransition(R.anim.left_in,R.anim.left_out);

                        } else {
                            Intent intent = new Intent(SetPasswordActivity.this, RegistroExitosoActivity.class);
                            intent.putExtra("padre", RECUPERACION_PASSWORD);
                            startActivityForResult(intent, RECUPERACION_PASSWORD);
                            overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        }

                    } else {
                        Log.e(Constantes.TAG, message);
                        new  MaterialDialog.Builder(SetPasswordActivity.this)
                                .title("¡Error!")
                                .content(message)
                                .positiveText("Ok")
                                .show();
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
