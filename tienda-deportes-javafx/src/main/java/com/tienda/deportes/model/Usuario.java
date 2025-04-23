package com.tienda.deportes.model;

public class Usuario {
    private int id;
    private String nombre;
    private String password;
    private String tipo;

    // Getters y Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public String getTipo() { 
        return tipo; 
    }
    
    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }

    // Método alternativo para compatibilidad con el controlador
    public String getUsuario() {
        return nombre;
    }
}