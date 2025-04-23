package com.tienda.deportes.model;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private String marca;
    private double precio;
    private int stock;
    private String imagen;  // Nuevo campo para la ruta de la imagen
    private int cantidadEnCarrito;

    // Getters y Setters (asegúrate de agregar el nuevo campo)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImagen() { return imagen; }          // Nuevo getter
    public void setImagen(String imagen) { this.imagen = imagen; }  // Nuevo setter
    public int getCantidadEnCarrito() { return cantidadEnCarrito; }
    public void setCantidadEnCarrito(int cantidadEnCarrito) { this.cantidadEnCarrito = cantidadEnCarrito; }
}