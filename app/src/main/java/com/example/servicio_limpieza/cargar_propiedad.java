package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class cargar_propiedad extends AppCompatActivity {
    Spinner spinnerBarrio;
    private EditText nameEditText;
    private Spinner barrioEditText;
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
        barrioEditText = findViewById(R.id.Barrio);
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

        spinnerBarrio = findViewById(R.id.Barrio);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.barrio_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBarrio.setAdapter(adapter);

    }


    private boolean validateFields() {
        // Validar que todos los campos estén completos
        return !nameEditText.getText().toString().isEmpty() &&
                !barrioEditText.getSelectedItem().toString().isEmpty() &&
                !addressEditText.getText().toString().isEmpty() &&
                !stateEditText.getText().toString().isEmpty() &&
                !sizeEditText.getText().toString().isEmpty() &&
                !typeEditText.getText().toString().isEmpty();
    }

    private void cargarPropiedad() {
        String nombre = nameEditText.getText().toString();
        String barrio = barrioEditText.getSelectedItem().toString();
        String direccion = addressEditText.getText().toString();
        String estado = stateEditText.getText().toString();
        int tamano = Integer.parseInt(sizeEditText.getText().toString());
        String tipo = typeEditText.getText().toString();

        // Obtener el ID del usuario en sesión
        usuario Usuario = usuario.getInstance();
        int propietarioId = Usuario.getId();

        // Ejecutar AsyncTask para cargar la propiedad
        new CargarPropiedadTask().execute(nombre, barrio, direccion, estado, String.valueOf(tamano), tipo, String.valueOf(propietarioId));
    }

    private class CargarPropiedadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String mensaje;
            String nombre = params[0];
            String barrio = params[1];
            String direccion = params[2];
            String estado = params[3];
            int tamano = Integer.parseInt(params[4]);
            String tipo = params[5];
            int propietarioId = Integer.parseInt(params[6]);

            Log.d("CargarPropiedadTask", "Datos recibidos: " + nombre + ", " + barrio + ", " + direccion + ", " + estado + ", " + tamano + ", " + tipo + ", " + propietarioId);

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    Log.e("CargarPropiedadTask", "No se pudo establecer conexión con la base de datos");
                    return "No se pudo establecer conexión con la base de datos";
                }

                String sql = "INSERT INTO propiedades (nombre, barrio, direccion, estado, tamano, tipo, FK_propietario_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, String.valueOf(nombre));
                stmt.setString(2, String.valueOf(barrio));
                stmt.setString(3, String.valueOf(direccion));
                stmt.setString(4, String.valueOf(estado));
                stmt.setInt(5, Integer.parseInt(String.valueOf(tamano)));
                stmt.setString(6, String.valueOf(tipo));
                stmt.setInt(7, Integer.parseInt(String.valueOf(propietarioId)));

                int result = stmt.executeUpdate();
                mensaje = result > 0 ? "Propiedad cargada exitosamente" : "Error al cargar la propiedad";

                usuario Usuario = usuario.getInstance();
                PreparedStatement propiedadesStmt = conn.prepareStatement("SELECT * FROM propiedades WHERE nombre = ? and direccion = ?");
                propiedadesStmt.setString(1,String.valueOf(nombre));
                propiedadesStmt.setString(2,String.valueOf(direccion));
                ResultSet propiedadesResultSet = propiedadesStmt.executeQuery();

                if(propiedadesResultSet.next()) {
                    int propiedadId = propiedadesResultSet.getInt("PK_propiedad_ID");
                    Propiedad propiedad = new Propiedad(propiedadId, nombre, direccion, barrio, tamano, estado, tipo, propietarioId);
                    Usuario.agregarPropiedad(propiedad);
                }

            } catch (SQLException e) {
                mensaje = "Error al cargar la propiedad. Por favor, inténtalo de nuevo.";
                //mensaje = nombre + barrio + direccion+ estado + tamano + tipo + propietarioId;
                Log.e("CargarPropiedadTask", "Error al ejecutar la consulta SQL", e);
            } catch (Exception e) {
                mensaje = "Error inesperado. Por favor, inténtalo de nuevo.";
                Log.e("CargarPropiedadTask", "Error inesperado", e);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Log.e("CargarPropiedadTask", "Error al cerrar recursos", e);
                }
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
}

