package com.cacaopaycard.cacaopay.mvp.cardInfo;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.Modelos.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cacaopaycard.cacaopay.Constantes.APP_ID;
import static com.cacaopaycard.cacaopay.mvp.util.URLCacao.URL_CARD_MOVEMENTS;
import static com.cacaopaycard.cacaopay.mvp.util.URLCacao.URL_USER_DATA;

public class CardInfoInteractor {

    private final String TAG = "CardInfoInteractor";

    private RequestQueue requestQueue;
    private OnFinishCardInfoRequest listener;


    public CardInfoInteractor(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getCards(final OnFinishCardInfoRequest listener, Usuario user){
        this.listener = listener;

        Map<String, String> params = new HashMap<>();
        params.put("phone", user.getTelefono());
        params.put("id_app", APP_ID);
        requestData(WebService.USER_DATA, params);
    }

    public void getCardMovements(final OnFinishCardInfoRequest listener,final Tarjeta card){

        this.listener = listener;

        Map<String, String> params = new HashMap<>();
        params.put("card_number", card.getNumeroCuenta());
        requestData(WebService.CARD_MOVEMENTS, params);

    }

    /**
     * Request handler
     * */
    private void requestData(final WebService service, final Map<String, String> params){
        String url = "";

        switch (service){
            case USER_DATA:
                url = URL_USER_DATA;
                break;
            case CARD_MOVEMENTS:
                url = URL_CARD_MOVEMENTS;
                break;
        }


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG,response);
                    processResponse(response, service);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError("Ocurrió un error al procesar la información del ususario, por favor inténtalo de nuevo.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                listener.onError("Ocurrió un error al obtener la información del ususario, por favor inténtalo de nuevo.");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void processResponse(String response, WebService service) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        int success = jsonObject.getInt("succes");

        final int SUCCESS = 1;
        final int FAIL = 0;

        switch (success){

            case SUCCESS:

                switch (service){
                    case USER_DATA:
                        processUserInfo(jsonObject.getJSONObject("message"));
                        processUserCards(jsonObject.getJSONArray("cards"));
                        break;
                    case CARD_MOVEMENTS:
                        processCardMovements(jsonObject);
                        break;
                }

                break;
            case FAIL:
                listener.onError(jsonObject.getString("message"));
                break;
            default:
                listener.onError("Ocurrió un error inesperado, por favor inténtalo de nuevo.");
                break;
        }
    }

    private void processUserInfo(JSONObject userInfo) throws JSONException {
        //user.setTelefono(userInfo.getString("phone"));
    }

    private void processUserCards(JSONArray userCards) throws JSONException {

        List<Tarjeta> cardList = new ArrayList<>();

        for (int i = 0; i < userCards.length(); i++){
            JSONObject cardObject = userCards.getJSONObject(i);
            Tarjeta card = new Tarjeta(
                    cardObject.getString("card"),
                    cardObject.getString("nickname"),
                    cardObject.getString("name")
            );
            cardList.add(card);
        }
        if (cardList.size() > 0){
            listener.onSuccessCardList(cardList);
        } else{
            listener.onEmptyCards();
        }

    }

    private void processCardMovements(JSONObject jsonObject) throws JSONException {

        List<Movimiento> movimientoList = new ArrayList<>();
        List<Fecha> fechas = new ArrayList<>();
        List<String> fechasTemp = new ArrayList<>();

        JSONArray arrayTransito = jsonObject.getJSONArray("transito");
        //  NO SE ESTAN MOSTRANDO MOVIMIENTOS EN TRANSITO

        JSONArray arrayMovements = jsonObject.getJSONObject("message")
                .getJSONObject("response")
                .getJSONObject("consulta_movimientos_response")
                .getJSONObject("return")
                .getJSONArray("respuesta_movimientos");

        if(arrayMovements.length() == 0){
            listener.onEmptyMovements();
            Log.e(TAG,"Emptyyy");
        } else {

            movimientoList = new ArrayList<>();

            for (int i = 0; i < arrayMovements.length(); i++) {

                String monto = arrayMovements.getJSONObject(i).getString("monto");
                String tipo = arrayMovements.getJSONObject(i).getString("tipo");//  Concepto
                String fechaObject = arrayMovements.getJSONObject(i).getString("fecha");
                String dia = fechaObject.substring(6);
                String mes = fechaObject.substring(4,6);
                String anio = fechaObject.substring(0,4);
                boolean transferRecibida = !monto.contains("-");


                Fecha fecha = new Fecha(dia, mes, anio);

                if (!fechasTemp.contains(fechaObject)) {
                    fechasTemp.add(fechaObject);
                    fechas.add(fecha);
                }

                Movimiento movimiento = new Movimiento(tipo, fecha, monto, transferRecibida);
                movimientoList.add(movimiento);

            }
            listener.onSuccessCardMovements(movimientoList, fechas);

        }
    }

    public interface OnFinishCardInfoRequest {
        void onSuccessCardList(List<Tarjeta> cards);
        void onSuccessCardMovements(List<Movimiento> movimientos, List<Fecha> fechas);
        void onError(String error);
        void onEmptyMovements();
        void onEmptyCards();
    }

    private enum WebService {
        USER_DATA,
        CARD_MOVEMENTS
    }
}
