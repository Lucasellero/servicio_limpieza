package com.example.servicio_limpieza;

public class UsuarioService {
    private com.example.servicio_limpieza.databaseHelper databaseHelper;

    public UsuarioService() {
        this.databaseHelper = new databaseHelper();
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