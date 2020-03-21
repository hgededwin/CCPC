package com.cacaopaycard.cacaopay.Modelos;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cacaopaycard.cacaopay.R;

public class Dialog {
    private Context context;

    public Dialog(Context context) {
        this.context = context;
    }

    public void showDialog(String content){
        new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(content)
                .positiveText(R.string.str_aceptar)
                .cancelable(false)
                .show();
    }

    public void showDialog(String content, String title){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(R.string.str_aceptar)
                .cancelable(false)
                .show();
    }

    public void showDialog(String content, MaterialDialog.SingleButtonCallback singleButtonCallback){
        new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(content)
                .positiveText(R.string.str_aceptar)
                .onPositive(singleButtonCallback)
                .cancelable(false)
                .show();
    }

    public void showDialog(String content, String positiveText, MaterialDialog.SingleButtonCallback singleButtonCallback){
        new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(content)
                .positiveText(positiveText)
                .onPositive(singleButtonCallback)
                .cancelable(false)
                .show();
    }

    public void showDialog(String title, String content, String positiveText, MaterialDialog.SingleButtonCallback singleButtonCallback, Boolean cancelable){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .onPositive(singleButtonCallback)
                .cancelable(cancelable)
                .show();
    }
    public void showDialog(String content, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback singleButtonCallback){
        new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(content)
                .positiveText(positiveText)
                .onPositive(singleButtonCallback)
                .negativeText(negativeText)
                .show();
    }


}
