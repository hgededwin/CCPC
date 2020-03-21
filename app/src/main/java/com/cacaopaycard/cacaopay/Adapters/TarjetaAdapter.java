package com.cacaopaycard.cacaopay.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cacaopaycard.cacaopay.AdminCuenta.Tarjetas.EditarTarjetaActivity;
import com.cacaopaycard.cacaopay.Modelos.DialogFragmentPIN;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.R;

import java.util.ArrayList;
import java.util.List;

import static com.cacaopaycard.cacaopay.Constantes.AGREGAR_TARJETA;

public class TarjetaAdapter extends RecyclerView.Adapter<TarjetaAdapter.TarjetaViewHolder> {

    private List<Tarjeta> items;
    private boolean editando = false;
    Context context;
    public static EditionCardListener listener;

    private SparseBooleanArray seleccionados;
    private int colorYellow = Color.rgb(169,160,60);
    private int colorAqua = Color.rgb(26,76,121);
    private int colorGray = Color.rgb(74,152,156);
    private int[] colores = {colorYellow,colorAqua,colorGray};
    private static int contador;

// #1A4C79
    // 4A989C
    public interface EditionCardListener{
        void eliminarTarjeta(Context context,int indice, String numeroTarjeta);
        void lockUnlockCard(Context context, int indice, String numeroTarjeta, boolean isBlocked);
    }

    public TarjetaAdapter(List<Tarjeta> items, Context context){
        this.items = items;
        this.editando = false;
        if(items.get(items.size()-1).esTarjetaDeAgregacion()) {
            items.remove(items.get(items.size() - 1));
            notifyDataSetChanged();
        }
        this.context = context;

    }

    public TarjetaAdapter(List<Tarjeta> items, EditionCardListener listener, Context context){
        TarjetaAdapter.listener = listener;
        this.items = items;
        this.editando = false;
        /*if(items.get(items.size()-1).esTarjetaDeAgregacion()) {
            items.remove(items.get(items.size() - 1));
            notifyDataSetChanged();
        }*/

        this.context = context;

    }

    public TarjetaAdapter(List<Tarjeta> items,boolean editando){
        this.items = items;
        this.editando = editando;
        if(editando && !items.get(items.size()-1).esTarjetaDeAgregacion()){
            items.add(new Tarjeta());
            notifyDataSetChanged();
        }

        seleccionados = new SparseBooleanArray();
    }

    public void setListenerRemovedLock(EditionCardListener listenerRemovedLock){
        TarjetaAdapter.listener = listenerRemovedLock;
    }


