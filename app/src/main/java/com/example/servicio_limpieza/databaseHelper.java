package com.example.servicio_limpieza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class databaseHelper {
    // Definir las credenciales de la base de datos
    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName=seminario";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    // Método para obtener el usuario a partir de la base de datos
    public usuario obtenerUsuario(String email, String contraseña) {
        usuario usuario = null;
        String query = "SELECT * FROM propietarios WHERE email = ? AND contraseña = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement cuenta = conn.prepareStatement(query)) {

            cuenta.setString(1, email);
            cuenta.setString(2, contraseña);

            try (ResultSet rs = cuenta.executeQuery()) {
                if (rs.next()) {
                    int dbUsuario = rs.getInt("PK_propietario_ID");
                    String dbEmail = rs.getString("email");
                    String dbNombre = rs.getString("nombre");
                    String dbContraseña = rs.getString("contraseña");
                    String dbGenero = rs.getString("genero");
                    String dbApellido = rs.getString("apellido");
                    String dbTelefono = rs.getString("telefono");
                    String dbDireccion = rs.getString("direccion");

                    // Crear o obtener la instancia Singleton del Usuario
                    usuario = usuario.getInstance(dbUsuario,dbNombre, dbApellido, dbDireccion, dbTelefono, dbGenero, dbEmail, dbContraseña);
                    // inicializar otros atributos si es necesario...
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }
}