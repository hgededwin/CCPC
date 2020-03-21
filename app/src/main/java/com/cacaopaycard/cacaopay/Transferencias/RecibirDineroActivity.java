package com.cacaopaycard.cacaopay.Transferencias;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.R;

public class RecibirDineroActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtClabe, txtNombre, txtNumeroTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibir_dinero);

        toolbar = findViewById(R.id.toolbar_recibir_dinero);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String stp = getIntent().getStringExtra("clabe");
        String numCuenta = getIntent().getStringExtra("num_cuenta");
        String nombreUsuario = getIntent().getStringExtra("nombre");

        txtClabe = findViewById(R.id.txt_clabe);
        txtNombre = findViewById(R.id.txt_nombre_titular_recibir);
        txtNumeroTarjeta = findViewById(R.id.txt_numero_cuenta_recibir);

        txtClabe.setText(stp);
        txtNumeroTarjeta.setText(numCuenta);
        txtNombre.setText(nombreUsuario);


    }

    public void onClickCompartirDatos(View view) {

        String messageForSend = getString(R.string.str_mensaje_para_compartir,txtNumeroTarjeta.getText().toString(),txtClabe.getText().toString());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, messageForSend);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    public void onClickCancelarRecibirDinero(View view) {
        onBackPressed();
    }
}
