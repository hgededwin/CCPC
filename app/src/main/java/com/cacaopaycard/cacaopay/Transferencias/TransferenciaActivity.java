package com.cacaopaycard.cacaopay.Transferencias;

import android.content.Intent;
import androidx.annotation.Nullable;

import com.cacaopaycard.cacaopay.mvp.util.URLCacao;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.Enums;
import com.cacaopaycard.cacaopay.Modelos.DialogFragmentPIN;
import com.cacaopaycard.cacaopay.Modelos.Peticion;
import com.cacaopaycard.cacaopay.Modelos.Transferencia;
import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Registro.RegistroExitosoActivity;
import com.cacaopaycard.cacaopay.Utils.MaskEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.Constantes.TRANSFERENCIA;
import static com.cacaopaycard.cacaopay.Constantes.TRANSFERENCIA_FALLIDA;

public class TransferenciaActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView txtNumCuentacv, txtSaldo;
    private EditText edtxtNumRef, edtxtConcepto, edtxtNomBene;
    private TextInputLayout tilCard, tilConcepto, tilMonto, tilReferencia, tilNomBene;
    private MaskEditText edtxtNumCardSend;
    private CurrencyEditText edtxtMonto;
    private List<String> tipoCuentas = new ArrayList(){{add("Cuenta CLABE"); add("Tarjeta CACAO");}};
    private RequestQueue requestQueue;
    private Usuario usuario;
    private boolean esInterbancaria = false;
    private String numTarjetaEmisora, strNumTelefomo, strSaldo;
    private final String TAG = "TRANSFERENCIAPROPIAS";
