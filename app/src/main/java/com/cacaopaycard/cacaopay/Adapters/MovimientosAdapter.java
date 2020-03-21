package com.cacaopaycard.cacaopay.Adapters;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.R;

import java.util.List;

public class MovimientosAdapter extends RecyclerView.Adapter<MovimientosAdapter.MovimientosViewholder> {

    private List<Movimiento> items;

    public MovimientosAdapter(List<Movimiento> items){
        this.items = items;
    }

    @NonNull
    @Override
    public MovimientosViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movimientos,viewGroup,false);
        return new MovimientosViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientosViewholder movimientosViewholder, int indice) {

        movimientosViewholder.descripcionTransferencia.setText(items.get(indice).getDescripcionTransferencia());
        //movimientosViewholder.fecha.setText(items.get(indice).getFecha());


        if(items.get(indice).getEsTransferenciaRecibida()){
            System.out.println("....Transferencia recibida");
            movimientosViewholder.iconoTransferencia.setImageResource(R.drawable.ic_transferencia_recibida);
            movimientosViewholder.montoTransferencia.setText("+" + items.get(indice).getMontoTransferencia());
            movimientosViewholder.montoTransferencia.setTextColor(Color.rgb(111,191,74)); // #6FBF4A
        } else {
            movimientosViewholder.iconoTransferencia.setImageResource(R.drawable.ic_transferencia_hecha);
            movimientosViewholder.montoTransferencia.setText(items.get(indice).getMontoTransferencia());
            movimientosViewholder.montoTransferencia.setTextColor(Color.rgb(204,16,16)); // #CC1010
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MovimientosViewholder extends RecyclerView.ViewHolder {

        ImageView iconoTransferencia;
        TextView descripcionTransferencia, montoTransferencia;


        public MovimientosViewholder(@NonNull View itemView) {
            super(itemView);

            iconoTransferencia = itemView.findViewById(R.id.img_item_movimientos);
            descripcionTransferencia = itemView.findViewById(R.id.txt_descripcion_movimiento);
            montoTransferencia = itemView.findViewById(R.id.txt_movimiento_monto);
            //fecha = itemView.findViewById(R.id.txt_fecha_movimiento);
        }
    }
}
