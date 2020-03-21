package com.cacaopaycard.cacaopay.Transferencias;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.R;

public class TransferenciaFallidaActivity extends AppCompatActivity {

    private TextView txtMessageError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_fallida);

        txtMessageError = findViewById(R.id.txt_leyenda_transferencia_fallida);

        String message = getIntent().getStringExtra("failure_message");
        txtMessageError.setText(message);



    }

    public void onClickIntentarTarde(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onClickReintentarTransfer(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }
}
