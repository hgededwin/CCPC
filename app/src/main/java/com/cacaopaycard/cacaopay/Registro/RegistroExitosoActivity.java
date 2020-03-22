package com.cacaopaycard.cacaopay.Registro;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.LoginActivity;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;

import static com.cacaopaycard.cacaopay.Constantes.AGREGAR_TARJETA;
import static com.cacaopaycard.cacaopay.Constantes.EDITAR_TARJETA;
import static com.cacaopaycard.cacaopay.Constantes.ENVIO_PASSWORD;
import static com.cacaopaycard.cacaopay.Constantes.RECUPERACION_PASSWORD;
import static com.cacaopaycard.cacaopay.Constantes.REGISTRO;
import static com.cacaopaycard.cacaopay.Constantes.TRANSFERENCIA;

public class RegistroExitosoActivity extends AppCompatActivity {

    TextView leyenda, titulo;
    Button btnContinuar;
    int padre;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_exitoso);

        titulo = findViewById(R.id.txt_registro_exitoso);
        leyenda = findViewById(R.id.txt_leyenda_registro_exitoso);
        btnContinuar = findViewById(R.id.btn_aceptar);
        usuario = new Usuario(this);

        padre = getIntent().getIntExtra("padre",REGISTRO);
        if (padre == TRANSFERENCIA){
            titulo.setText(getString(R.string.str_transferencia_exitosa));
            leyenda.setText(getString(R.string.str_leyenda_transferencia_exitosa));
            btnContinuar.setText(getString(R.string.str_terminar));
        } else if(padre == EDITAR_TARJETA){
            titulo.setText(R.string.str_edicion_exitosa);
            leyenda.setText(R.string.str_leyenda_edicion_exitosa);
            btnContinuar.setText(R.string.str_listo);
        } else if(padre == AGREGAR_TARJETA){
            titulo.setText(R.string.str_adicion_tarjeta_exitosa);
            leyenda.setText(R.string.str_leyenda_tarjeta_agregada);
            btnContinuar.setText(getString(R.string.str_listo));
        } else if(padre == RECUPERACION_PASSWORD){
            titulo.setText(R.string.str_titulo_pass_change);
            leyenda.setText(R.string.str_leyenda_cambio_pass_exitoso);
            btnContinuar.setText(getString(R.string.str_entendido));
        } else if (padre == ENVIO_PASSWORD) {
            titulo.setText("Verifica tu correo electrónico");
            leyenda.setText("Se ha enviado un correo electrónico con las instrucciones para recuperar tu contraseña");
            btnContinuar.setText(getString(R.string.str_entendido));
        } else {
            titulo.setText(R.string.str_registro_exitoso);
            leyenda.setText(R.string.str_texto_registro_terminado);
            btnContinuar.setText(getString(R.string.str_comenzar));
        }

    }

    public void onClickRegistroExitoso(View view) {


        switch(padre){
            case TRANSFERENCIA:
                setResult(RESULT_OK);
                finish();
                break;
            case EDITAR_TARJETA:
                /*Uri dato = Uri.parse("content://titular/" + getIntent().getStringExtra("titular"));

            Intent resultado = new Intent(null, dato);
            resultado.putExtra("titular", titular.getText().toString());
            resultado.putExtra("nombre_tarjeta", nombreTarjeta.getText().toString());*/
                //setResult(RESULT_OK, resultado);
                setResult(RESULT_OK);
                finish();
                break;
            case AGREGAR_TARJETA:
                setResult(RESULT_OK);
                finish();
                break;

            case RECUPERACION_PASSWORD:

                usuario.registrarUsuario();
                setResult(RESULT_OK);
                finish();
                break;

            case ENVIO_PASSWORD:

                setResult(RESULT_OK);
                finish();

            case REGISTRO:
                // registro
                System.out.println(".....Registro exitoso");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
                break;
        }


    }


    @Override
    public void onBackPressed() {

        switch (padre){
            case AGREGAR_TARJETA:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case EDITAR_TARJETA:
                super.onBackPressed();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case RECUPERACION_PASSWORD:
                setResult(RESULT_OK);
                finish();
                break;

                default:

                    break;

        }

    }
}
