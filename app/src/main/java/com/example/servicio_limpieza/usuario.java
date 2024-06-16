package com.example.servicio_limpieza;

import java.util.ArrayList;
import java.util.List;

public final class usuario {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String genero;
    private String email;
    private String contrasena;
    private static usuario instancia;
    private List<Propiedad> propiedades;


    // Constructor
    private usuario(int idUsuario, String nombre, String apellido, String direccion, String telefono,
                    String genero, String email, String contrasena) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.telefono = telefono;
        this.genero = genero;
        this.email = email;
        this.contrasena = contrasena;
        this.propiedades = new ArrayList<>();
    }

    public static usuario getInstance(int idUsuario, String nombre, String apellido, String direccion, String telefono,
                                      String genero, String email, String contrasena) {
        if (instancia == null) {
            instancia = new usuario(idUsuario, nombre, apellido, direccion, telefono, genero, email, contrasena);
        }
        return instancia;
    }

    public static usuario getInstance(){return instancia;}

    public static void resetInstance() {
        instancia = null;
    }


    public int getId() {
        return idUsuario;
    }

    public String getNombre(){
        return nombre;
    }

    public void setPropiedades(List<Propiedad> propidades) {
        this.propiedades = propiedades;
    }

    public void agregarPropiedad(Propiedad propiedad){
        propiedades.add(propiedad);
    }
}