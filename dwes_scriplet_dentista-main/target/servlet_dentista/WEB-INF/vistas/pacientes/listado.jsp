<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <title>Listado de pacientes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilos.css">
  </head>
  <body>
    <h1>Listado de pacientes</h1>
    <c:if test="${param.mensaje == 'creado'}">
      <p class="nota">Paciente dado de alta.</p>
    </c:if>
    <c:if test="${param.mensaje == 'eliminado'}">
      <p class="nota">Paciente eliminado. Las citas asociadas también se han eliminado.</p>
    </c:if>
    <c:if test="${param.error == 'accion'}">
      <p class="error">No se ha podido eliminar el paciente.</p>
    </c:if>
    <c:if test="${not empty error}">
      <p class="error"><c:out value="${error}"/></p>
    </c:if>
    <table>
      <thead>
        <tr>
          <th>Id</th><th>Nombre</th><th>Apellidos</th><th>DNI</th>
          <th>Fecha nacimiento</th><th>Teléfono</th><th>Email</th>
          <th>Dirección</th><th>Alergias</th><th>Citas</th><th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="paciente" items="${pacientes}">
          <tr>
            <td><c:out value="${paciente.id}"/></td>
            <td><c:out value="${paciente.nombre}"/></td>
            <td><c:out value="${paciente.apellidos}"/></td>
            <td><c:out value="${paciente.dni}"/></td>
            <td><c:out value="${empty paciente.fechaNacimiento ? '-' : paciente.fechaNacimiento}"/></td>
            <td><c:out value="${empty paciente.telefono ? '-' : paciente.telefono}"/></td>
            <td><c:out value="${empty paciente.email ? '-' : paciente.email}"/></td>
            <td><c:out value="${empty paciente.direccion ? '-' : paciente.direccion}"/></td>
            <td><c:out value="${empty paciente.alergias ? '-' : paciente.alergias}"/></td>
            <td><c:out value="${paciente.numeroCitas}"/></td>
            <td>
              <a href="${pageContext.request.contextPath}/citas/nueva?paciente=${paciente.id}">Nueva cita</a>
              <form method="post" action="${pageContext.request.contextPath}/pacientes/eliminar">
                <input type="hidden" name="id_paciente" value="<c:out value='${paciente.id}'/>">
                <input type="submit" value="borrar">
              </form>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
    <p>
      <a href="${pageContext.request.contextPath}/pacientes/nuevo">Nuevo paciente</a> |
      <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>
  </body>
</html>
