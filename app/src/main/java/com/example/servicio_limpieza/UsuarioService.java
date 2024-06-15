package com.example.servicio_limpieza;

import com.example.servicio_limpieza.DatabaseHelper;

public class UsuarioService {
    private DatabaseHelper databaseHelper;

    public UsuarioService() {
        this.databaseHelper = new DatabaseHelper();
    }

    public usuario login(String email, String contraseña) {
        usuario usuario = databaseHelper.obtenerUsuario(email, contraseña);
        if (usuario != null) {
            // Aquí puedes agregar cualquier lógica adicional de negocio
            System.out.println("Usuario autenticado correctamente.");
        } else {
            System.out.println("Credenciales inválidas.");
        }
        return usuario;
    }
}