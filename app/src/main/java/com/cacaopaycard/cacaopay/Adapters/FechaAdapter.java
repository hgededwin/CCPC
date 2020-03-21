package com.cacaopaycard.cacaopay.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.R;

import java.util.ArrayList;
import java.util.List;

public class FechaAdapter extends RecyclerView.Adapter<FechaAdapter.FechaViewholder> {

    private List<Movimiento> movimientos;
    private List<Fecha> fechas;

    public FechaAdapter(List<Movimiento> movimientos, List<Fecha> fechas){
        this.movimientos = movimientos;
        this.fechas = fechas;
    }

    @Override
    public FechaAdapter.FechaViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fecha,viewGroup,false);
        return new FechaViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FechaAdapter.FechaViewholder fechaViewholder, int indice) {

        Fecha date = fechas.get(indice);
        //System.out.println(indice);
        fechaViewholder.txtFecha.setText(date.getDate());

        List<Movimiento> movByDate = new ArrayList<>();
        for (int i = 0; i < movimientos.size(); i++){
            if (movimientos.get(i).getFecha().getDate().equals(date.getDate())){
                movByDate.add(movimientos.get(i));
                //System.out.println(movimientos.get(i).getMontoTransferencia());
            }
        }

        fechaViewholder.rvMovimientos.setAdapter(new MovimientosAdapter(movByDate));
        fechaViewholder.rvMovimientos.setLayoutManager(new LinearLayoutManager(fechaViewholder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    public class FechaViewholder extends RecyclerView.ViewHolder {

        TextView txtFecha;
        RecyclerView rvMovimientos;

        public FechaViewholder(@NonNull View itemView) {
            super(itemView);

            txtFecha = itemView.findViewById(R.id.txt_fecha_item);
            rvMovimientos = itemView.findViewById(R.id.rv_movimiento_por_fecha);
        }
    }
}
