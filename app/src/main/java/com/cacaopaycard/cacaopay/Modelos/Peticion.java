package com.cacaopaycard.cacaopay.Modelos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Peticion {
    Context context;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    MaterialDialog.Builder dialog;
    Map<String, String> headers = new HashMap<>();
    Map<String, String> params = new HashMap<>();

    JSONObject jsonObject = new JSONObject();
    Boolean mostrarProgressDialog = true;
    String errorMessage = "";
    Usuario usuario;

    public Peticion(final Context context, RequestQueue requestQueue) {
        this.context = context;
        this.requestQueue = requestQueue;

        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando");
        dialog = new MaterialDialog.Builder(context)
                .positiveText(R.string.str_aceptar)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (errorMessage){
                            case "Error en el registro volver a Iniciar":
                                Intent intent = new Intent(context, SplashActivity.class);
                                ((Activity) context).setResult(RESULT_OK);
                                ((Activity) context).finish();
                                context.startActivity(intent);
                                break;
                        }
                    }
                });
        usuario = new Usuario(context);
    }

    public void stringRequest(int methodRequest, String url, Response.Listener<String> listener){
        progressDialog.dismiss();
        final StringRequest stringRequest = new StringRequest(
                methodRequest,
                url,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError)
                        {
                            Log.e(Constantes.TAG,error.toString());
                            dialog.content("Error de conexión").show();
                        }
                        else if (error instanceof ServerError)
                        {
                            Log.e("ServerError", "error SERVER ERROR");
                            // error
                        }
                        else {
                            Log.e(Constantes.TAG, (error != null) ? error.getMessage() : "error");
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Content-Type", "application/json; charset=utf-8");
                if (headers.isEmpty()){
                    return super.getHeaders();
                }
                else{
                    return headers;
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params.isEmpty()){
                    return super.getHeaders();
                }
                else{
                    return params;
                }
            }
        };
        if (mostrarProgressDialog){
            progressDialog.show();
        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);

    }

    public void stringRequest(int methodRequest, String url, Response.Listener<String> listener, Response.ErrorListener errorListenerr){
        final StringRequest stringRequest = new StringRequest(
                methodRequest,
                url,
                listener,
                errorListenerr
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers.isEmpty()){
                    return super.getHeaders();
                }
                else{
                    return headers;
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params.isEmpty()){
                    return super.getHeaders();
                }
                else{
                    return params;
                }
            }

        };
        if (mostrarProgressDialog)
            progressDialog.show();
        requestQueue.add(stringRequest);

    }

    public void jsonObjectRequest(int methodRequest, String url, Response.Listener listener, Response.ErrorListener error){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(methodRequest,url,jsonObject,listener,error){

        };
        if (mostrarProgressDialog){
            progressDialog.show();
        }
        requestQueue.add(jsonObjectRequest);

    }


    public void jsonObjectRequest(int methodRequest, String url, Response.Listener listener){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(methodRequest, url, jsonObject, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(Constantes.TAG,error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError)
                {
                    dialog.content("Error de conexión").show();
                }
                else if (error instanceof ServerError)
                {
                    // error
                    Log.e("ServerError", "error SERVER ERROR");
                }
                else {


                }
            }
        }){

        };
        if (mostrarProgressDialog){
            progressDialog.show();
        }
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);

    }


    public void imageRequest(final CircleImageView imageView)
    {

        String url = "";

        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        progressDialog.dismiss();
                        imageView.setImageBitmap(response);

                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                //map.put(context.getString(R.string.authorization), context.getString(R.string.bearer) + " " + usuario.getAccessToken());
                return map;
            }
        };
        if (mostrarProgressDialog){
            progressDialog.show();
        }
        requestQueue.add(imageRequest);

    }


    // método para uso con jsonRequest
    public void addParamsInteger(String key, Integer value){
        try {
            jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // método para uso con jsonRequest
    public void addParamsBoolean(String key, Boolean value){
        try {
            jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // método para uso con jsonRequest
    public void addParamsString(String key, String value){
        try {
            jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParamsFloat(String key, Float value){
        try {
            jsonObject.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setMessageProgressDialog(String message){
        progressDialog.setMessage(message);
    }
    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }
    public void addHeader(String key, String value){
        headers.put(key, value);
    }
    public void addParams(String key, String value){
        params.put(key, value);
    }



    public void setMostrarProgressDialog(Boolean mostrarProgressDialog) {
        this.mostrarProgressDialog = mostrarProgressDialog;
    }
}
