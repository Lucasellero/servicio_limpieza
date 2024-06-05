package com.example.servicio_limpieza;

import android.os.Bundle;
import android.content.Intent;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class crear_cuenta extends AppCompatActivity {

    EditText editNombre, editApellido, editDireccion, editTelefono, editEmail, editContraseña;
    Spinner spinnerGenero;
    Button buttonCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        editNombre = findViewById(R.id.Nombre);
        editApellido = findViewById(R.id.Apellido);
        editDireccion = findViewById(R.id.Direccion);
        editTelefono = findViewById(R.id.Telefono);
        editEmail = findViewById(R.id.Email);
        editContraseña = findViewById(R.id.Contraseña);

        spinnerGenero = findViewById(R.id.Genero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genero_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);

        buttonCrearCuenta = findViewById(R.id.buttonCrearCuenta);

        // TextWatcher para validar el E-mail
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                if (email.contains("@")) {
                    editEmail.setError(null); // Clear error
                } else {
                    editEmail.setError("Email debe contener @");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        buttonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CrearCuenta", "Botón Crear Cuenta presionado");
                crearCuenta();
            }
        });
    }

    public Connection conexionBD() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName=seminario;user=sa;password=1234";
            conexion = DriverManager.getConnection(url);
            Log.d("Conexion BD", "Conexión exitosa");

        } catch (Exception e) {
            Log.e("Error Conexion BD", "Error al conectar: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error al conectar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }

    public void crearCuenta() {
        try {
            PreparedStatement cuenta = conexionBD().prepareStatement("INSERT INTO propietarios (nombre, apellido, direccion, telefono, genero, email, contraseña) VALUES (?, ?, ?, ?, ?, ?, ?)");
            cuenta.setString(1, editNombre.getText().toString());
            cuenta.setString(2, editApellido.getText().toString());
            cuenta.setString(3, editDireccion.getText().toString());
            cuenta.setString(4, editTelefono.getText().toString());
            cuenta.setString(5, spinnerGenero.getSelectedItem().toString());
            cuenta.setString(6, editEmail.getText().toString());
            cuenta.setString(7, editContraseña.getText().toString());
            cuenta.executeUpdate();

            Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            Log.d("Conexion BD", "Conexión exitosa");

            // Redirigir a la pantalla de inicio de sesión
            Intent intent = new Intent(crear_cuenta.this, iniciarSesion.class);
            startActivity(intent);
            finish(); // Opcional: cerrar la actividad actual

        } catch (SQLException e) {
            Log.e("Error SQL", "Error al ejecutar consulta: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error al crear la cuenta. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }
}
