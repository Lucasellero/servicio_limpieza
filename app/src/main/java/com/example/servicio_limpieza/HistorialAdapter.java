package com.example.servicio_limpieza;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        holder.tvPropiedadId.setText("Propiedad " + limpieza.getPropiedadId());
        holder.tvFechaLimpieza.setText("Fecha limpieza: " + limpieza.getFecha());
        holder.tvNombreEmpleado.setText("Empleado a cargo: " + limpieza.getNombreEmpleado());

        // Cargar el estado de la limpieza en un hilo separado
        new LoadEstadoLimpiezaTask(holder, limpieza).execute();
    }

    @Override
    public int getItemCount() {
        return limpiezas.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEmpleado;
        TextView tvPropiedadId;
        TextView tvFechaLimpieza;
        Button btnCalificar;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropiedadId = itemView.findViewById(R.id.tvPropiedadId);
            tvFechaLimpieza = itemView.findViewById(R.id.tvFechaLimpieza);
            tvNombreEmpleado = itemView.findViewById(R.id.tvNombreEmpleado);
            btnCalificar = itemView.findViewById(R.id.btnCalificarEmpleado);
        }
    }

    private class LoadEstadoLimpiezaTask extends AsyncTask<Void, Void, String> {
        private HistorialViewHolder holder;
        private Limpieza limpieza;

        public LoadEstadoLimpiezaTask(HistorialViewHolder holder, Limpieza limpieza) {
            this.holder = holder;
            this.limpieza = limpieza;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String estado = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return null;
                }

                String sql = "SELECT calificar FROM limpiezas WHERE FK_empleado_ID = ? and FK_propiedad_ID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, limpieza.getEmpleadoId());
                stmt.setInt(2, limpieza.getPropiedadId());
                rs = stmt.executeQuery();

                if (rs.next()) {
                    estado = rs.getString("calificar");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return estado;
        }

        @Override
        protected void onPostExecute(String estado) {
            if ("Calificada".equalsIgnoreCase(estado)) {
                holder.btnCalificar.setVisibility(View.GONE);
            } else {
                holder.btnCalificar.setVisibility(View.VISIBLE);
            }

            // Configurar el OnClickListener para el botón Calificar
            holder.btnCalificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Resena.class);
                    intent.putExtra("propiedad_id", limpieza.getPropiedadId());
                    intent.putExtra("empleado_id", limpieza.getEmpleadoId());
                    context.startActivity(intent);
                }
            });
        }
    }

    public Connection conexionBD() {
        return databaseConnection.getConnection();
    }
}


