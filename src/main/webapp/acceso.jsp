<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><title>Acceso</title></head>
<body>
<h2>Identificación requerida</h2>
<p>Para dar de alta un alumno debes identificarte:</p>
<form action="Login" method="post">
    <label>Usuario: <input type="text" name="txtUsuario"></label><br><br>
    <label>Contraseña: <input type="password" name="txtContrasenya"></label><br><br>
    <input type="submit" value="Aceptar">
</form>
</body>
</html>