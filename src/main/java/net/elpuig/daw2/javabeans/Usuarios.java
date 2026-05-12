package net.elpuig.daw2.javabeans;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

public class Usuarios implements HttpSessionBindingListener {

    private String nombre;

    public Usuarios(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("Usuario añadido a sesión: " + nombre);
        ServletContext ctx = event.getSession().getServletContext();
        synchronized (ctx) {
            Integer validados = (Integer) ctx.getAttribute("usuariosValidados");
            if (validados == null) validados = 0;
            ctx.setAttribute("usuariosValidados", validados + 1);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("Usuario eliminado de sesión: " + nombre);
        ServletContext ctx = event.getSession().getServletContext();
        synchronized (ctx) {
            Integer validados = (Integer) ctx.getAttribute("usuariosValidados");
            if (validados == null) validados = 0;
            ctx.setAttribute("usuariosValidados", validados - 1);
        }
    }
}