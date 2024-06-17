package com.example.servicio_limpieza;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReservarLimpiezaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_limpieza);

        int propiedadId = getIntent().getIntExtra("propiedad_id", -1);
        if (propiedadId != -1) {
            // Aquí puedes manejar la lógica para reservar la limpieza
            Toast.makeText(this, "Reserva de limpieza para la propiedad ID: " + propiedadId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al obtener la propiedad", Toast.LENGTH_SHORT).show();
        }
    }
}