    @NonNull
    @Override
    public TarjetaAdapter.TarjetaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mis_tarjetas,viewGroup,false);
        return new TarjetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TarjetaAdapter.TarjetaViewHolder tarjetaViewHolder, final int indice) {


        final String numeroTarjeta = items.get(indice).getNumeroCuenta();
        tarjetaViewHolder.saldo.setText(String.valueOf(items.get(indice).getSaldo()));
        tarjetaViewHolder.numeroCuenta.setText(items.get(indice).getCardMask());

        if(items.get(indice).isEstaBloqueada()) {
            tarjetaViewHolder.container.setBackgroundColor(Color.rgb(152, 156, 161));
            tarjetaViewHolder.lock.setImageResource(R.drawable.ic_lock_red);
            items.get(indice).setEstaBloqueada(true);
        } else {
            tarjetaViewHolder.container.setBackgroundColor(items.get(indice).getColor());
            tarjetaViewHolder.lock.setImageResource(R.drawable.ic_pass);
            items.get(indice).setEstaBloqueada(false);
        }


        /*if (items.get(indice).isEstaBloqueada()) {
            // tarjeta bloqueada
            tarjetaViewHolder.lock.setImageResource(R.drawable.ic_lock_red);

        } else
            tarjetaViewHolder.lock.setImageResource(R.drawable.ic_pass);*/

        tarjetaViewHolder.imgEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //eliminarTarjeta(view.getContext(),items.get(indice).getNumeroCuenta(),indice);
                System.out.println("....Listener eliminar " + listener);
                if (listener != null) {
                    listener.eliminarTarjeta(view.getContext(), indice, items.get(indice).getNumeroCuenta());


                }
            }
        });

        tarjetaViewHolder.lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(".....Listener lock " + listener);
                boolean lock = items.get(indice).isEstaBloqueada();

                if (listener != null) {
                    listener.lockUnlockCard(view.getContext(), indice, items.get(indice).getNumeroCuenta(), lock);

                }

            }
        });

        tarjetaViewHolder.imgEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarTarjetaActivity.class);
                intent.putExtra("number_card",items.get(indice).getNumeroCuenta());
                ((Activity) context).startActivityForResult(intent, AGREGAR_TARJETA);
                ((Activity) context).overridePendingTransition(R.anim.left_in,R.anim.left_out);
            }
        });

        /*if(!items.get(indice).esTarjetaDeAgregacion()) {

            final String numeroTarjeta = items.get(indice).getNumeroCuenta();
            tarjetaViewHolder.saldo.setText(String.valueOf(items.get(indice).getSaldo()));
            tarjetaViewHolder.numeroCuenta.setText("**** " + numeroTarjeta.substring(numeroTarjeta.length() - 4));

            if (items.get(indice).isEstaBloqueada()) {
                // tarjeta bloqueada

            }

            if (editando) {

                tarjetaViewHolder.imgMastercard.setVisibility(View.GONE);
                //tarjetaViewHolder.imgAgregar.setVisibility(View.GONE);
                tarjetaViewHolder.imgEliminar.setVisibility(View.VISIBLE);
                tarjetaViewHolder.imgEliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        new MaterialDialog.Builder(view.getContext())
                                .title("Eliminar tarjeta")
                                .content("¿Seguro que deseas eliminar la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4)+ "?")
                                .positiveText(R.string.str_continuar)
                                .negativeText("Deshacer")
                                .negativeColorRes(R.color.blue_color_contraste)
                                .positiveColorRes(R.color.blue_color_contraste)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        // Servicio que elimina tarjetas.

                                        DialogFragmentPIN dialogDesbloqueo = new DialogFragmentPIN();
                                        dialogDesbloqueo.instanceDialog(view.getContext(), new DialogFragmentPIN.DialogListener() {
                                            @Override
                                            public void calbackOnComplete(DialogFragment dialog, String otp) {
                                                items.remove(indice);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                            }
                                        },null,"Ingrese su PIN para continuar",null);

                                        dialogDesbloqueo.show(((AppCompatActivity)view.getContext()).getSupportFragmentManager(),"CacaoPay");



                                    }
                                })
                                .show();

                    }
                });
            } else {
                tarjetaViewHolder.imgMastercard.setVisibility(View.VISIBLE);
                //tarjetaViewHolder.imgAgregar.setVisibility(View.GONE);
                tarjetaViewHolder.imgEliminar.setVisibility(View.VISIBLE);
            }

        } else{
            tarjetaViewHolder.saldo.setText("Agrega tarjeta");
            tarjetaViewHolder.numeroCuenta.setText(" ");
            tarjetaViewHolder.saldo.setTextSize(16);
            tarjetaViewHolder.imgEliminar.setVisibility(View.GONE);
            //tarjetaViewHolder.imgAgregar.setVisibility(View.VISIBLE);
            tarjetaViewHolder.imgMastercard.setVisibility(View.GONE);
            tarjetaViewHolder.cvTarjeta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), AgregarTarjetaActivity.class);
                    ((Activity)view.getContext()).startActivityForResult(intent, AGREGAR_TARJETA);
                    ((Activity)view.getContext()).overridePendingTransition(R.anim.left_in,R.anim.left_out);
                }
            });
        }*/

        /*tarjetaViewHolder.cvTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editando) {
                    // el usuario se encuentra removiendo tarjetas
                    Tarjeta tarjeta = items.get(indice);
                    //tarjetaViewHolder.bindView(tarjeta);

                } else {
                    // estado normal
                    tarjetaViewHolder.cvTarjeta.setCardBackgroundColor(Color.argb(0,0,0,0));
                    Intent intent = new Intent(v.getContext(), DetalleTarjetaActivity.class);
                    (v.getContext()).startActivity(intent);
                    ((Activity)v.getContext()).overridePendingTransition(R.anim.left_in,R.anim.left_out);
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class TarjetaViewHolder extends RecyclerView.ViewHolder {

        TextView saldo, numeroCuenta;
        ImageView imgEliminar, imgMastercard, imgEditar, lock;
        RelativeLayout container;
        CardView cvTarjeta;
        View item;
        public TarjetaViewHolder(@NonNull View itemView) {
            super(itemView);

            saldo = itemView.findViewById(R.id.txt_saldo_mis_tarjetas_cv);
            numeroCuenta = itemView.findViewById(R.id.txt_cuenta_mis_tarjetas_cv);
            cvTarjeta = itemView.findViewById(R.id.cv_mis_tarjetas);
            imgEditar = itemView.findViewById(R.id.img_editar_item_cv);
            imgEliminar = itemView.findViewById(R.id.img_eliminar_item_cv);
            imgMastercard = itemView.findViewById(R.id.img_mastercard);
            container = itemView.findViewById(R.id.relative_container);
            lock = itemView.findViewById(R.id.img_bloquear_item_cv);


            this.item = itemView;

        }

        // método creado para seleccionar y deseleccionar las tarjetas
        public void bindView(Tarjeta tarjeta){

            if(seleccionados.get(getAdapterPosition()))
                item.setSelected(true);
            else
                item.setSelected(false);

            // seleccion y deseleccion los items
            if(!item.isSelected()){
                item.setSelected(true);
                seleccionados.put(getAdapterPosition(),true);
                cvTarjeta.setCardBackgroundColor(Color.rgb(07,38,61));

            } else {
                item.setSelected(false);
                seleccionados.put(getAdapterPosition(),false);
                cvTarjeta.setCardBackgroundColor(Color.argb(0,0,0,0));
            }

        }

    }

    public boolean haySeleccionados() {
        for (int i = 0; i <= items.size(); i++) {
            if (seleccionados.get(i))
                return true;
        }
        return false;
    }


    /**Devuelve aquellos objetos marcados.*/

    public ArrayList<Tarjeta> obtenerSeleccionados(){
        ArrayList<Tarjeta> marcados = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (seleccionados.get(i)){
                marcados.add(items.get(i));
            }
        }
        return marcados;
    }

    public void eliminarTarjeta(final Context context, final String numeroTarjeta, final int indice){


        new MaterialDialog.Builder(context)
                .title("Eliminar tarjeta")
                .content("¿Seguro que deseas eliminar la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4)+ "?")
                .positiveText(R.string.str_continuar)
                .negativeText("Deshacer")
                .negativeColorRes(R.color.blue_color_contraste)
                .positiveColorRes(R.color.blue_color_contraste)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // Servicio que elimina tarjetas.

                        DialogFragmentPIN dialogDesbloqueo = new DialogFragmentPIN();
                        dialogDesbloqueo.instanceDialog(context, new DialogFragmentPIN.DialogListener() {
                            @Override
                            public void calbackOnComplete(DialogFragment dialog, String otp) {
                                items.remove(indice);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        },null,"Ingrese su PIN para continuar",null);

                        dialogDesbloqueo.show(((AppCompatActivity)context).getSupportFragmentManager(),"CacaoPay");



                    }
                })
                .show();
    }

    public void bloquearTarjeta(final Context context, String numeroTarjeta, final int indice){
        new MaterialDialog.Builder(context)
                .title("Eliminar tarjeta")
                .content("¿Seguro que deseas bloquear la tarjeta con terminación **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4)+ "?")
                .positiveText(R.string.str_continuar)
                .negativeText("Deshacer")
                .negativeColorRes(R.color.blue_color_contraste)
                .positiveColorRes(R.color.blue_color_contraste)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // Servicio que elimina tarjetas.

                        DialogFragmentPIN dialogDesbloqueo = new DialogFragmentPIN();
                        dialogDesbloqueo.instanceDialog(context, new DialogFragmentPIN.DialogListener() {
                            @Override
                            public void calbackOnComplete(DialogFragment dialog, String otp) {
                                items.remove(indice);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        },null,"Ingrese su PIN para continuar",null);

                        dialogDesbloqueo.show(((AppCompatActivity)context).getSupportFragmentManager(),"CacaoPay");



                    }
                })
                .show();
    }

}
