package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class iniciarSesion extends AppCompatActivity {

    EditText editEmail, editContraseña;
    Button buttoniniciarSesion;

    TextView perdi_mi_contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        editEmail = findViewById(R.id.Email);
        editContraseña = findViewById(R.id.Contraseña);
        buttoniniciarSesion = findViewById(R.id.buttonIniciarSesion);
        perdi_mi_contra = findViewById(R.id.textView2);

        buttoniniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IniciarSesion", "Botón Iniciar Sesión presionado");
                iniciarSesion();
            }
        });

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

        perdi_mi_contra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(iniciarSesion.this,recuperar_contra.class);
                startActivity(intento);
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


    public void iniciarSesion() {
        try {
            // Consulta para verificar si el email y la contraseña existen en la base de datos
            PreparedStatement cuenta = conexionBD().prepareStatement("SELECT * FROM propietarios WHERE email = ? AND contraseña = ?");
            cuenta.setString(1, editEmail.getText().toString());
            cuenta.setString(2, editContraseña.getText().toString());
            ResultSet resultSet = cuenta.executeQuery();

            if (resultSet.next()) {
                // Si existe un registro con el email y la contraseña proporcionados
                Toast.makeText(getApplicationContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                Log.d("Conexion BD", "Inicio de sesión exitoso");

                // Redirigir a la pantalla de inicio (home)
                    Intent intent = new Intent(iniciarSesion.this, home.class);

                    startActivity(intent);

                    finish(); // Opcional: cerrar la actividad actual


            } else {
                // Si no existe el registro, mostrar un mensaje de error
                Toast.makeText(getApplicationContext(), "Email o contraseña incorrectos. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                Log.d("Error SQL", "Email o contraseña incorrectos");
            }

            resultSet.close();
            cuenta.close();

        } catch (SQLException e) {
            Log.e("Error SQL", "Error al ejecutar consulta: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error al iniciar sesión. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
        }
    }
}