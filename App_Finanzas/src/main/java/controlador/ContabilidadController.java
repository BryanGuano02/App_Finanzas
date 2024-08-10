package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dto.MovimientoDTO;
import modelo.entidades.*;
import modelo.dao.*;

@WebServlet("/ContabilidadController")
public class ContabilidadController extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "verCuenta";

        switch (ruta) {
            case "verDashboard":
                verDashboard(req, resp);
                break;
            case "verCuenta":
                this.verCuenta(req, resp);
                break;
            case "filtrarPorFechas":
                this.filtrarPorFechas(req, resp);
                break;
            case "verMovimientos":
                this.verMovimientos(req, resp);
                break;
            case "registrarGasto":
                this.registrarGasto(req, resp);
                break;
            case "ingresarInfoGasto":
                this.ingresarInfoGasto(req, resp);
                break;
            case "verCategoria":
                verCategoria(req, resp);
                break;
        }
    }

    private void filtrarPorFechas(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LocalDate fechaInicio = LocalDate.parse(req.getParameter("fechaInicio"));
        LocalDate fechaActual = LocalDate.parse(req.getParameter("fechaFin"));

        HttpSession session = req.getSession();
        session.setAttribute("fechaInicio", fechaInicio);
        session.setAttribute("fechaActual", fechaActual);

        resp.sendRedirect("/ContabilidadController?ruta=verDashboard");
    }

    private void verDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        1. Obtener parámetros
        HttpSession session = req.getSession();

        LocalDate fechaInicio = null;
        LocalDate fechaActual = null;

        if (session.getAttribute("fechaInicio") == null && session.getAttribute("fechaActual") == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
            fechaActual = LocalDate.now().plusDays(1);

            session.setAttribute("fechaInicio", fechaInicio);
            session.setAttribute("fechaActual", fechaActual);
        }

//        2. Hablar con el modelo
        CuentaDAO cuentaDAO = new CuentaDAO();
        List<Cuenta> cuentas = cuentaDAO.obtenerTodo(); //TODO: El saldo de la cuenta es también en base a las fechas

        CategoriaEgresoDAO categoriaEgresoDAO = new CategoriaEgresoDAO();
        List<CategoriaEgreso> categoriasEgreso = categoriaEgresoDAO.obtenerTodo();

        req.setAttribute("cuentas", cuentas);
        req.setAttribute("categoriasEgreso", categoriasEgreso);

        //req.setAttribute("ingresos", ingresos);
        //TODO: Mandar los parámetros restantes  de mostrar()
        //		llamar a la vista - mostrar()
        req.getRequestDispatcher("jsp/VerDashboard.jsp").forward(req, resp);
    }

    private void verCuenta(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        1.
        int idCuenta = Integer.parseInt(req.getParameter("idCuenta"));

//        2.
        CuentaDAO cuentaDAO = new CuentaDAO();
        Cuenta cuenta = cuentaDAO.obtenerCuentaPorId(idCuenta);

        MovimientoDAO movimientoDao = new MovimientoDAO();
        List<MovimientoDTO> movimientos = movimientoDao.obtenerMovimientosPorIdCuenta(idCuenta);

        req.setAttribute("cuenta", cuenta);
        req.setAttribute("movimientos", movimientos);

//        3.
        req.getRequestDispatcher("jsp/VerCuenta.jsp").forward(req, resp);
    }

    private void verMovimientos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Obtener las fechas desde la solicitud
        LocalDate desde = LocalDate.parse(req.getParameter("desde"));
        LocalDate hasta = LocalDate.parse(req.getParameter("hasta"));

        // Crear una instancia del DAO para acceder a los movimientos
        MovimientoDAO movimientoDAO = new MovimientoDAO();

        // Llamar al método obtenerTodo del DAO para obtener los movimientos
        List<MovimientoDTO> movimientos = movimientoDAO.obtenerTodo(desde, hasta);

        // Establecer los movimientos en la solicitud para ser enviados a la vista
        req.setAttribute("movimientos", movimientos);

        // Llamar a la vista para mostrar los movimientos
        req.getRequestDispatcher("jsp/VerMovimientos.jsp").forward(req, resp);
    }

    private void verCategoria(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    private void eliminarMovimiento(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void confirmarEliminacion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void actualizarMovimiento(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void confirmarActualizacion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void registrarlnfoActualizacion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void registrarGasto(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        1.
        int idCuenta = Integer.parseInt(req.getParameter("idCuenta"));

//        2.
        CuentaDAO cuentaDao = new CuentaDAO();
        Cuenta cuenta = cuentaDao.obtenerCuentaPorId(idCuenta);

        CategoriaEgresoDAO categoriaEgresoDAO = new CategoriaEgresoDAO();
        List<CategoriaEgreso> categoriasEgresos = categoriaEgresoDAO.obtenerTodo();

        req.setAttribute("saldoCuenta", cuenta.getTotal());
        req.setAttribute("categoriasEgresos", categoriasEgresos);

        actualizarIdCuenta(req, idCuenta);

//        3.
        req.getRequestDispatcher("jsp/VerRegistrarGasto.jsp").forward(req, resp);
    }


    private void ingresarInfoGasto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        1.
        String concepto = String.valueOf(Integer.parseInt(req.getParameter("concepto")));
        LocalDate fecha = LocalDate.ofEpochDay(Integer.parseInt(req.getParameter("fecha")));
        double valor = Double.parseDouble(req.getParameter("valor"));
        int idCategoria = Integer.parseInt(req.getParameter("idCategoria"));

//        2.
        CategoriaEgresoDAO categoriaDao = new CategoriaEgresoDAO();
        CategoriaEgreso categoriaEgreso = categoriaDao.obtenerCategoriaPorId(idCategoria);

        Egreso egreso = new Egreso(12.2, 1, fecha, new Cuenta(1, "Billetera544", 192.1), new Cuenta(4, "Bajo del colchón", 15.24), concepto, categoriaEgreso);

        EgresoDAO egresoDao = new EgresoDAO();
        egresoDao.guardarEgreso(egreso);

        HttpSession session = req.getSession();
        int idCuenta = (int) session.getAttribute("idCuenta");

        CuentaDAO cuentaDao = new CuentaDAO();
        Cuenta cuenta = cuentaDao.obtenerCuentaPorId(idCuenta);


//        actualizar categoria
        cuentaDao.actualizarSaldo(idCuenta, valor);

//        actualizar cuenta


//        3.
        verCuenta(req, resp);
    }

    private void registrarlngreso(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    }

    private void ingresarlnfolngreso(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void registrarTransferencia(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void ingresarlnfoTransferencia(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    private void actualizarIdCuenta(HttpServletRequest req, int nuevoIdCuenta) {
        HttpSession session = req.getSession();
        Integer idCuentaExistente = (Integer) session.getAttribute("idCuenta");

        if (idCuentaExistente == null || idCuentaExistente != nuevoIdCuenta) {
            // Si el idCuenta no está en la sesión o es diferente, actualiza la sesión
            session.setAttribute("idCuenta", nuevoIdCuenta);
        }
    }

}
