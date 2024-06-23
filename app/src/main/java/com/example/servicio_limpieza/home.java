package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private PropertyAdapter adapter;
    private RecyclerView recyclerView;
    private List<Propiedad> propiedades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        propiedades = new ArrayList<>();
        adapter = new PropertyAdapter(propiedades);
        recyclerView.setAdapter(adapter);

        Button btnCargarPropiedad = findViewById(R.id.btnCargarPropiedad);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        btnCargarPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(home.this, cargar_propiedad.class);
                startActivity(intento);
            }
        });

        // Configurar el Listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_profile) {
                Intent intent = new Intent(home.this, perfil.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_home) {
                return true;
            }
            return false;
        });

        usuario Usuario = usuario.getInstance();

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Mostrar el nombre del usuario en el TextView
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        String nombreUsuario = Usuario.getNombre();
        textViewUserName.setText("Bienvenido, " + nombreUsuario);

        // Cargar propiedades desde la base de datos
        new LoadPropertiesTask().execute();
    }

    private class LoadPropertiesTask extends AsyncTask<Void, Void, List<Propiedad>> {

        @Override
        protected List<Propiedad> doInBackground(Void... voids) {
            List<Propiedad> properties = new ArrayList<>();
            try (Connection connection = DatabaseConnection.getConnection()) {
                usuario Usuario = usuario.getInstance();
                String query = "SELECT * FROM propiedades WHERE FK_propietario_ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, Usuario.getId());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("PK_propiedad_ID");
                    String nombre = resultSet.getString("nombre");
                    String barrio = resultSet.getString("barrio");
                    String direccion = resultSet.getString("direccion");
                    String estado = resultSet.getString("estado");
                    int tamano = resultSet.getInt("tamano");
                    String tipo = resultSet.getString("tipo");
                    int propietarioId = resultSet.getInt("FK_propietario_ID");
                    properties.add(new Propiedad(id, nombre, direccion, barrio, tamano, estado, tipo, propietarioId));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return properties;
        }

        @Override
        protected void onPostExecute(List<Propiedad> properties) {
            propiedades.clear();
            propiedades.addAll(properties);
            adapter.notifyDataSetChanged();
        }
    }
}
