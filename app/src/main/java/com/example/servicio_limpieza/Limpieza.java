package com.example.servicio_limpieza;

public class Limpieza {
    private int propiedadId;
    private String fecha;

    private int empleadoId;

    public Limpieza(int propiedadId, String fecha, int empleadoId) {
        this.propiedadId = propiedadId;
        this.fecha = fecha;
        this.empleadoId = empleadoId;
    }

    public int getPropiedadId() {
        return propiedadId;
    }

    public String getFecha() {
        return fecha;
    }

    public int getEmpleadoId(){
        return empleadoId;
    }
}