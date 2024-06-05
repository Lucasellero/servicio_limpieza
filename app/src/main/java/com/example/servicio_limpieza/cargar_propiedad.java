package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class cargar_propiedad extends AppCompatActivity {

    private EditText nameEditText;
    private EditText cityEditText;
    private EditText addressEditText;
    private EditText stateEditText;
    private EditText sizeEditText;
    private EditText typeEditText;
    private Button acceptButton;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_propiedad);

        // Inicializar vistas
        nameEditText = findViewById(R.id.name);
        cityEditText = findViewById(R.id.city);
        addressEditText = findViewById(R.id.address);
        stateEditText = findViewById(R.id.state);
        sizeEditText = findViewById(R.id.size);
        typeEditText = findViewById(R.id.type);
        acceptButton = findViewById(R.id.accept_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configurar el Listener para el botón de aceptar
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Si los campos son válidos, realizar alguna acción
                    Toast.makeText(cargar_propiedad.this, "La propiedad fue cargada exitosamente.", Toast.LENGTH_SHORT).show();
                } else {
                    // Si hay campos inválidos, mostrar un mensaje de error
                    Toast.makeText(cargar_propiedad.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el Listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_profile) {
                // Cambiar a la pantalla de perfil
                Intent intent = new Intent(cargar_propiedad.this, perfil.class);
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

    private boolean validateFields() {
        // Validar que todos los campos estén completos
        return !nameEditText.getText().toString().isEmpty() &&
                !cityEditText.getText().toString().isEmpty() &&
                !addressEditText.getText().toString().isEmpty() &&
                !stateEditText.getText().toString().isEmpty() &&
                !sizeEditText.getText().toString().isEmpty() &&
                !typeEditText.getText().toString().isEmpty();
    }
}
