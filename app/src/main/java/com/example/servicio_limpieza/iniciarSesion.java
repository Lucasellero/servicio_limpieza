package com.example.servicio_limpieza;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;


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
                // Ejecutar AsyncTask para iniciar sesión
                new IniciarSesionTask().execute();
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
                Intent intento = new Intent(iniciarSesion.this, recuperar_contra.class);
                startActivity(intento);
            }
        });
    }

    private class IniciarSesionTask extends AsyncTask<Void, Void, usuario> {
        @Override
        protected usuario doInBackground(Void... voids) {
            usuario Usuario = null;
            try {
                Connection conn = conexionBD();
                if (conn == null) {
                    return null;
                }

                PreparedStatement cuenta = conn.prepareStatement("SELECT * FROM propietarios WHERE email = ? AND contraseña = ?");
                cuenta.setString(1, editEmail.getText().toString());
                cuenta.setString(2, editContraseña.getText().toString());
                ResultSet resultSet = cuenta.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("PK_propietario_ID");
                    String nombre = resultSet.getString("nombre");
                    String apellido = resultSet.getString("apellido");
                    String direccion = resultSet.getString("direccion");
                    String telefono = resultSet.getString("telefono");
                    String genero = resultSet.getString("genero");
                    String email = resultSet.getString("email");
                    String contrasena = resultSet.getString("contraseña");

                    // Crear el objeto Usuario
                    Usuario = usuario.getInstance(id, nombre, apellido, direccion, telefono, genero, email, contrasena);

                    // Cargar propiedades del usuario
                    PreparedStatement propiedadesStmt = conn.prepareStatement("SELECT * FROM propiedades WHERE FK_propietario_ID = ?");
                    propiedadesStmt.setInt(1, id);
                    ResultSet propiedadesResultSet = propiedadesStmt.executeQuery();

                    List<Propiedad> propiedades = new ArrayList<>();
                    while (propiedadesResultSet.next()) {
                        int propiedadId = propiedadesResultSet.getInt("PK_propiedad_ID");
                        String propiedadNombre = propiedadesResultSet.getString("nombre");
                        String propiedadBarrio = propiedadesResultSet.getString("barrio");
                        String propiedadDireccion = propiedadesResultSet.getString("direccion");
                        String propiedadEstado = propiedadesResultSet.getString("estado");
                        int propiedadTamano = propiedadesResultSet.getInt("tamano");
                        String propiedadTipo = propiedadesResultSet.getString("tipo");
                        int propietarioId = propiedadesResultSet.getInt("FK_propietario_ID");

                        Propiedad propiedad = new Propiedad(propiedadId, propiedadNombre, propiedadDireccion, propiedadBarrio, propiedadTamano, propiedadEstado, propiedadTipo, propietarioId);
                        propiedades.add(propiedad);
                    }
                    Usuario.setPropiedades(propiedades);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Usuario;
        }

        @Override
        protected void onPostExecute(usuario usuario) {
            if (usuario != null) {
                Toast.makeText(getApplicationContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                // Aquí puedes almacenar el usuario en SharedPreferences o mantenerlo como referencia global
                // Ejemplo de almacenamiento en SharedPreferences
                guardarUsuarioEnSesion(usuario);

                Intent intent = new Intent(iniciarSesion.this, home.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Email o contraseña incorrectos. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
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

    private void guardarUsuarioEnSesion(usuario usuario) {
        // Implementa tu lógica para guardar el usuario en sesión, por ejemplo, en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idUsuario", usuario.getId());
        // Aquí puedes guardar más detalles del usuario si es necesario
        editor.apply();
    }
}
