package net.elpuig.daw2.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class WebSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("Sesión creada: " + event.getSession().getId());
        ServletContext ctx = event.getSession().getServletContext();
        synchronized (ctx) {
            Integer conectados = (Integer) ctx.getAttribute("usuariosConectados");
            if (conectados == null) conectados = 0;
            ctx.setAttribute("usuariosConectados", conectados + 1);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("Sesión destruida: " + event.getSession().getId());
        ServletContext ctx = event.getSession().getServletContext();
        synchronized (ctx) {
            Integer conectados = (Integer) ctx.getAttribute("usuariosConectados");
            if (conectados == null) conectados = 0;
            ctx.setAttribute("usuariosConectados", conectados - 1);
        }
    }
}