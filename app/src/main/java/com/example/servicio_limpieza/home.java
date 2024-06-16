package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.example.servicio_limpieza.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        propiedades = new ArrayList<>();
        adapter = new PropertyAdapter(propiedades);
        recyclerView.setAdapter(adapter);

        Button btnCargarPropiedad = findViewById(R.id.btnCargarPropiedad);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Asegúrate de inicializar aquí

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
                // Cambiar a la pantalla de perfil
                Intent intent = new Intent(home.this, perfil.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_home) {
                // Ya estamos en la actividad MainActivity, no hacer nada
                return true;
            }
            return false;
        });

        usuario Usuario = usuario.getInstance();

        // Establecer el ítem seleccionado en el BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Dentro del método onCreate de la clase home
        TextView textViewUserName = findViewById(R.id.textViewUserName);

        // Obtener el nombre del usuario desde la instancia Usuario
        String nombreUsuario = Usuario.getNombre(); // Asumiendo que existe un método getNombre en la clase Usuario

        // Mostrar el nombre del usuario en el TextView
        textViewUserName.setText("Bienvenido " + nombreUsuario);
    }
}

