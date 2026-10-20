<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <title>Clínica Dental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos.css">
  </head>
  <body>
    <h1>Gestión de citas de la clínica dental</h1>
    <p>Seleccione una opción:</p>
    <ul>
      <li><a href="${pageContext.request.contextPath}/pacientes">Ver listado de pacientes</a></li>
      <li><a href="${pageContext.request.contextPath}/citas">Ver próximas citas</a></li>
      <li><a href="${pageContext.request.contextPath}/citas/nueva">Nueva cita</a></li>
      <li><a href="${pageContext.request.contextPath}/pacientes/nuevo">Nuevo paciente</a></li>
    </ul>
  </body>
</html>
