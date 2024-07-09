package com.example.servicio_limpieza;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Resena extends AppCompatActivity {

    private TextView nombreEditText;
    private TextView legajoEditText;
    private TextView barrioEditText;
    private EditText reviewEditText;
    private Spinner ratingSpinner;

    private int empleadoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resena);

        int propiedadId = getIntent().getIntExtra("propiedad_id", -1);
        empleadoId = getIntent().getIntExtra("empleado_id", -1); // Obtener el ID del empleado

        nombreEditText = findViewById(R.id.nombre);
        legajoEditText = findViewById(R.id.legajo);
        barrioEditText = findViewById(R.id.barrio);
        reviewEditText = findViewById(R.id.review);
        ratingSpinner = findViewById(R.id.spinner_rating);

        // Initialize the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);

        // Load employee details from the database
        new LoadEmployeeDetailsTask().execute(empleadoId);
    }

    public void confirmarResena(View view) {
        String nombre = nombreEditText.getText().toString();
        String legajo = legajoEditText.getText().toString();
        String barrio = barrioEditText.getText().toString();
        String resena = reviewEditText.getText().toString();
        String calificacion = ratingSpinner.getSelectedItem().toString();

        new ActualizarCalificacionTask().execute(nombre, legajo, barrio, resena, calificacion);
    }

    private class LoadEmployeeDetailsTask extends AsyncTask<Integer, Void, Boolean> {
        private String nombre;
        private String legajo;
        private String barrio;

        @Override
        protected Boolean doInBackground(Integer... params) {
            int empleadoId = params[0];

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return false;
                }

                String sql = "SELECT nombre, legajo, barrio FROM personal WHERE PK_personal_ID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, empleadoId);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    nombre = rs.getString("nombre");
                    legajo = rs.getString("legajo");
                    barrio = rs.getString("barrio");
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                nombreEditText.setText(nombre);
                legajoEditText.setText(legajo);
                barrioEditText.setText(barrio);
            } else {
                Toast.makeText(Resena.this, "Error al cargar los datos del empleado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ActualizarCalificacionTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String nombre = params[0];
            String legajo = params[1];
            String barrio = params[2];
            String resena = params[3];
            float calificacion = Float.parseFloat(params[4]);

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return false;
                }

                String sql = "UPDATE personal SET calificación = ? WHERE PK_personal_ID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setFloat(1, calificacion);
                stmt.setInt(2, empleadoId);
                stmt.executeUpdate();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Resena.this, "Calificación actualizada exitosamente", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after saving
            } else {
                Toast.makeText(Resena.this, "Error al actualizar la calificación. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Connection conexionBD() {
        Connection conexion = databaseConnection.getConnection();
        return conexion;
    }
}
