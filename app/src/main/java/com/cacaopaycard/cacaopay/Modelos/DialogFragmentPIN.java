package com.cacaopaycard.cacaopay.Modelos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.Constantes;
import com.cacaopaycard.cacaopay.R;
import com.mukesh.OtpView;

public class DialogFragmentPIN extends DialogFragment {

    private String titulo;
    private String leyenda;
    private String cancelar;
    private String postiveText;
    private OtpView otpView;

    public interface DialogListener{
        void calbackOnComplete(DialogFragment dialog, String otp);

    }
    public interface DialogPositive{
        void listenerPositiveText(DialogFragment dialog, String otp);
    }
    public interface DialogNegative{
        void listenerNegativeText(DialogFragment dialog);
    }

    private static DialogListener listener;
    private static DialogPositive listenerPositive;
    private static DialogNegative listenerNegative;

    public static DialogFragmentPIN instanceDialog(Context context, DialogListener listener){

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("arguments", "sin argumentos");
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;

    }
    public static DialogFragmentPIN instanceDialog(Context context, DialogListener listener, String titulo, String leyenda, String cancelar){
        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("titulo",titulo);
        bundle.putString("leyenda", leyenda);
        bundle.putString("cancelar", cancelar);
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    public static DialogFragmentPIN instanceDialog(Context context, DialogPositive postiveText, String titulo, String leyenda, String cancelar){
        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("titulo",titulo);
        bundle.putString("leyenda", leyenda);
        bundle.putString("cancelar", cancelar);
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }
    public static DialogFragmentPIN instanceDialog(Context context, DialogListener listener, String titulo, String leyenda, String cancelar, String positiveText, DialogPositive listenerPositive, DialogNegative listenerNegative){

        DialogFragmentPIN.listenerPositive = listenerPositive;
        DialogFragmentPIN.listenerNegative = listenerNegative;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("titulo",titulo);
        bundle.putString("leyenda", leyenda);
        bundle.putString("cancelar", cancelar);
        bundle.putString("positive_text", positiveText);
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;

    }

    public static DialogFragmentPIN instanceDialog(Context context, DialogListener listener, DialogPositive listenerPositive, DialogNegative listenerNegative){
        DialogFragmentPIN.listenerPositive = listenerPositive;
        DialogFragmentPIN.listenerNegative = listenerNegative;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("arguments", "sin argumentos");
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    public static DialogFragmentPIN instanceDialog(Context context, DialogListener listener, DialogPositive listenerPositive){
        System.out.println("instancia con listener positive y listener");
        DialogFragmentPIN.listener = listener;
        DialogFragmentPIN.listenerPositive = listenerPositive;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("arguments", "sin argumentos");
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    public static DialogFragmentPIN instanceDialog(Context context,DialogPositive listenerPositive){
        Log.e(Constantes.TAG,"instanceDialog....dialog");
        DialogFragmentPIN.listenerPositive = listenerPositive;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("arguments", "sin argumentos");
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    public static DialogFragmentPIN instanceDialog(Context context, DialogPositive listenerPositive, String leyenda, String titulo){
        Log.e(Constantes.TAG,"instanceDialog....dialog");
        DialogFragmentPIN.listenerPositive = listenerPositive;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("leyenda", leyenda);
        bundle.putString("titulo",titulo);
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    public static DialogFragmentPIN instanceDialog(Context context, DialogPositive listenerPositive, String leyenda){
        Log.e(Constantes.TAG,"instanceDialog....dialog");
        DialogFragmentPIN.listenerPositive = listenerPositive;

        DialogFragmentPIN dialogFragmentPIN = new DialogFragmentPIN();
        Bundle bundle = new Bundle();
        bundle.putString("leyenda", leyenda);
        dialogFragmentPIN.setArguments(bundle);

        return  dialogFragmentPIN;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(Constantes.TAG,"onCreateView....dialog");
        View view = inflater.inflate(R.layout.dialog_transferencia,container);
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.e(Constantes.TAG,"onCreateDialog....dialog");

        View viewDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_transferencia, null);


        otpView = viewDialog.findViewById(R.id.otp_transferencia);
        TextView txtTitulo = viewDialog.findViewById(R.id.txt_titulo_otp);
        TextView txtLeyenda = viewDialog.findViewById(R.id.txt_leyenda_otp);


        //otpView.setImeOptions(EditorInfo.IME_ACTION_GO);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();


        System.out.println("getArguments: " + getArguments());

        if(getArguments() != null) {
            if (getArguments().getString("cancelar") == null)
                cancelar = "Cancelar";
            else
                cancelar = getArguments().getString("cancelar");

            if (getArguments().getString("positive_text") == null)
                postiveText = "OK";
            else
                postiveText = getArguments().getString("positive_text");

            if(getArguments().getString("leyenda") != null) {
                System.out.println("..leyendaArgument" + getArguments().getString("leyenda"));
                txtLeyenda.setText(getArguments().getString("leyenda"));
            }

            if(getArguments().getString("titulo") != null)
                txtTitulo.setText(getArguments().getString("titulo"));


            // otp listener
        /*otpView = new OtpView(this.getContext());
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                listener.calbackOnComplete(DialogFragmentPIN.this, otp);
            }
        });
        // mandar código manuakmente
        otpView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean action = false;
                if(i == EditorInfo.IME_ACTION_GO){
                    listener.calbackOnComplete(DialogFragmentPIN.this, otpView.getText().toString());
                    action = true;
                }
                return action;
            }
        });*/

            // creación de dialogo



            builder.setView(viewDialog)
                    .setPositiveButton(postiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("Listener Positive....." + listenerPositive);
                            if (listenerPositive != null)
                                listenerPositive.listenerPositiveText(DialogFragmentPIN.this, otpView.getText().toString());

                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton(cancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (listenerNegative != null) {
                                listenerNegative.listenerNegativeText(DialogFragmentPIN.this);
                            } else {
                                DialogFragmentPIN.this.getDialog().cancel();
                            }
                        }
                    });

        }

        return builder.create();
    }
}
