package net.elpuig.daw2.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.elpuig.daw2.javabeans.Alumno;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsultaServlet extends HttpServlet {
    private static List<Alumno> alumnos = new ArrayList<>();

    @Override
    public void init() {
        if (alumnos.isEmpty()) {
            alumnos.addAll(Arrays.asList(
                    new Alumno(1, "Java", "Samu"),
                    new Alumno(2, "Python", "Jose"),
                    new Alumno(3, "PHP", "Manuel")
            ));
        }
    }


    //PARTE1

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String sql = req.getParameter("sql");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><head>");
        out.println("<style>");
        out.println("body { background-color: #fff9a6; font-family: Arial; padding: 20px; }");
        out.println("h2 { color: #003366; }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<h2>Usa JDBC para recuperar registros de una tabla</h2>");
        out.println("<hr>");

        if (sql != null)
            out.println("<p>Sentencia SQL introducida: " + sql + "</p>");

        out.println("<br>id&nbsp;&nbsp;curso&nbsp;&nbsp;nombre<br>");

        // Esta vez! usamos la lista compartida con nuevos alumnos
        for (Alumno a : alumnos) {
            out.println(a.getId() + " " + a.getCurso() + " " + a.getNombre() + "<br>");
        }

        out.println("</body></html>");
    }



    //PARTE2

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));
        String curso = req.getParameter("curso");
        String nombre = req.getParameter("nombre");

        Alumno nuevo = new Alumno(id, curso, nombre);

        alumnos.add(nuevo);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><head>");
        out.println("<style>");
        out.println("body { background-color: #fff9a6; font-family: Arial; padding: 20px; }");
        out.println("h2 { color: #003366; }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<h2>Usa JDBC para grabar un registro en una tabla</h2>");
        out.println("<hr>");

        out.println("Filas afectadas: 1");

        out.println("</body></html>");
    }
}
