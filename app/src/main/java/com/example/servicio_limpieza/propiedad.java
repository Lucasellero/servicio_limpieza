package com.example.servicio_limpieza;

public class propiedad {
    private int idPropiedad;
    private String nombre;
    private String direccion;
    private String barrio;
    private int tamano;
    private String estado;
    private String tipo;
    private int idPropietario;

    public propiedad(int idPropiedad, String nombre, String direccion, String barrio, int tamano,
                     String estado, String tipo, int idPropietario){
        this.idPropiedad = idPropiedad;
        this.nombre = nombre;
        this.direccion = direccion;
        this.barrio = barrio;
        this.tamano = tamano;
        this.estado = estado;
        this.tipo = tipo;
        this.idPropietario = idPropietario;
    }

    public int getIdPropiedad() {
        return idPropiedad;
    }

    public void setIdPropiedad(int idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }
}
