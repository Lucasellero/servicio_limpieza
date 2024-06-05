package com.example.servicio_limpieza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conectar {

    private static conectar instancia;
    private static Connection connection;

    private conectar(String uri) throws SQLException {
        connection = DriverManager.getConnection(uri);
    }

    public static conectar getInstance() throws SQLException {
        if (instancia == null) {
            String uri = usuarios.getInstance().getUri();
            instancia = new conectar(uri);
            System.out.println("Conectado a SQL Server exitosamente");
        }
        return instancia;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                instancia = null;
                System.out.println("Desconectado de SQL Server exitosamente");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }


}