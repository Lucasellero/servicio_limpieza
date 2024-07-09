package com.example.servicio_limpieza;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Resena extends AppCompatActivity {

    private EditText nombreEditText;
    private EditText legajoEditText;
    private EditText direccionEditText;
    private EditText reviewEditText;
    private Spinner ratingSpinner;
    private ImageView imageView1;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resena);

        nombreEditText = findViewById(R.id.nombre);
        legajoEditText = findViewById(R.id.legajo);
        direccionEditText = findViewById(R.id.direccion);
        reviewEditText = findViewById(R.id.review);
        ratingSpinner = findViewById(R.id.spinner_rating);


        // Inicializar el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);

        // Ejemplo de asignación de valores
        nombreEditText.setText("Lautaro Petruccelli");
        legajoEditText.setText("45769212");
        direccionEditText.setText("Carlos Paz 57");
        reviewEditText.setHint("Escriba su referencia aquí");

    }
    public void confirmarResena(View view) {

        String nombre = nombreEditText.getText().toString();
        String legajo = legajoEditText.getText().toString();
        String direccion = direccionEditText.getText().toString();
        String resena = reviewEditText.getText().toString();
        String calificacion = ratingSpinner.getSelectedItem().toString();


        System.out.println("Nombre: " + nombre);
        System.out.println("Legajo: " + legajo);
        System.out.println("Dirección: " + direccion);
        System.out.println("Reseña: " + resena);
        System.out.println("Calificación: " + calificacion);


        Toast.makeText(this, "Se guardo la reseña exitosamente",Toast.LENGTH_SHORT).show();

    }
}