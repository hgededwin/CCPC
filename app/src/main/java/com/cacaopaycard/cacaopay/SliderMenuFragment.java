package com.cacaopaycard.cacaopay;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cacaopaycard.cacaopay.Modelos.DialogFragmentPIN;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;


public class SliderMenuFragment extends Fragment {


    private TextView saldo;
    private TextView numCuenta, txtLock;
    private CardView cvTarjeta;
    private SwitchCompat switchCompat;
    private RelativeLayout relativeLayout;

    private String strNumCuenta, intSaldo;
    private Boolean esBloqueada;
    static Boolean isTouched = false;

    public static interface ListenerLock{
        void listenerLockUnlock(boolean isLock);
    }


    public static SliderMenuFragment newInstance(Tarjeta tarjeta) {

        SliderMenuFragment fragment = new SliderMenuFragment();

        Bundle bundle = new Bundle();

        String numeroTarjeta = tarjeta.getNumeroCuenta();
        bundle.putString("saldo", tarjeta.getSaldo());
        bundle.putString("numero_cuenta", "**** " + numeroTarjeta.substring(numeroTarjeta.length() - 4));
        bundle.putBoolean("esta_bloqueada", tarjeta.isEstaBloqueada());
        //SliderMenuFragment.listenerLock = listenerLock;
        //SliderMenuFragment.checkedChangeListener = checkedChangeListener;
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){

            intSaldo = getArguments().getString("saldo");
            strNumCuenta = getArguments().getString("numero_cuenta");
            esBloqueada = getArguments().getBoolean("esta_bloqueada");
            System.out.println("hay argumentos");
        } else
            Log.e("CacaoPay", "no hay argumentos");

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_slider_menu, container,false);

        saldo = view.findViewById(R.id.txt_saldo_consulta_cv);
        numCuenta = view.findViewById(R.id.txt_number_cuenta_consulta_cv);
        cvTarjeta = view.findViewById(R.id.cv_tarjeta_consultas);
        relativeLayout = view.findViewById(R.id.relative_consultas);
        switchCompat = view.findViewById(R.id.switch_consulta_cv);
        txtLock = view.findViewById(R.id.txt_bloquear_tarjeta_cv);


        if(getArguments() != null) {

            saldo.setText(intSaldo);
            numCuenta.setText(strNumCuenta);
            switchCompat.setChecked(esBloqueada);


            if(esBloqueada) {
                // tarjeta bloqueada
                relativeLayout.setBackgroundColor(Color.rgb(152,156,161));
                txtLock.setText("Desbloquear tarjeta");

            } else {
                // tarjeta desbloqueada
                relativeLayout.setBackgroundColor(Color.rgb(0,38,61));
                txtLock.setText("Bloquear tarjeta");
            }

            switchCompat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isTouched){
                        isTouched = false;
                        if(isChecked){
                            switchCompat.setChecked(false);
                            lockUnlockBehavior(false);
                        } else {
                            switchCompat.setChecked(true);
                            lockUnlockBehavior(true);
                        }
                    }
                }
            });


        }
        return view;

    }

    public void lockUnlockBehavior(boolean isLock){

        if(isLock) {

            // tarjeta bloqueada, se tiene que desbloquear
            //relativeLayout.setBackgroundColor(Color.rgb(0,38,61));
            ConsultasFragment instanceFragmentParent = ConsultasFragment.getInstance();
            instanceFragmentParent.lockUnlockcard(isLock);
            //instanceFragmentParent.enableDisableUI(true);
            //switchCompat.setChecked(false);
            //txtLock.setText("Bloquear tarjeta");

        } else {

            // tarjeta desbloqueada, se esta bloqueando
            //relativeLayout.setBackgroundColor(Color.rgb(152,156,161));
            ConsultasFragment instanceFragmentParent = ConsultasFragment.getInstance();
            instanceFragmentParent.lockUnlockcard(isLock);
            //instanceFragmentParent.enableDisableUI(false);
            //switchCompat.setChecked(true);
            //txtLock.setText("Desbloquear tarjeta");

        }

    }

    public void lockBehavior(final boolean isLock, final View view){
        if(isLock) { //  Aqui validas la accion de tu componente

            new MaterialDialog.Builder(view.getContext())
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // bloquear tarjeta
                            final DialogFragmentPIN dialogBloqueo = DialogFragmentPIN.instanceDialog(view.getContext(), new DialogFragmentPIN.DialogPositive() {
                                @Override
                                public void listenerPositiveText(DialogFragment dialog, String otp) {
                                    relativeLayout.setBackgroundColor(Color.rgb(0,38,61));
                                    ConsultasFragment instanceFragmentParent = ConsultasFragment.getInstance();
                                    instanceFragmentParent.enableDisableUI(true);
                                    switchCompat.setChecked(false);
                                    txtLock.setText("Bloquear tarjeta");
                                    dialog.dismiss();
                                }
                            },"Ingrese su PIN para continuar", "PIN");

                            dialogBloqueo.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"CacaoPay");

                        }
                    })
                    .positiveText(R.string.str_aceptar)
                    .negativeText(R.string.str_cancelar)
                    .content(R.string.str_confirmacion_desbloqueo)
                    .show();
        } else {
            new MaterialDialog.Builder(view.getContext())
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            DialogFragmentPIN dialogDesbloqueo = DialogFragmentPIN.instanceDialog(view.getContext(), new DialogFragmentPIN.DialogPositive() {
                                @Override
                                public void listenerPositiveText(DialogFragment dialog, String otp) {
                                    System.out.println("Bloqueo");
                                    //estaBloqueada = false;
                                    relativeLayout.setBackgroundColor(Color.rgb(152,156,161));
                                    ConsultasFragment instanceFragmentParent = ConsultasFragment.getInstance();
                                    instanceFragmentParent.enableDisableUI(false);
                                    switchCompat.setChecked(true);
                                    txtLock.setText("Desbloquear tarjeta");
                                    dialog.dismiss();
                                }
                            }, "Ingrese su PIN para continuar","PIN");

                            dialogDesbloqueo.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(),"CacaoPay");


                        }
                    })
                    .positiveText(R.string.str_aceptar)
                    .negativeText(R.string.str_cancelar)
                    .content(R.string.str_confirmacion_bloqueo)
                    .show();
        }
    }

}
