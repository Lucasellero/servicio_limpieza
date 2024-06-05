package com.example.servicio_limpieza;

import static com.example.servicio_limpieza.R.layout.activity_bienvenida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class bienvenida extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_bienvenida);

        Button btnCrearCuenta = findViewById(R.id.buttonCrearCuenta);
        Button btnIniciarSesion = findViewById(R.id.buttonIniciarSesion);

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(bienvenida.this, crear_cuenta.class);
                startActivity(intento);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(bienvenida.this, iniciarSesion.class);
                startActivity(intento);
            }
        });
    }
}
