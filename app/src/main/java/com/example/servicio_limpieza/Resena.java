package com.example.servicio_limpieza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
    private RatingBar ratingBar;
    private Button btnConfirmar;
    private int empleadoId;
    private int propiedadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resena);

        propiedadId = getIntent().getIntExtra("propiedad_id", -1);
        empleadoId = getIntent().getIntExtra("empleado_id", -1);

        nombreEditText = findViewById(R.id.nombre);
        legajoEditText = findViewById(R.id.legajo);
        barrioEditText = findViewById(R.id.barrio);
        reviewEditText = findViewById(R.id.review);
        ratingBar = findViewById(R.id.ratingBar);
        btnConfirmar = findViewById(R.id.accept_button);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarResena();
                Intent intent = new Intent(Resena.this, historialLimpieza.class);
                startActivity(intent);
            }
        });

        // Load employee details from the database
        new LoadEmployeeDetailsTask().execute(empleadoId);
    }

    private void confirmarResena() {
        String resena = reviewEditText.getText().toString();
        String calificacion = String.valueOf(ratingBar.getRating());

        new ActualizarCalificacionTask().execute(resena, calificacion);
    }

    private class LoadEmployeeDetailsTask extends AsyncTask<Integer, Void, EmployeeDetails> {

        @Override
        protected EmployeeDetails doInBackground(Integer... params) {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return null;
                }

                String sql = "SELECT nombre, apellido, PK_personal_ID, barrio FROM personal WHERE PK_personal_ID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, params[0]);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    EmployeeDetails details = new EmployeeDetails();
                    details.nombre = rs.getString("nombre");
                    details.legajo = rs.getString("PK_personal_ID");
                    details.barrio = rs.getString("barrio");
                    details.apellido = rs.getString("apellido");
                    return details;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(EmployeeDetails details) {
            if (details != null) {
                nombreEditText.setText("Empleado a cargo: " + details.nombre + " " + details.apellido);
                legajoEditText.setText("Legajo del empleado: " + details.legajo);
                barrioEditText.setText("Barrio: " + details.barrio);
            } else {
                Toast.makeText(Resena.this, "Error al cargar los datos del empleado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ActualizarCalificacionTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String resena = params[0];
            float calificacion = Float.parseFloat(params[1]);

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return false;
                }

                // Actualizar la calificación y cambiar el estado a "Calificada"
                String sql = "UPDATE limpiezas SET calificacion = ?, calificar = 'Calificada' WHERE FK_propiedad_ID = ? and FK_empleado_ID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setFloat(1, calificacion);
                stmt.setInt(2, propiedadId);
                stmt.setInt(3,empleadoId);
                int rowsAffected = stmt.executeUpdate();

                return rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Resena.this, "Calificación y estado de la limpieza actualizados exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Resena.this, "Error al actualizar la calificación y estado de la limpieza. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Connection conexionBD() {
        Connection conexion = databaseConnection.getConnection();
        return conexion;
    }

    // Clase para almacenar los detalles del empleado
    private static class EmployeeDetails {
        String nombre;
        String legajo;
        String barrio;
        String apellido;
    }
}


