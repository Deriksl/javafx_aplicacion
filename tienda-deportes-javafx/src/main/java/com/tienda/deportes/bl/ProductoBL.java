package com.tienda.deportes.bl;

import java.util.List;

import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;

public class ProductoBL {

    private ProductoDAO productoDAO;

    public ProductoBL() {
        productoDAO = new ProductoDAO();
    }

    public List<Producto> obtenerProductos() {
        return productoDAO.obtenerTodos();
    }
}