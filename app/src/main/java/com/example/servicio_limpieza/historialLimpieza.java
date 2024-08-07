package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class historialLimpieza extends AppCompatActivity {
    private RecyclerView recyclerViewHistorial;
    private HistorialAdapter historialAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_limpieza);

        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configurar el Listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                Intent intent = new Intent(historialLimpieza.this, home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_history) {
                return true;
            }else if (id == R.id.navigation_profile){
                Intent intent = new Intent(historialLimpieza.this, perfil.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_history);

        new FetchDataTask().execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, List<Limpieza>> {
        @Override
        protected List<Limpieza> doInBackground(Void... voids) {
            List<Limpieza> limpiezas = new ArrayList<>();
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                connection = databaseConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT l.FK_propiedad_ID, l.FK_empleado_ID, l.fecha, p.nombre " +
                        "FROM limpiezas l " +
                        "JOIN personal p ON l.FK_empleado_ID = p.PK_personal_ID");

                while (resultSet.next()) {
                    Limpieza limpieza = new Limpieza();
                    limpieza.setPropiedadId(resultSet.getInt("FK_propiedad_ID"));
                    limpieza.setEmpleadoId(resultSet.getInt("FK_empleado_ID"));
                    limpieza.setFecha(resultSet.getString("fecha"));
                    limpieza.setNombreEmpleado(resultSet.getString("nombre"));
                    limpiezas.add(limpieza);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return limpiezas;
        }

        @Override
        protected void onPostExecute(List<Limpieza> limpiezas) {
            if (limpiezas.isEmpty()) {
                Toast.makeText(historialLimpieza.this, "No se encontraron datos de limpieza", Toast.LENGTH_SHORT).show();
            } else {
                historialAdapter = new HistorialAdapter(historialLimpieza.this, limpiezas);
                recyclerViewHistorial.setAdapter(historialAdapter);
            }
        }
    }
}

