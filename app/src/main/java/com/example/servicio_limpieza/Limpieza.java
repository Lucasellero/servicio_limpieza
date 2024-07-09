package com.example.servicio_limpieza;

public class Limpieza {
    private int propiedadId;
    private String fecha;
    private int empleadoId;

    public Limpieza() {
        // Constructor por defecto necesario para Firebase
    }

    public Limpieza(int propiedadId, String fecha, int empleadoId) {
        this.propiedadId = propiedadId;
        this.fecha = fecha;
        this.empleadoId = empleadoId;
    }

    public int getPropiedadId() {
        return propiedadId;
    }

    public void setPropiedadId(int propiedadId) {
        this.propiedadId = propiedadId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }
}
