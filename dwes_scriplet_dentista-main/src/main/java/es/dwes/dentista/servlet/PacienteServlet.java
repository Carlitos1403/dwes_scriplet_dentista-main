package es.dwes.dentista.servlet;

import es.dwes.dentista.dao.PacienteDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/pacientes", "/pacientes/nuevo", "/pacientes/eliminar"})
public class PacienteServlet extends HttpServlet {
    private final PacienteDAO pacienteDAO = new PacienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/pacientes/nuevo".equals(request.getServletPath())) {
            request.getRequestDispatcher("/WEB-INF/vistas/pacientes/formulario.jsp")
                    .forward(request, response);
            return;
        }

        try {
            request.setAttribute("pacientes", pacienteDAO.listarTodos());
        } catch (SQLException e) {
            getServletContext().log("No se pudo cargar el listado de pacientes.", e);
            request.setAttribute("error", "No se ha podido obtener el listado de pacientes.");
        }
        request.getRequestDispatcher("/WEB-INF/vistas/pacientes/listado.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        switch (request.getServletPath()) {
            case "/pacientes/nuevo" -> insertar(request, response);
            case "/pacientes/eliminar" -> eliminar(request, response);
            default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String dni = request.getParameter("dni");
        String fecha = request.getParameter("fecha_nacimiento");

        if (vacio(nombre) || vacio(apellidos) || vacio(dni)) {
            mostrarFormularioConError(request, response, "Nombre, apellidos y DNI son obligatorios.");
            return;
        }

        try {
            LocalDate fechaNacimiento = vacio(fecha) ? null : LocalDate.parse(fecha);
            pacienteDAO.insertar(nombre.trim(), apellidos.trim(), dni.trim(), fechaNacimiento,
                    request.getParameter("telefono"), request.getParameter("email"),
                    request.getParameter("direccion"), request.getParameter("alergias"));
            response.sendRedirect(request.getContextPath() + "/pacientes?mensaje=creado");
        } catch (DateTimeParseException e) {
            mostrarFormularioConError(request, response, "La fecha de nacimiento no es válida.");
        } catch (SQLException e) {
            getServletContext().log("No se pudo registrar el paciente.", e);
            mostrarFormularioConError(request, response,
                    "No se ha podido registrar el paciente. Comprueba los datos e inténtalo de nuevo.");
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id_paciente"));
            if (id < 1) {
                throw new NumberFormatException("El identificador debe ser positivo.");
            }
            pacienteDAO.eliminar(id);
            response.sendRedirect(request.getContextPath() + "/pacientes?mensaje=eliminado");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/pacientes?error=accion");
        } catch (SQLException e) {
            getServletContext().log("No se pudo eliminar el paciente.", e);
            response.sendRedirect(request.getContextPath() + "/pacientes?error=accion");
        }
    }

    private void mostrarFormularioConError(HttpServletRequest request, HttpServletResponse response,
            String mensaje) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/WEB-INF/vistas/pacientes/formulario.jsp")
                .forward(request, response);
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
