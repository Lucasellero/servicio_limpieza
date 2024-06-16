package com.example.servicio_limpieza;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        Connection conexion = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String url = "jdbc:jtds:sqlserver://192.168.1.15:1433;databaseName=seminario;user=sa;password=1234";
            conexion = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            Log.e("DatabaseConnection", "Error al establecer conexión con la base de datos", e);
        }
        return conexion;
    }
}
