package es.dwes.dentista.servlet;

import es.dwes.dentista.dao.CitaDAO;
import es.dwes.dentista.dao.OdontologoDAO;
import es.dwes.dentista.dao.PacienteDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DateTimeException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/citas", "/citas/nueva", "/citas/eliminar"})
public class CitaServlet extends HttpServlet {
    private final CitaDAO citaDAO = new CitaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final OdontologoDAO odontologoDAO = new OdontologoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/citas/nueva".equals(request.getServletPath())) {
            mostrarFormulario(request, response, null);
            return;
        }

        boolean todas = "1".equals(request.getParameter("todas"));
        request.setAttribute("todas", todas);
        try {
            request.setAttribute("citas", citaDAO.listar(todas));
        } catch (SQLException e) {
            getServletContext().log("No se pudo cargar el listado de citas.", e);
            request.setAttribute("error", "No se ha podido obtener el listado de citas.");
        }
        request.getRequestDispatcher("/WEB-INF/vistas/citas/listado.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        switch (request.getServletPath()) {
            case "/citas/nueva" -> insertar(request, response);
            case "/citas/eliminar" -> eliminar(request, response);
            default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fecha = request.getParameter("fecha");
        String hora = request.getParameter("hora");
        String idPaciente = request.getParameter("id_paciente");
        String idOdontologo = request.getParameter("id_odontologo");

        if (vacio(fecha) || vacio(hora) || vacio(idPaciente) || vacio(idOdontologo)) {
            mostrarFormulario(request, response, "Todos los campos salvo el motivo son obligatorios.");
            return;
        }

        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);
            int paciente = Integer.parseInt(idPaciente);
            int odontologo = Integer.parseInt(idOdontologo);
            if (paciente < 1 || odontologo < 1) {
                mostrarFormulario(request, response, "Selecciona un paciente y un odontólogo válidos.");
                return;
            }
            citaDAO.insertar(fechaCita, horaCita, paciente, odontologo, request.getParameter("motivo"));
            response.sendRedirect(request.getContextPath() + "/citas?mensaje=creada");
        } catch (DateTimeException | NumberFormatException e) {
            mostrarFormulario(request, response, "La fecha, la hora o la selección no son válidas.");
        } catch (SQLException e) {
            getServletContext().log("No se pudo registrar la cita.", e);
            mostrarFormulario(request, response,
                    "No se ha podido registrar la cita. Comprueba los datos e inténtalo de nuevo.");
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id_cita"));
            if (id < 1) {
                throw new NumberFormatException("El identificador debe ser positivo.");
            }
            citaDAO.eliminar(id);
            response.sendRedirect(request.getContextPath() + "/citas?mensaje=eliminada");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/citas?error=accion");
        } catch (SQLException e) {
            getServletContext().log("No se pudo anular la cita.", e);
            response.sendRedirect(request.getContextPath() + "/citas?error=accion");
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
            String mensajeError) throws ServletException, IOException {
        try {
            request.setAttribute("pacientes", pacienteDAO.listarParaSelector());
            request.setAttribute("odontologos", odontologoDAO.listarTodos());
        } catch (SQLException e) {
            getServletContext().log("No se pudieron cargar los datos del formulario de cita.", e);
            mensajeError = "No se han podido cargar los pacientes y odontólogos.";
        }
        if (mensajeError != null) {
            request.setAttribute("error", mensajeError);
        }
        String pacienteSeleccionado = request.getParameter("id_paciente");
        if (pacienteSeleccionado == null) {
            pacienteSeleccionado = request.getParameter("paciente");
        }
        request.setAttribute("pacienteSeleccionado", pacienteSeleccionado);
        request.getRequestDispatcher("/WEB-INF/vistas/citas/formulario.jsp")
                .forward(request, response);
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
