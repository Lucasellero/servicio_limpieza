package com.example.servicio_limpieza;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class historialLimpieza extends AppCompatActivity {
    private ListView listViewHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_limpieza);

        listViewHistorial = findViewById(R.id.listViewHistorial);

        new FetchDataTask().execute();

    }

    private class FetchDataTask extends AsyncTask<Void, Void, List<Map<String, String>>> {
        @Override
        protected List<Map<String, String>> doInBackground(Void... voids) {
            List<Map<String, String>> data = new ArrayList<>();
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                connection = databaseConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT FK_propiedad_ID, FK_empleado_ID, fecha, valor FROM limpiezas");

                while (resultSet.next()) {
                    Map<String, String> item = new HashMap<>();
                    item.put("PropiedadID", "Departamento " + resultSet.getInt("FK_propiedad_ID"));
                    item.put("FechaLimpieza", "Fecha limpieza: " + resultSet.getString("fecha").toString());
                    data.add(item);
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
            return data;
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> data) {
            if (data.isEmpty()) {
                Toast.makeText(historialLimpieza.this, "No se encontraron datos de limpieza", Toast.LENGTH_SHORT).show();
            } else {
                SimpleAdapter adapter = new SimpleAdapter(
                        historialLimpieza.this,
                        data,
                        R.layout.item_historial,
                        new String[]{"PropiedadID", "FechaLimpieza"},
                        new int[]{R.id.tvPropiedadId, R.id.tvFechaLimpieza}
                );
                listViewHistorial.setAdapter(adapter);
            }
        }
    }
}
