package com.cacaopaycard.cacaopay.Registro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.LoginActivity;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Utils.MaskEditText;
import com.cacaopaycard.cacaopay.Utils.MaskTransformationMethod;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.Constantes.HAY_CUENTA;
import static com.cacaopaycard.cacaopay.Constantes.REGISTRO;
import static com.cacaopaycard.cacaopay.Constantes.USER_REGISTERED;



public class RegistroActivity extends AppCompatActivity {

    private EditText edtxtNombre, edtxtCorreo, edtxtTelefono;
    private MaskEditText edtxtNumeroTarjeta;
    private Toolbar toolbar;
    private Singleton singleton;
    private RequestQueue requestQueue;
    private Usuario usuario;
    private TextInputLayout tilNombre, tilTelefono, tilcorreo , tilNumeroTarjeta;
    private static final String TAG_REGISTRO = "RegistroActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();
        usuario = new Usuario(this);

        edtxtNombre = findViewById(R.id.edtxt_nombre_registro);
        edtxtTelefono = findViewById(R.id.edtxt_telefono_registro);
        edtxtCorreo = findViewById(R.id.edtxt_email_registro);
        edtxtNumeroTarjeta = findViewById(R.id.edtxt_num_card_registro);
        edtxtNumeroTarjeta.setTransformationMethod(new MaskTransformationMethod());
        tilNombre = findViewById(R.id.til_nombre);
        tilTelefono = findViewById(R.id.til_telefono);
        tilcorreo = findViewById(R.id.til_email);
        tilNumeroTarjeta = findViewById(R.id.til_numero_tarjeta);



        // Visibilidad de lo que haya en las tarjetas,
        edtxtNumeroTarjeta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;


                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtxtNumeroTarjeta.getRight() - edtxtNumeroTarjeta.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        if(edtxtNumeroTarjeta.isVisible){

                            // Se esconde y se coloca la máscara personalizada
                            Drawable img = VectorDrawableCompat.create(getResources(), R.drawable.ic_eye_no_visibility,null);
                            img.setBounds(0,0,35,35);
                            edtxtNumeroTarjeta.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                            edtxtNumeroTarjeta.setTransformationMethod(new MaskTransformationMethod());
                            edtxtNumeroTarjeta.isVisible = false;
                        } else {
                            // Se muestra y se quita la máscara
                            //Drawable img = RegistroActivity.this.getResources().getDrawable(R.drawable.ic_eye_visibility);
                            Drawable img = VectorDrawableCompat.create(getResources(), R.drawable.ic_eye_visibility,null);
                            img.setBounds(0,0,35,35);
                            edtxtNumeroTarjeta.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);

                            edtxtNumeroTarjeta.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edtxtNumeroTarjeta.isVisible = true;

                        }

                    }
                }
                return false;

            }
        });


        edtxtNumeroTarjeta.completeEntry = new MaskEditText.CompleteEntry() {
            @Override
            public void isCardComplete(String textFinal, boolean cardIsCorrect) {
                System.out.println("text final: " + textFinal);
                tilNumeroTarjeta.setError(null);

            }
        };

        if(!permisosConcedidos()){
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.app_name))
                    .content(getString(R.string.str_pedir_permisos))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ActivityCompat.requestPermissions(RegistroActivity.this,new String[]{CAMERA,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},1);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            RegistroActivity.this.finish();
                        }
                    })
                    .positiveText("Aceptar")
                    .negativeText("Cancelar")
                    .cancelable(false)
                    .show();
        }


        edtxtNombre.addTextChangedListener(new TextWatcher() {
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

        edtxtCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilcorreo.setError(null);
            }
        });


    }


    public void onClickIngresar(View view) {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivityForResult(intentLogin, HAY_CUENTA);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);

    }


    public boolean permisosConcedidos(){

        if(ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED)
            return false;
        if(ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return false;
        if(ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;

    }
    private void crearDialogoPermisos(){
        new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .content(R.string.str_permisos_no_otorgados)
                .positiveText(R.string.str_aceptar)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RegistroActivity.this.finish();
                    }
                })
                .cancelable(false)
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) crearDialogoPermisos();
            else if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_DENIED) crearDialogoPermisos();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == REGISTRO){
            finish();
        }
        if(resultCode == USER_REGISTERED && requestCode == REGISTRO){
            finish();
        }
        if(resultCode == RESULT_OK && requestCode == HAY_CUENTA){

            System.out.println("Terminando registro");
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }


    }


    public void validaciones(){
        if(edtxtNombre.getText().toString().isEmpty()){
            tilNombre.setError("Debe ingresar el nombre de usuario");

        } else if(edtxtTelefono.getText().toString().isEmpty()){
            tilTelefono.setError("Debe ingresar el número teléfono");

        } else if(edtxtCorreo.getText().toString().isEmpty()){
            tilcorreo.setError("Debe ingresar el correo");

        } else if(edtxtNumeroTarjeta.getRowText().isEmpty()){
            tilNumeroTarjeta.setError("Debe ingresar el número detarjeta");
        } else{
            //registroRequest();
            Bundle bundle = new Bundle();
            bundle.putString("telefono",edtxtTelefono.getText().toString());
            bundle.putString("correo", edtxtCorreo.getText().toString());
            bundle.putString("tarjeta", edtxtNumeroTarjeta.getRowText());
            bundle.putString("nombre", edtxtNombre.getText().toString());

            Intent intent = new Intent(RegistroActivity.this, SetPasswordActivity.class);
            intent.putExtra("flujo", REGISTRO);
            intent.putExtra("registro", bundle); // bundle con información del registro
            Log.d(TAG_REGISTRO, "activityForResult init");
            startActivityForResult(intent,REGISTRO);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);

            Log.d(TAG_REGISTRO, "setters success");
        }
    }

    public void registroRequest(){

        final Peticion peticionRegistro = new Peticion(this,requestQueue);
        peticionRegistro.addParams(getString(R.string.phone_params), edtxtTelefono.getText().toString());
        peticionRegistro.addParams(getString(R.string.device_uuid_params), Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        peticionRegistro.addParams(getString(R.string.email_params),edtxtCorreo.getText().toString());
        peticionRegistro.addParams(getString(R.string.name_params),edtxtNombre.getText().toString());
        peticionRegistro.addParams(getString(R.string.card_number_param), edtxtNumeroTarjeta.getRowText());
        peticionRegistro.addParams(getString(R.string.app_id_params),APP_ID);
        peticionRegistro.addParams(getString(R.string.phone_code_params),"");

        peticionRegistro.stringRequest(Request.Method.POST, getString(R.string.url_registro), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionRegistro.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");

                    System.out.println(jsonObject);
                    if(success == 1){
                        Log.i(Constantes.TAG, message);
                        Intent intent = new Intent(RegistroActivity.this, VerificacionActivity.class);
                        startActivityForResult(intent,REGISTRO);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        usuario.setTelefono(edtxtTelefono.getText().toString());
                        usuario.setNombreUsuario(edtxtNombre.getText().toString());
                        usuario.setCorreo(edtxtCorreo.getText().toString());

                        //definir parámetros telefono, etc


                    } else{
                        Log.e(Constantes.TAG, message);
                        new MaterialDialog.Builder(RegistroActivity.this)
                                .title("¡Error!")
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


    public void onClickRegistro2(View view) {
        validaciones();
    }
}
