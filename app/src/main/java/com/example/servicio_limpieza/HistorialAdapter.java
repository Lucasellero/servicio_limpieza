package com.example.servicio_limpieza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private Context context;
    private List<Limpieza> limpiezas;

    public HistorialAdapter(Context context, List<Limpieza> limpiezas) {
        this.context = context;
        this.limpiezas = limpiezas;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Limpieza limpieza = limpiezas.get(position);
        holder.tvPropiedadId.setText("Departamento " + limpieza.getPropiedadId());
        holder.tvFechaLimpieza.setText("Fecha limpieza: " + limpieza.getFecha());
        holder.btnCalificar.setOnClickListener(v -> {
            // Implementar acción para calificar
        });
    }

    @Override
    public int getItemCount() {
        return limpiezas.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvPropiedadId;
        TextView tvFechaLimpieza;
        Button btnCalificar;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropiedadId = itemView.findViewById(R.id.tvPropiedadId);
            tvFechaLimpieza = itemView.findViewById(R.id.tvFechaLimpieza);
            btnCalificar = itemView.findViewById(R.id.btnCalificar);
        }
    }
}