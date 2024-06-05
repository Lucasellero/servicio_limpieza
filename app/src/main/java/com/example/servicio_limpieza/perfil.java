package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configurar el Listener para el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    // Cambiar a la pantalla de inicio (MainActivity)
                    Intent intent = new Intent(perfil.this, perfil.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_profile) {
                    // Ya estamos en la actividad ProfileActivity, no hacer nada
                    return true;
                }
                return false;
            }
        });

        // Establecer el ítem seleccionado en el BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }
}

