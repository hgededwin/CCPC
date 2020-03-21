package com.cacaopaycard.cacaopay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import androidx.annotation.Nullable;

import com.cacaopaycard.cacaopay.mvp.util.URLCacao;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.Registro.RecuperacionPassword.RecuperacionPassInPhoneActivity;
import com.cacaopaycard.cacaopay.Registro.RegistroActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    EditText edtxtTelefono, edtxtPassword;
    TextView tituloBienvenida, txtCrearCuenta;
    TextInputLayout tilTelefono, tilPass;
    boolean isOpened = false;

    private RequestQueue requestQueue;
    private Singleton singleton;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();

        edtxtTelefono = findViewById(R.id.edtxt_telefono_login);
        tituloBienvenida = findViewById(R.id.txt_bienvenida_cacao);
        edtxtPassword = findViewById(R.id.edtxt_password_perfil);
        txtCrearCuenta = findViewById(R.id.txt_crear_cuenta);
        tilTelefono = findViewById(R.id.til_telefono_login);
        tilPass = findViewById(R.id.til_pass_login);

        usuario = new Usuario(this);

        if(!usuario.getTelefono().isEmpty()){
            edtxtTelefono.setText(usuario.getTelefono());
            edtxtTelefono.setEnabled(false);
            edtxtPassword.requestFocus();

        } else {
            edtxtTelefono.setEnabled(true);
            edtxtTelefono.requestFocus();
        }
        if(usuario.estaRegistrado()){
            txtCrearCuenta.setVisibility(View.GONE);
        }
        //setListenerToRootView();

        edtxtTelefono.addTextChangedListener(new TextWatcher() {
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

        edtxtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilPass.setError(null);
            }
        });






    }


    // escuchando el hide/show keyboard
    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                System.out.println("height:" + heightDiff);
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    //Toast.makeText(getApplicationContext(), "Gotcha!!! softKeyboardup", Toast.LENGTH_SHORT).show();

                    if (!isOpened) {
                        System.out.println("up");
                        //Do two things, make the view top visible and the editText smaller
                    }

                    isOpened = true;
                } else if (isOpened) {
                    //Toast.makeText(getApplicationContext(), "softkeyborad Down!!!", Toast.LENGTH_SHORT).show();
                    System.out.println("down");
                    isOpened = false;
                }
            }
        });
    }



    public void onClickEntrar(View view) {

        if(edtxtTelefono.getText().toString().isEmpty()){
            tilTelefono.setError("Se debe ingresar un númro de teléfono");
        } else if(edtxtPassword.getText().toString().isEmpty()){
            tilPass.setError("Debe ingresar un número de teléfono");
        } else if(!edtxtPassword.getText().toString().matches("[0-9]{6}")){
            tilPass.setError("Su contraseña no es valida");
        } else {

            loginRequest();
        }

    }

    public void onClickCrearCuenta(View view) {
        Intent intentRegistro = new Intent(this, RegistroActivity.class);
        startActivity(intentRegistro);
    }

    public void onClickRecuperarPassword(View view) {
        Intent intenRecuperacion = new Intent(this, RecuperacionPassInPhoneActivity.class);
        startActivityForResult(intenRecuperacion,RECUPERACION_PASSWORD);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);
    }


    public void loginRequest(){

        System.out.println("device" + Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        String uuid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        final Peticion peticionLogin = new Peticion(this, requestQueue);

        //peticionLogin.addParams(getString(R.string.phone_params),edtxtTelefono.getText().toString());
        //peticionLogin.addParams(getString(R.string.device_uuid_params), uuid);
        //peticionLogin.addParams(getString(R.string.app_id_params), APP_ID);
        peticionLogin.addParams("NombreUsuario", edtxtTelefono.getText().toString());
        peticionLogin.addParams(getString(R.string.pass_param),edtxtPassword.getText().toString());


        peticionLogin.stringRequest(Request.Method.POST, URLCacao.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionLogin.dismissProgressDialog();

                Log.e(TAG,response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int success = jsonObject.getInt("success");

                    if (success == 1) {

                        if (!usuario.estaRegistrado())
                            usuario.registrarUsuario();

                        JSONObject jsonObjectLogin = jsonObject.getJSONObject("login");

                        Log.i(Constantes.TAG, jsonObjectLogin.toString());

                        String id = jsonObjectLogin.getString("id");
                        //JSONObject card = jsonObjectLogin.getJSONObject("card");
                        String address = jsonObjectLogin.getString("address");

                        usuario.setTelefono(jsonObjectLogin.getString("phone"));
                        usuario.setCorreo(jsonObjectLogin.getString("email"));
                        usuario.setNombreUsuario(jsonObjectLogin.getString("name"));
                        usuario.setBirthDate(jsonObjectLogin.getString("birth_date"));

                        String urlImage = jsonObjectLogin.getString("avatar");
                        if (urlImage != null)
                            usuario.setUrlAvatar(urlImage);


                        Bundle bundle = new Bundle();
                        bundle.putString("idLogin", id);
                        bundle.putString("address", address);

                        /*Bundle bundleCard = new Bundle();
                        bundleCard.putString("num_card", card.getString("card"));
                        bundleCard.putString("nickname_card", card.getString("nickname"));*/

                        Intent intentEntrar = new Intent(LoginActivity.this, MainContainer.class);
                        intentEntrar.putExtra("params_personal", bundle);
                        //intentEntrar.putExtra("info_card",bundleCard);
                        startActivity(intentEntrar);
                        setResult(RESULT_OK);
                        finish();

                    } else {

                        String message = jsonObject.getString("message");
                        Log.e(Constantes.TAG, message);

                        new MaterialDialog.Builder(LoginActivity.this)
                                .title("Error")
                                .content(message)
                                .positiveText("Ok")
                                .show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                peticionLogin.dismissProgressDialog();

                if(error instanceof AuthFailureError) {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .content("Contraseña incorrecta")
                            .positiveText("OK")
                            .show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!usuario.estaRegistrado() && requestCode == RECUPERACION_PASSWORD && resultCode == RESULT_OK ) {
            setResult(RESULT_OK);
            finish();
        } else if(requestCode == RECUPERACION_PASSWORD && resultCode == RESULT_OK){

            edtxtTelefono.setText(usuario.getTelefono());
            edtxtPassword.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!usuario.estaRegistrado()) {
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    private Bitmap getImagen(String url) {
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
}
