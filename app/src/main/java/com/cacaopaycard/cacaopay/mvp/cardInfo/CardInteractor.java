package com.cacaopaycard.cacaopay.mvp.cardInfo;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.cacaopaycard.cacaopay.mvp.util.URLCacao.URL_CARD_BALANCE;
import static com.cacaopaycard.cacaopay.mvp.util.URLCacao.URL_LOCK_CARD;

public class CardInteractor {

    private final String TAG = "CardInteractor";

    private RequestQueue requestQueue;
    private Tarjeta mCard;
    private CardBalanceRequest listener;

    public CardInteractor(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }



    public void getCardBalance(final Tarjeta card, final CardBalanceRequest listener){
        this.mCard = card;
        this.listener = listener;

        StringRequest request = new StringRequest(Request.Method.POST, URL_CARD_BALANCE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    processResponse(response);
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
                Map<String, String> params = new HashMap<>();
                params.put("card_number", card.getNumeroCuenta());
                return params;
            }
        };

        requestQueue.add(request);
    }


    public void lockCard(final boolean newStatus, final String card, final CardBalanceRequest listener){
        final String lockUnlockDesired = newStatus ? "28": "00";
        Log.e(TAG,lockUnlockDesired);
        StringRequest request = new StringRequest(Request.Method.POST, URL_LOCK_CARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                try {
                    JSONObject object = new JSONObject(response);
                    int success = object.getInt("succes");

                    switch (success){
                        case 1:
                            listener.onLockUnlockSuccess(newStatus);
                            break;
                        case 0:
                            listener.onError(object.getString("message"));
                            break;
                        default:
                            listener.onError("Ocurrió un error inesperado, por favor inténtalo de nuevo.");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError("Ocurrió un error inesperado, por favor inténtalo de nuevo.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                listener.onError("Ocurrió un error al procesar la solicitud, por favor inténtalo de nuevo.");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("card_number", card);
                params.put("status_desired", lockUnlockDesired);
                return params;
            }
        };

        requestQueue.add(request);
    }


    private void processResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        int success = jsonObject.getInt("succes");

        final int SUCCESS = 1;
        final int FAIL = 0;

        switch (success){

            case SUCCESS:
                processCardBalance(jsonObject);
                break;
            case FAIL:
                listener.onError(jsonObject.getString("message"));
                break;
            default:
                listener.onError("Ocurrió un error inesperado, por favor inténtalo de nuevo.");
                break;
        }
    }

    private void processCardBalance(JSONObject jsonObject) throws JSONException {
        String saldo = jsonObject.getString("saldo");
        String estado = jsonObject.getString("estado");
        String stp = jsonObject.getString("stp");
        mCard.setSaldo(saldo);
        mCard.setEstado(estado);
        mCard.setStp(stp);
        listener.onSuccess(mCard);
    }


    interface CardBalanceRequest{
        void onError(String error);
        void onSuccess(Tarjeta card);
        void onLockUnlockSuccess(boolean newStatus);
    }
}
