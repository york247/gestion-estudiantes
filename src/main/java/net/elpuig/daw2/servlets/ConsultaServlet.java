package net.elpuig.daw2.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.elpuig.daw2.javabeans.Alumno;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsultaServlet extends HttpServlet {
    private static List<Alumno> alumnos = new ArrayList<>();

    //conexion pgadmin
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gestion-estudiantes?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "";

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

        String sqlParam = req.getParameter("sql");

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

        if (sqlParam != null)
            out.println("<p>Sentencia SQL introducida: " + sqlParam + "</p>");

        out.println("<br>id&nbsp;&nbsp;curso&nbsp;&nbsp;nombre<br>");

        String sql = "SELECT id, curso, nombre FROM alumnos";

        try (Connection cn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String curso = rs.getString("curso");
                String nombre = rs.getString("nombre");

                out.println(id + " " + curso + " " + nombre + "<br>");
            }

        } catch (SQLException e) {
            out.println("<p style='color:red'>Error JDBC: " + e.getMessage() + "</p>");
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

        String sql = "INSERT INTO alumnos (id, curso, nombre) VALUES (?, ?, ?)";

        try (Connection cn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, curso);
            ps.setString(3, nombre);

            int filas = ps.executeUpdate();
            out.println("Filas afectadas: " + filas);

        } catch (SQLException e) {
            out.println("<p style='color:red'>Error JDBC: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }
}
