package com.example.servicio_limpieza;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservarLimpiezaActivity extends AppCompatActivity {

    private int propiedadId;
    private String barrio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_limpieza);

        propiedadId = getIntent().getIntExtra("propiedad_id", -1);

        // Obtener el barrio de la propiedad a través de una consulta o de los extras del Intent
        // Aquí debes agregar la lógica para obtener el barrio de la propiedad

        // Por ejemplo, si pasas el barrio en el Intent:
        barrio = getIntent().getStringExtra("barrio");


        // Iniciar la tarea para buscar el empleado disponible
        new BuscarEmpleadoDisponibleTask().execute(propiedadId);
    }

    private class BuscarEmpleadoDisponibleTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int propiedadId = params[0];
            String mensaje = "No se encontró un empleado disponible.";

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String nombreBarrio = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return "No se pudo establecer conexión con la base de datos";
                }

                String sql2 = "Select barrio from propiedades where PK_propiedad_ID = "+propiedadId;
                ResultSet rs2 = null;
                stmt = conn.prepareStatement(sql2);
                rs2 = stmt.executeQuery();

                if (rs2.next()) {
                    nombreBarrio = rs2.getString("barrio");
                }

                // Consulta para obtener el empleado y el barrio asociado
                String sql = "SELECT TOP 1 * FROM personal WHERE barrio = ? AND disponible = 1";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombreBarrio);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    int empleadoId;
                    String nombreEmpleado;
                    empleadoId = rs.getInt("PK_personal_ID");
                    nombreEmpleado = rs.getString("nombre");
                    barrio = rs.getString("barrio");

                    // Actualizar el estado del empleado a ocupado si es necesario
                    actualizarEstadoEmpleado(conn, empleadoId, false);

                    mensaje = "Empleado encontrado: " + nombreEmpleado + ", Barrio: " + barrio;
                }
            } catch (SQLException e){
                    mensaje = "Error al buscar el empleado disponible. Por favor, inténtalo de nuevo.";
                    e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarEstadoEmpleado(Connection conn, int empleadoId, boolean disponible) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE personal SET disponible = ? WHERE PK_personal_ID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, disponible ? 1 : 0);
            stmt.setInt(2, empleadoId);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
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