private final String TAG_SPEI = "TRANSFERENCIASPEI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        numTarjetaEmisora = getIntent().getStringExtra("num_tarjeta_emisora");
        strNumTelefomo = getIntent().getStringExtra("telefono_transfer");
        strSaldo = getIntent().getStringExtra("saldo");
        setUpUI();

        requestQueue = Volley.newRequestQueue(this);
        usuario = new Usuario(this);

    }

    public void onClickHacerTransferencia(View view) {
        if(edtxtNumCardSend.getRowText().isEmpty()){
            tilCard.setError("Debe llenar el campo");
        } else if((!validacionBIN() && !esInterbancaria )|| tilCard.getError() != null && !esInterbancaria){
            tilCard.setError("Ingrese una tarjeta válida");
            edtxtNumCardSend.requestFocus();
        } else if(edtxtMonto.getRawValue() == 0){
            tilMonto.setError("Debe ingresar un monto");
        } else if(!edtxtNumRef.getText().toString().matches("[0-9]*")){
            tilReferencia.setError("Solo se pueden ingresar números");
        } else if(edtxtConcepto.getText().toString().isEmpty()){
            tilConcepto.setError("Debe ingresar un concepto");
        } else if(edtxtNomBene.getText().toString().isEmpty()){
            tilNomBene.setError("Debes de ingresar un nombre de beneficiario");
        } else if(!esInterbancaria){

            Log.i(Constantes.TAG, "No es interbancaria");
            initTransferOwnerRequest();

        } else{
            Log.i(Constantes.TAG, "Es interbancaria");
            transferThirdsRequest();

        }
    }

    public boolean validacionBIN(){
        if(edtxtNumCardSend.getRowText().matches("(54392400+[0-9]{8})"))
            return true;
        if(edtxtNumCardSend.getRowText().matches("(53392200+[0-9]{8})"))
            return true;
        if(edtxtNumCardSend.getRowText().matches("(54392431+[0-9]{8})"))
            return true;

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == TRANSFERENCIA_FALLIDA && resultCode == RESULT_CANCELED){
            finish();
        } else if(requestCode == TRANSFERENCIA_FALLIDA && resultCode == RESULT_OK){
            // reenviar transferencia
        } else if (requestCode == TRANSFERENCIA && resultCode == RESULT_OK){
            this.setResult(RESULT_OK);
            this.finish();
        }
        else {
            finish();
        }

    }

    public boolean validacionConcepto(String concepto){
        String regex = "^[\\x20-\\x3B\\x3F-\\x5A\\\\_\\x61-\\x7AáéíóúñÑ¿¡]+$";

        if(concepto.matches(regex) && concepto.length() < 40){

            String conceptoSinDiagonales = concepto.replaceAll("\\\\x5C"," ");
            String conceptSinEspacios = conceptoSinDiagonales.replaceAll(" ","");

            if(conceptSinEspacios.length() == 0) return false;
            else return true;

        } else
            return false;

    }


    public void onClickCancelarTransferencia(View view) {
        onBackPressed();
    }

    public void initTransferOwnerRequest(){

        System.out.println("......initTransferOwnerRequest");

        final Peticion peticionTransfer = new Peticion(this,requestQueue);
       /* peticionTransfer.addParamsString(getString(R.string.tarjeta_emisora_param), numTarjetaEmisora);
        peticionTransfer.addParamsString(getString(R.string.tarjeta_receptora_param),edtxtNumCardSend.getRowText());
        peticionTransfer.addParamsString(getString(R.string.monto_param), edtxtMonto.getText().toString().replaceAll("[$|,]",""));
        peticionTransfer.addParamsString(getString(R.string.num_refer_param), edtxtNumRef.getText().toString());
        peticionTransfer.addParamsString(getString(R.string.app_id_params),APP_ID);
        peticionTransfer.addParamsString(getString(R.string.phone_params), usuario.getTelefono()); */

        peticionTransfer.addParamsString("TarjetaOrigen", numTarjetaEmisora);
        peticionTransfer.addParamsString("TarjetaDestino", edtxtNumCardSend.getRowText());
        peticionTransfer.addParamsString("Importe", edtxtNumCardSend.getText().toString().replaceAll("[$|,]", ""));
        peticionTransfer.addParamsString("ClaveMovimiento", "");
        peticionTransfer.addParamsString("RefNumerica", edtxtNumRef.getText().toString());
        peticionTransfer.addParamsString("Observaciones", "N/A");

        peticionTransfer.jsonObjectRequest(Request.Method.POST, URLCacao.URL_TRANSFERENCIAS_CACAO, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                peticionTransfer.dismissProgressDialog();
                Log.e(TAG,response.toString());

                try {
                    System.out.println("Response..." );
                    JSONObject jsonObject = new JSONObject(response.toString());
                    System.out.println("....jsonObject:" + jsonObject);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");

                    if (success == 1){
                        Log.i(Constantes.TAG, message);

                        JSONObject datos = jsonObject.getJSONObject("datos");

                        final String idTrx = String.valueOf(datos.getInt("id"));
                        final String idClient = String.valueOf(datos.getInt("admin_user_id"));

                        DialogFragmentPIN dialog = DialogFragmentPIN.instanceDialog(TransferenciaActivity.this,new DialogFragmentPIN.DialogPositive() {
                            @Override
                            public void listenerPositiveText(DialogFragment dialog, String otp) {

                                System.out.println("...OTP _transfer:"+ otp);
                                confirmTransferOwner(idTrx,idClient,otp);

                            }
                        }, getString(R.string.str_ingrese_pin_leyenda), getString(R.string.str_pin));
                        dialog.show(getSupportFragmentManager(), "CacaoPay");



                    } else {
                        Log.e(Constantes.TAG, message);

                        new MaterialDialog.Builder(TransferenciaActivity.this)
                                .content(message)
                                .positiveText("OK")
                                .cancelable(false)
                                .show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    public void confirmTransferOwner(String trx,String idClient, String otp){

        System.out.println("....confirmTransferOwner");
        final Peticion peticionTrx = new Peticion(this, requestQueue);
        peticionTrx.addParams(getString(R.string.trx_param),trx);
        peticionTrx.addParams(getString(R.string.id_client_param),idClient); // ****
        peticionTrx.addParams(getString(R.string.pin_param),otp);
        peticionTrx.stringRequest(Request.Method.POST, getString(R.string.url_tranfer_send), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                peticionTrx.dismissProgressDialog();
                Log.e("TAG",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("succes");
                    String message = jsonObject.getString("message");

                    if(success == 1){
                        Log.i(Constantes.TAG, "transfer owner account");

                        Intent intent = new Intent(TransferenciaActivity.this, TransferenciaExitosaActivity.class);

                        Transferencia transferenciaRealizada  = new Transferencia();

                        Date date = new Date();

                        //  fecha
                        transferenciaRealizada.setFecha(new SimpleDateFormat("dd/MMM/yyyy").format(date));
                        //  HORA
                        transferenciaRealizada.setHora(new SimpleDateFormat("h:mm a").format(date));
                        //  DESTINO NOMBRE
                        transferenciaRealizada.setNombredestino(edtxtNomBene.getText().toString());
                        //  DESTINO ACC
                        transferenciaRealizada.setCuentaDestino(edtxtNumCardSend.getText().toString());

                        //  CANTIDAD
                        transferenciaRealizada.setMonto(edtxtMonto.getText().toString());

                        //  CUENTA ORIGEN
                        transferenciaRealizada.setCuentaOrigen(numTarjetaEmisora);

                        //  RASTREO
                        transferenciaRealizada.setNumeroRastreo("XXXXXXX");

                        intent.putExtra("datos_transferecias", (Serializable) transferenciaRealizada);
                        intent.putExtra("tipo_transfeencia", Enums.DEBITO);
                        startActivityForResult(intent, TRANSFERENCIA);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                    } else{
                        Log.e(Constantes.TAG, message);

                        Intent intentFallido = new Intent(TransferenciaActivity.this, TransferenciaFallidaActivity.class);
                        intentFallido.putExtra("failure_message", message);
                        startActivityForResult(intentFallido, TRANSFERENCIA_FALLIDA);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    Intent intentFallido = new Intent(TransferenciaActivity.this, TransferenciaFallidaActivity.class);
                    //intentFallido.putExtra("failure_message", otpWrite);
                    //intentFallido.getBundleExtra("");
                    startActivityForResult(intentFallido, TRANSFERENCIA_FALLIDA);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }

            }
        });

    }


    // implementar con jsonObject por por tipos de datos distintos entre si.
    public void transferThirdsRequest(){
        System.out.println(".....transferThirdsRequest");
        System.out.println("Monto: " + edtxtMonto.getText().toString().replaceAll("[$|,]","")  + " Decimal: " + edtxtMonto.getDecimalDigits());

        final Peticion peticionTransferOthers = new Peticion(this,requestQueue);
       /* peticionTransferOthers.addParamsString(getString(R.string.monto_param),edtxtMonto.getText().toString().replaceAll("[$|,]",""));
        peticionTransferOthers.addParamsString(getString(R.string.concepto_param),edtxtConcepto.getText().toString());
        peticionTransferOthers.addParamsString(getString(R.string.referencia_numerica_param),edtxtNumRef.getText().toString());
        peticionTransferOthers.addParamsString(getString(R.string.nombre_beneficiario_param), edtxtNomBene.getText().toString());
        peticionTransferOthers.addParamsString(getString(R.string.tarjeta_emisora_2_param),numTarjetaEmisora);
        peticionTransferOthers.addParamsString(getString(R.string.cuenta_beneficiario), edtxtNumCardSend.getRowText());
        peticionTransferOthers.addParamsString(getString(R.string.phone_params), strNumTelefomo);
        peticionTransferOthers.addParamsString(getString(R.string.app_id_params), APP_ID); */

        peticionTransferOthers.addParamsString("Tarjeta", numTarjetaEmisora);
        peticionTransferOthers.addParamsString("NombreBeneficiario", edtxtNomBene.getText().toString());
        peticionTransferOthers.addParamsString("CuentaBeneficiario", edtxtNumCardSend.getText().toString());
        peticionTransferOthers.addParamsString("RfcCurpBeneficiario", "RFC DE ALGN LUGAR");
        peticionTransferOthers.addParamsString("ConceptoPago", edtxtConcepto.getText().toString());
        peticionTransferOthers.addParamsString("ReferenciaNumerica", edtxtNumRef.getText().toString());
        peticionTransferOthers.addParamsString("Monto", edtxtMonto.getText().toString());
        peticionTransferOthers.addParamsString("EMailBeneficiario", "NO EXISTE EMIAL BENEFICICARI INPUT");

        peticionTransferOthers.jsonObjectRequest(Request.Method.POST, getString(R.string.url_tranfer_terceros), new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                peticionTransferOthers.dismissProgressDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    Log.e(Constantes.TAG, jsonObject.toString());
                    if(success == 1){
                        Log.i(Constantes.TAG, message);
                        Intent intent = new Intent(TransferenciaActivity.this, TransferenciaExitosaActivity.class);
                        Transferencia transferenciaRealizada  = new Transferencia();

                        Date date = new Date();

                        //  fecha
                        transferenciaRealizada.setFecha(new SimpleDateFormat("dd/MMM/yyyy").format(date));
                        //  HORA
                        transferenciaRealizada.setHora(new SimpleDateFormat("h:mm a").format(date));
                        //  DESTINO NOMBRE
                        transferenciaRealizada.setNombredestino(edtxtNomBene.getText().toString());
                        //  DESTINO ACC
                        transferenciaRealizada.setCuentaDestino(edtxtNumCardSend.getRowText());

                        //  CANTIDAD
                        transferenciaRealizada.setMonto(edtxtMonto.getText().toString());

                        //  CUENTA ORIGEN
                        transferenciaRealizada.setCuentaOrigen(numTarjetaEmisora);

                        //  RASTREO
                        transferenciaRealizada.setNumeroRastreo("XXXXXXX");

                        intent.putExtra("datos_transferecias", (Serializable) transferenciaRealizada);

                        intent.putExtra("tipo_transfeencia", Enums.CLABE);
                        intent.putExtra("padre", TRANSFERENCIA);
                        startActivityForResult(intent, TRANSFERENCIA);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                    } else {
                        Log.e(Constantes.TAG, message);



                        Intent intentFallido = new Intent(TransferenciaActivity.this, TransferenciaFallidaActivity.class);
                        intentFallido.putExtra("failure_message", message);
                        startActivityForResult(intentFallido, TRANSFERENCIA_FALLIDA);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public int generateNumberReference(){

        int numberRandom = 0;

        while(String.valueOf(numberRandom).length() != 7){
            numberRandom = (int)(Math.random() * 9999999) + 1111111;
        }
        //System.out.println("Number Random:" + numberRandom);

        return numberRandom;

    }

    public void setEnableInputs(boolean enable){
        tilCard.setEnabled(enable);
        tilMonto.setEnabled(enable);
        tilNomBene.setEnabled(enable);
        tilReferencia.setEnabled(enable);
        tilConcepto.setEnabled(enable);
    }

    public void onClikMostrarTipoCuenta(View view) {
        new MaterialDialog.Builder(this)
                .title("Tipo de cuenta")
                .positiveText(R.string.str_aceptar)
                .items(tipoCuentas)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        //edtxtTipoCuenta.setText(text);
                        setEnableInputs(true);

                        if(which == 0){
                            //transferThirdsRequest();
                            /*bancosPrueba();
                            tilBanco.setVisibility(View.VISIBLE);*/
                            esInterbancaria = true;
                            edtxtNumCardSend.setInputType(InputType.TYPE_CLASS_NUMBER);
                            edtxtNumCardSend.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                            edtxtNumCardSend.setText("");
                            edtxtNumCardSend.completeEntry = new MaskEditText.CompleteEntry() {
                                @Override
                                public void isCardComplete(String textFinal, boolean cardIsCorrect) {
                                    if(textFinal.matches("[0-9]{18}"))
                                        tilCard.setError(null);
                                }
                            };
                            tilCard.setHint(getString(R.string.str_tarjeta_clabe_transferir));

                        } else{
                            esInterbancaria = false;
                            edtxtNumCardSend.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                            edtxtNumCardSend.setInputType(InputType.TYPE_CLASS_PHONE);
                            edtxtNumCardSend.setText("");
                            tilCard.setHint(getString(R.string.str_tarjeta_cuenta_cacao));
                            edtxtNumCardSend.completeEntry = new MaskEditText.CompleteEntry() {
                                @Override
                                public void isCardComplete(String textFinal, boolean cardIsCorrect) {
                                    if (cardIsCorrect) {
                                        tilCard.setError(null);
                                        if (edtxtNumCardSend.getRowText().matches("(54392415+[0-9]{8})")){
                                            // tarjeta aceptable
                                        } else{
                                            // La tarjeta no pertenece a cacao
                                            tilCard.setError("La tarjeta no pertenece a cacao");
                                        }

                                    } else
                                        tilCard.setError("Error en la inserción de la tarjeta");
                                }
                            };
                        }
                        return false;
                    }
                }).show();
    }



    private void setUpUI(){

        toolbar = findViewById(R.id.toolbar_transferencias);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        edtxtNumCardSend = findViewById(R.id.edtxt_numero_tarjeta_transferencia);
        edtxtMonto = findViewById(R.id.edtxt_monto_transferencia);
        edtxtNumRef = findViewById(R.id.edtxt_numero_referencia_transfer);
        edtxtConcepto = findViewById(R.id.edtxt_concepto_transferencia);
        tilConcepto = findViewById(R.id.til_concepto);
        tilMonto = findViewById(R.id.til_monto);
        tilCard = findViewById(R.id.til_tarjeta_transfer);
        tilReferencia = findViewById(R.id.til_numero_referencia);
        tilNomBene = findViewById(R.id.til_nombre_beneficiario);
        edtxtNomBene = findViewById(R.id.edtxt_nombre_beneficiario_trf);
        txtNumCuentacv = findViewById(R.id.txt_numero_cuenta_cv);
        txtSaldo = findViewById(R.id.txt_saldo_tarjeta_cv_transferencia);



        txtNumCuentacv.setText("**** " + numTarjetaEmisora.substring(12));
        txtSaldo.setText(strSaldo);

        edtxtNumRef.setText(String.valueOf(generateNumberReference()));

        edtxtMonto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilMonto.setError(null);
            }
        });

        edtxtNumRef.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilReferencia.setError(null);
            }
        });

        edtxtConcepto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilConcepto.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtxtNumCardSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilCard.setError(null);
            }
        });

        edtxtNomBene.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tilNomBene.setError(null);
            }
        });


        edtxtNumCardSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            boolean entradaCorrecta = false;
            @Override
            public void onFocusChange(View view, boolean b) {

                if(b) {
                    edtxtNumCardSend.completeEntry = new MaskEditText.CompleteEntry() {
                        @Override
                        public void isCardComplete(String textFinal, boolean inputIsCorrect) {

                            tilCard.setError(null);
                            if (inputIsCorrect) {
                                entradaCorrecta = true;
                                System.out.println("texto correcto" + textFinal);
                                if(textFinal.replaceAll(" ","").matches("[0-9]{18}"))
                                    esInterbancaria = true;
                                else
                                    esInterbancaria = false;
                            } else
                                entradaCorrecta = false;
                        }
                    };
                } else {

                    if (!entradaCorrecta)
                        tilCard.setError("Longitud no válida");
                }

            }
        });


    }
}
