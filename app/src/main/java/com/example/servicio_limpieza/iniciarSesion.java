package com.example.servicio_limpieza;

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

    private class IniciarSesionTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean exito = false;
            try {
                Connection conn = conexionBD();
                if (conn == null) {
                    return false;
                }

                PreparedStatement cuenta = conn.prepareStatement("SELECT * FROM propietarios WHERE email = ? AND contraseña = ?");
                cuenta.setString(1, editEmail.getText().toString());
                cuenta.setString(2, editContraseña.getText().toString());
                ResultSet resultSet = cuenta.executeQuery();
                exito = resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return exito;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
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
            String url = "jdbc:jtds:sqlserver://192.168.1.15:1433;databaseName=seminario;user=sa;password=1234";
            conexion = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
}

