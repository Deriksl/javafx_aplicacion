package com.tienda.deportes.model;

public class Sesion {
    private static Integer usuarioId;
    private static String nombreUsuario;
    private static String tipoUsuario;

    public static void iniciarSesion(int idUsuario, String nombre, boolean esAdmin) {
        usuarioId = idUsuario;
        nombreUsuario = nombre;
        tipoUsuario = esAdmin ? "admin" : "cliente";
        System.out.println("Sesión iniciada para: " + nombre + " (ID: " + usuarioId + ", Tipo: " + tipoUsuario + ")");
    }

    public static void verificarAdmin() throws SecurityException {
        if (!"admin".equals(tipoUsuario)) {
            throw new SecurityException("Acceso denegado: se requieren privilegios de administrador");
        }
    }

    public static boolean esAdmin() {
        return "admin".equals(tipoUsuario);
    }

    public static Integer getUsuarioId() {
        return usuarioId;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static String getTipoUsuario() {
        return tipoUsuario;
    }

    public static void cerrarSesion() {
        usuarioId = null;
        nombreUsuario = null;
        tipoUsuario = null;
        System.out.println("Sesión cerrada");
    }
}