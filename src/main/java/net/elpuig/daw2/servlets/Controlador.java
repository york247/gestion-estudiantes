package net.elpuig.daw2.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class Controlador extends HttpServlet {

    private static final String PG_INFO_SESION      = "/infosesion.jsp";
    private static final String PG_DESCONECTADO     = "/desconectado.jsp";
    private static final String PG_ACCESO           = "/acceso.jsp";
    private static final String PG_ALTA_OK          = "/altaok.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession(true);
        incrementarContadorSesion(sesion);

        String operacion = req.getParameter("operacion");
        if (operacion == null) operacion = "";

        String siguientePag = "/index.html";

        switch (operacion) {

            case "info":
                siguientePag = PG_INFO_SESION;
                break;

            case "desconectar":
                sesion.invalidate();
                siguientePag = PG_DESCONECTADO;
                break;

            case "alta":
                String id     = req.getParameter("txtID");
                String curso  = req.getParameter("txtCurso");
                String nombre = req.getParameter("txtNombre");

                net.elpuig.daw2.javabeans.Usuarios usuarioSesion =
                        (net.elpuig.daw2.javabeans.Usuarios) sesion.getAttribute("usuario");

                if (usuarioSesion != null) {
                    // Usuario validado → hace el alta en database
                    try (java.sql.Connection cn = ConsultaServlet.getConexion();
                         java.sql.PreparedStatement ps = cn.prepareStatement(
                                 "INSERT INTO alumnos (id, curso, nombre) VALUES (?,?,?)")) {

                        ps.setInt(1, Integer.parseInt(id));
                        ps.setString(2, curso);
                        ps.setString(3, nombre);
                        ps.executeUpdate();

                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                    }
                    siguientePag = PG_ALTA_OK;

                } else {
                    // Si no validado → guardar datos en sesión y pedir login
                    sesion.setAttribute("sesAlumnoId",     id);
                    sesion.setAttribute("sesAlumnoCurso",  curso);
                    sesion.setAttribute("sesAlumnoNombre", nombre);
                    siguientePag = PG_ACCESO;
                }
                break;

            default:
                siguientePag = "/index.html";
        }

        getServletContext().getRequestDispatcher(siguientePag)
                .forward(req, resp);
    }

    private void incrementarContadorSesion(HttpSession sesion) {
        Integer contador = 0;
        if (!sesion.isNew()) {
            Integer actual = (Integer) sesion.getAttribute("contadorAccesos");
            if (actual != null) contador = actual + 1;
        }
        sesion.setAttribute("contadorAccesos", contador);
    }
}
