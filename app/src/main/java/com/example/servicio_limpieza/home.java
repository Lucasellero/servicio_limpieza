package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Button btnCargarPropiedad = findViewById(R.id.btnCargarPropiedad);

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

        // Establecer el ítem seleccionado en el BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}
