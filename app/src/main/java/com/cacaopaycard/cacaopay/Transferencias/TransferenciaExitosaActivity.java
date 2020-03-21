package com.cacaopaycard.cacaopay.Transferencias;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cacaopaycard.cacaopay.BuildConfig;
import com.cacaopaycard.cacaopay.Modelos.Transferencia;
import com.cacaopaycard.cacaopay.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransferenciaExitosaActivity extends AppCompatActivity {

    TextView fecha, nombreDestino, hora, cuentadestino, monto, cardOrigen, numRastreo;
    Transferencia transferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_exitosa);

        transferencia = (Transferencia) getIntent().getSerializableExtra("datos_transferecias");
        fecha = findViewById(R.id.txt_fecha_trf);
        hora = findViewById(R.id.txt_hora_trf);
        nombreDestino = findViewById(R.id.txt_destino_trf);
        cuentadestino = findViewById(R.id.txt_cuenta);

        monto = findViewById(R.id.txt_cantidad_trf);
        cardOrigen = findViewById(R.id.txt_card_origin_trf);
        numRastreo = findViewById(R.id.txt_num_rastreo_trf);


        //  fecha
        fecha.setText(transferencia.getFecha());
        //  HORA
        hora.setText(transferencia.getHora());
        //  DESTINO NOMBRE
        nombreDestino.setText(transferencia.getNombredestino());

        //  DESTINO ACC
        cuentadestino.setText(transferencia.getCuentaDestino());

        //  CANTIDAD
        monto.setText(transferencia.getMonto());

        //  CUENTA ORIGEN
        cardOrigen.setText(transferencia.getCuentaOrigen());

        //  RASTREO
        numRastreo.setText(transferencia.getNumeroRastreo());



    }

    public void onClickTerminarTransaccionExitosa(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void onClickCompartirTransfer(View view) {
        takeScreenshot();
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyyMMdd_HHmmss", now);
        //String now = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            System.out.println("takinScreenshot");

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            // image naming and path  to include sd card  appending name you choose for file
            String dirPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "receipt";
            File dir = new File(dirPath);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dirPath,"image" + now + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(file);
            int quality = 85;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(file);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            Toast.makeText(this, "Ocurrió un error al tratar de compartir la captura de pantalla.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openScreenshot(File imageFile) {
        System.out.println("open Screenshot");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",imageFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}



