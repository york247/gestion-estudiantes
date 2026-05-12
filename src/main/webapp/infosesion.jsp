<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Date" %>
<%@ page import="jakarta.servlet.ServletContext" %>
<%
    ServletContext ctx = getServletContext();
    Integer usuariosConectados = null;
    Integer usuariosValidados  = null;
    synchronized (ctx) {
        usuariosConectados = (Integer) ctx.getAttribute("usuariosConectados");
        usuariosValidados  = (Integer) ctx.getAttribute("usuariosValidados");
    }
%>
<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><title>Info de sesión</title></head>
<body>
<h2>Información de la sesión</h2>
<table border="1" cellpadding="6">
    <tr><td>Identificador</td>           <td><%= session.getId() %></td></tr>
    <tr><td>Fecha/hora creación</td>     <td><%= new Date(session.getCreationTime()) %></td></tr>
    <tr><td>Hora último acceso</td>      <td><%= new Date(session.getLastAccessedTime()) %></td></tr>
    <tr><td>Número de accesos previos</td><td><%= session.getAttribute("contadorAccesos") %></td></tr>
    <tr><td>Usuario</td>                 <td><%= session.getAttribute("nombreUsuario") != null
                                                 ? session.getAttribute("nombreUsuario") : "(no validado)" %></td></tr>
    <tr><td>Usuarios conectados</td>     <td><%= usuariosConectados %></td></tr>
    <tr><td>Usuarios validados</td>      <td><%= usuariosValidados %></td></tr>
</table>
<br><a href="index.html">Volver al inicio</a>
</body>
</html>