package com.cacaopaycard.cacaopay.Modelos;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Singleton {
    private static Singleton mSingleton = null;
    private RequestQueue mRequestQueue;

    private Singleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static Singleton getInstance(Context context) {
        if (mSingleton == null) {
            mSingleton = new Singleton(context);
        }
        return mSingleton;
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }
}
