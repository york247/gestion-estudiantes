package net.elpuig.daw2.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.elpuig.daw2.javabeans.Usuarios;

import java.io.IOException;

public class Login extends HttpServlet {

    private static final String PG_ERROR = "/errorlogin.jsp";
    private static final String PG_ALTA  = "/Controlador?operacion=alta";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String usuarioIntro = req.getParameter("txtUsuario");
        String passwIntro   = req.getParameter("txtContrasenya");

        HttpSession sesion = req.getSession(true);

        Usuarios usuarioSesion = (Usuarios) sesion.getAttribute("usuario");

        String user = null;
        if (usuarioSesion == null) {
            user = validarUsuario(usuarioIntro, passwIntro);
        }

        String siguientePag;

        if (usuarioSesion == null && user == null) {
            siguientePag = PG_ERROR;
        } else {
            if (usuarioSesion == null) {
                Usuarios nuevoUsuario = new Usuarios(user);
                sesion.setAttribute("usuario", nuevoUsuario);
                sesion.setAttribute("nombreUsuario", nuevoUsuario.getNombre());
            }
            // Recuperar datos del alumno guardados y volver al alta
            siguientePag = PG_ALTA;
        }

        getServletContext().getRequestDispatcher(siguientePag)
                .forward(req, resp);
    }

    private String validarUsuario(String user, String pass) {
        // usuario/contraseña
        if ("samu".equals(user) && "samu".equals(pass)) return user;
        return null;
    }
}
