package com.example.servicio_limpieza;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class ReservarLimpiezaActivity extends AppCompatActivity {

    Spinner spinnerModo;
    EditText editTextDate;
    Button buttonConfirmarFecha, buttonConfirmarPago;
    LinearLayout layoutInfoReserva;
    TextView textViewEmpleado, textViewCalificacion, textViewDuracion, textViewPrecio;
    private int propiedadId;
    private String barrio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_limpieza);

        propiedadId = getIntent().getIntExtra("propiedad_id", -1);

        barrio = getIntent().getStringExtra("barrio");

        new BuscarEmpleadoDisponibleTask().execute(propiedadId);

        spinnerModo = findViewById(R.id.Modo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Modo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModo.setAdapter(adapter);

        editTextDate = findViewById(R.id.editTextDate);
        buttonConfirmarFecha = findViewById(R.id.buttonConfirmarFecha);
        layoutInfoReserva = findViewById(R.id.layoutInfoReserva);
        textViewEmpleado = findViewById(R.id.textViewEmpleado);
        textViewCalificacion = findViewById(R.id.textViewCalificacion);
        textViewDuracion = findViewById(R.id.textViewDuracion);
        textViewPrecio = findViewById(R.id.textViewPrecio);
        buttonConfirmarPago = findViewById(R.id.buttonConfirmarPago);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReservarLimpiezaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        buttonConfirmarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarInfoReserva();
            }
        });

        buttonConfirmarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPago();
            }
        });
    }

    private void mostrarInfoReserva() {
        layoutInfoReserva.setVisibility(View.VISIBLE);
        buttonConfirmarPago.setVisibility(View.VISIBLE);
        textViewEmpleado.setText("Nombre del empleado: Juan Pérez");
        textViewCalificacion.setText("Calificación: 4.5");
        textViewDuracion.setText("Duración estimada: 2 horas");
        textViewPrecio.setText("Precio estimado: $50");
    }

    private void confirmarPago() {
        Toast.makeText(this, "Pago confirmado", Toast.LENGTH_SHORT).show();
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

                String sql2 = "SELECT barrio FROM propiedades WHERE PK_propiedad_ID = " + propiedadId;
                ResultSet rs2 = null;
                stmt = conn.prepareStatement(sql2);
                rs2 = stmt.executeQuery();

                if (rs2.next()) {
                    nombreBarrio = rs2.getString("barrio");
                }

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

                    actualizarEstadoEmpleado(conn, empleadoId, false);

                    mensaje = "Empleado encontrado: " + nombreEmpleado + ", Barrio: " + barrio;
                }
            } catch (SQLException e) {
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
            String url = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName=seminario;user=sa;password=1234";
            conexion = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
}


