package com.example.servicio_limpieza;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class reservarLimpieza extends AppCompatActivity {

    Spinner spinnerModo;
    TextView editTextDate;
    Button btnConfirmar, btnConfirmarPago, btnCancelar;
    LinearLayout layoutDetalles;
    TextView tvNombreEmpleado, tvDuracionPrecio;
    private int propiedadId;
    private String barrio;
    private int duracionEstimada;
    private int precioEstimado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_limpieza);

        editTextDate = findViewById(R.id.editTextDate);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago);
        layoutDetalles = findViewById(R.id.layoutDetalles);
        tvNombreEmpleado = findViewById(R.id.tvNombreEmpleado);
        tvDuracionPrecio = findViewById(R.id.tvDuracionPrecio);

        propiedadId = getIntent().getIntExtra("propiedad_id", -1);
        barrio = getIntent().getStringExtra("barrio");

        spinnerModo = findViewById(R.id.Modo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Modo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModo.setAdapter(adapter);

        // Configurar clic en el campo de texto de fecha para mostrar el calendario
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarReserva();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeccionAzul();
                btnConfirmarPago.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.GONE);
            }
        });

        btnConfirmarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPago();
            }
        });
        btnConfirmarPago.setVisibility(View.GONE);
    }

    private void mostrarCalendario() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Mostrar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(reservarLimpieza.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Actualizar el campo de texto con la fecha seleccionada
                        editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void mostrarSeccionAzul() {
        // Mostrar la sección azul con los detalles del empleado y cálculos
        layoutDetalles.setVisibility(View.VISIBLE);
        // Llamar al método para buscar empleado disponible y actualizar los detalles
        new BuscarEmpleadoDisponibleTask().execute(propiedadId);
    }

    private void ocultarSeccionAzul() {
        // Ocultar la sección azul
        layoutDetalles.setVisibility(View.GONE);
    }

    private void confirmarPago() {
        // Ocultar la sección azul al confirmar el pago
        ocultarSeccionAzul();

        // Volver al home
        Intent intent = new Intent(reservarLimpieza.this, home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpiar todas las actividades anteriores
        startActivity(intent);
    }

    private void cancelarReserva() {
        // Volver al home
        Intent intent = new Intent(reservarLimpieza.this, home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpiar todas las actividades anteriores
        startActivity(intent);
    }

    private class BuscarEmpleadoDisponibleTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int propiedadId = params[0];
            String mensaje = "¡Reserva de limpieza exitosa!";

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String nombreBarrio = null;

            try {
                conn = conexionBD();
                if (conn == null) {
                    return "No se pudo establecer conexión con la base de datos";
                }

                // Consulta para obtener el barrio de la propiedad
                String sql2 = "Select barrio from propiedades where PK_propiedad_ID = ?";
                stmt = conn.prepareStatement(sql2);
                stmt.setInt(1, propiedadId);
                ResultSet rs2 = stmt.executeQuery();

                if (rs2.next()) {
                    nombreBarrio = rs2.getString("barrio");
                }

                // Consulta para obtener el empleado y el barrio asociado
                String sql = "SELECT TOP 1 * FROM personal WHERE barrio = ? AND disponible = 1";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombreBarrio);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    int empleadoId = rs.getInt("PK_personal_ID");
                    String nombreEmpleado = rs.getString("nombre");
                    String barrio = rs.getString("barrio");

                    // Actualizar el estado del empleado a ocupado si es necesario
                    actualizarEstadoEmpleado(conn, empleadoId, false);

                    // Mostrar los datos del empleado encontrado en la sección azul
                    mostrarDatosEmpleado(nombreEmpleado);
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

    private void mostrarDatosEmpleado(String nombreEmpleado) {
        tvNombreEmpleado.setText("Empleado: "+ nombreEmpleado);
        calcularDuracionPrecio();
    }

    private void calcularDuracionPrecio() {
        // Obtener el tamaño de la propiedad (ejemplo: tamaño en metros cuadrados)
        int tamanoPropiedad = obtenerTamanoPropiedad(propiedadId);

        // Obtener el modo de limpieza seleccionado
        String modoLimpieza = spinnerModo.getSelectedItem().toString();

        // Calcular la duración estimada en horas
        if (modoLimpieza.equals("Regular")) {
            if (tamanoPropiedad < 60) {
                duracionEstimada = 2;
            } else if (tamanoPropiedad < 90) {
                duracionEstimada = 3;
            } else if (tamanoPropiedad < 120) {
                duracionEstimada = 4;
            } else if (tamanoPropiedad < 160) {
                duracionEstimada = 5;
            } else if (tamanoPropiedad < 200) {
                duracionEstimada = 6;
            } else if (tamanoPropiedad < 400) {
                duracionEstimada = 8;
            } else {
                // Ejemplo: si es mayor o igual a 400 metros cuadrados, duración de 10 horas
                duracionEstimada = 10;
            }
        } else if (modoLimpieza.equals("Profunda")) {
            if (tamanoPropiedad < 60) {
                duracionEstimada = 2;
            } else if (tamanoPropiedad < 90) {
                duracionEstimada = 3;
            } else if (tamanoPropiedad < 120) {
                duracionEstimada = 4;
            } else if (tamanoPropiedad < 160) {
                duracionEstimada = 5;
            } else if (tamanoPropiedad < 200) {
                duracionEstimada = 6;
            } else if (tamanoPropiedad < 400) {
                duracionEstimada = 8;
            } else {
                // Ejemplo: si es mayor o igual a 400 metros cuadrados, duración de 10 horas
                duracionEstimada = 10;
            }
            duracionEstimada = calcularDuracionModoProfundo(duracionEstimada);
        } else if (modoLimpieza.equals("Express")) {
            if (tamanoPropiedad < 60) {
                duracionEstimada = 2;
            } else if (tamanoPropiedad < 90) {
                duracionEstimada = 3;
            } else if (tamanoPropiedad < 120) {
                duracionEstimada = 4;
            } else if (tamanoPropiedad < 160) {
                duracionEstimada = 5;
            } else if (tamanoPropiedad < 200) {
                duracionEstimada = 6;
            } else if (tamanoPropiedad < 400) {
                duracionEstimada = 8;
            } else {
                // Ejemplo: si es mayor o igual a 400 metros cuadrados, duración de 10 horas
                duracionEstimada = 10;
            }
            duracionEstimada = calcularDuracionModoExpress(duracionEstimada);
        }
        // Calcular el precio estimado
        precioEstimado = duracionEstimada * 15000;

        // Mostrar la duración estimada y el precio estimado en el TextView correspondiente
        tvDuracionPrecio.setText("Duracion estimada: " + duracionEstimada + " horas\nPrecio estimado: $" + precioEstimado);
    }

    private int obtenerTamanoPropiedad(int propiedadId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int tamanoPropiedad = 0;

        try {
            conn = conexionBD();
            if (conn == null) {
                // Manejo de error: no se pudo establecer conexión con la base de datos
                return 0;
            }

            String sql = "SELECT tamano FROM propiedades WHERE PK_propiedad_ID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, propiedadId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                tamanoPropiedad = rs.getInt("tamano");
            }
        } catch (SQLException e) {
            // Manejo de errores SQL
            e.printStackTrace();
        } finally {
            // Cerrar ResultSet, PreparedStatement y Connection en el bloque finally
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return tamanoPropiedad;
    }

    private int calcularDuracionModoProfundo(int duracion) {
        // Implementar la lógica para calcular la duración en modo Profundo
        // Por ejemplo, podría ser el doble de la duración en modo Regular
        return duracion * 2;
    }

    private int calcularDuracionModoExpress(int duracion) {
        // Implementar la lógica para calcular la duración en modo Express
        // Por ejemplo, podría ser la mitad de la duración en modo Regular
        return duracion / 2;
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

