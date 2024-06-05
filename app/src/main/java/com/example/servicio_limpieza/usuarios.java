package com.example.servicio_limpieza;

public class usuarios {
    private static usuarios instance;

    private String uri;


    private usuarios(){
        uri = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName=seminario;user=sa;password=1234";
    }

    public static usuarios getInstance(){
        if (instance == null){
            instance = new usuarios();
        }
        return instance;
    }

    public String getUri(){
        return uri;
    }

    /*public String getDatebaseName(){
        return datebaseName;
    }*/
}