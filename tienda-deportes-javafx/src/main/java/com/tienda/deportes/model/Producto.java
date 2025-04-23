package com.tienda.deportes.model;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private String marca;
    private double precio;
    private int stock;
    private String imagen;
    private int cantidadEnCarrito;  // Para funcionalidad del carrito

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros básicos
    public Producto(String nombre, String descripcion, String marca, double precio, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCantidadEnCarrito() {
        return cantidadEnCarrito;
    }

    public void setCantidadEnCarrito(int cantidadEnCarrito) {
        this.cantidadEnCarrito = cantidadEnCarrito;
    }

    // Método para mostrar información del producto (útil para debugging)
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", imagen='" + imagen + '\'' +
                '}';
    }

    // Método para verificar si el producto es válido (antes de guardar)
    public boolean esValido() {
        return nombre != null && !nombre.trim().isEmpty() &&
               marca != null && !marca.trim().isEmpty() &&
               precio > 0 &&
               stock >= 0;
    }
}