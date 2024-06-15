package com.example.servicio_limpieza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class cargar_propiedad extends AppCompatActivity {

    private EditText nameEditText;
    private EditText cityEditText;
    private EditText addressEditText;
    private EditText stateEditText;
    private EditText sizeEditText;
    private EditText typeEditText;
    private Button acceptButton;

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

        // Configurar el Listener para el botón de aceptar
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Si los campos son válidos, cargar la propiedad
                    cargarPropiedad();
                } else {
                    // Si hay campos inválidos, mostrar un mensaje de error
                    Toast.makeText(cargar_propiedad.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void cargarPropiedad() {
        String nombre = nameEditText.getText().toString();
        String barrio = cityEditText.getText().toString();
        String direccion = addressEditText.getText().toString();
        int estado = Integer.parseInt(stateEditText.getText().toString());
        float tamano = Float.parseFloat(sizeEditText.getText().toString());
        String tipo = typeEditText.getText().toString();

        // Obtener el ID del usuario en sesión
        int propietarioId = obtenerIdUsuarioEnSesion();

        // Ejecutar AsyncTask para cargar la propiedad
        new CargarPropiedadTask().execute(nombre, barrio, direccion, String.valueOf(estado), String.valueOf(tamano), tipo, String.valueOf(propietarioId));
    }

    private int obtenerIdUsuarioEnSesion() {
        // Implementa tu lógica para obtener el ID del usuario en sesión, por ejemplo desde SharedPreferences
        // Aquí un ejemplo básico (debes implementar según cómo manejes la sesión en tu app)
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        return sharedPreferences.getInt("idUsuario", -1); // -1 si no se encuentra
    }

    private class CargarPropiedadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String mensaje;
            String nombre = params[0];
            String barrio = params[1];
            String direccion = params[2];
            int estado = Integer.parseInt(params[3]);
            float tamano = Float.parseFloat(params[4]);
            String tipo = params[5];
            int propietarioId = Integer.parseInt(params[6]);

            try {
                Connection conn = conexionBD();
                if (conn == null) {
                    return "No se pudo establecer conexión con la base de datos";
                }

                PreparedStatement stmt = conn.prepareStatement("INSERT INTO propiedades (nombre, barrio, direccion, estado, tamano, tipo, FK_propietario_ID) VALUES (?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, nombre);
                stmt.setString(2, barrio);
                stmt.setString(3, direccion);
                stmt.setInt(4, estado);
                stmt.setFloat(5, tamano);
                stmt.setString(6, tipo);
                stmt.setInt(7, propietarioId);

                int result = stmt.executeUpdate();
                mensaje = result > 0 ? "Propiedad cargada exitosamente" : "Error al cargar la propiedad";
            } catch (SQLException e) {
                mensaje = "Error al cargar la propiedad. Por favor, inténtalo de nuevo.";
            }
            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            if (result.equals("Propiedad cargada exitosamente")) {
                Intent intent = new Intent(cargar_propiedad.this, home.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public Connection conexionBD() {
        Connection conexion = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String url = "jdbc:jtds:sqlserver://192.168.1.15:1433;databaseName=seminario;user=sa;password=1234";
            conexion = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
}

