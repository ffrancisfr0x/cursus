package com.cursus.service;

import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.model.Empresa;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmpresaService {
    private final ConexionBD conexion;
    private final EmpresaDAO empresaDAO;
    private final OfertaDAO ofertaDAO;
    private final EntrevistaDAO entrevistaDAO;

    public EmpresaService(ConexionBD conexion, EmpresaDAO empresaDAO, OfertaDAO ofertaDAO, EntrevistaDAO
            entrevistaDAO) {
        this.conexion = conexion;
        this.empresaDAO = empresaDAO;
        this.ofertaDAO = ofertaDAO;
        this.entrevistaDAO = entrevistaDAO;
    }

    public List<Empresa> listar() throws SQLException {
        try (Connection con = conexion.obtener()) {
            return empresaDAO.listar(con);
        }
    }

    public void guardar(Empresa empresa) throws SQLException {
        empresa.setNombre(Validacion.limpiar(empresa.getNombre()));
        empresa.setSector(Validacion.limpiar(empresa.getSector()));
        empresa.setWeb(Validacion.limpiar(empresa.getWeb()));

        Validacion.obligatorio(empresa.getNombre(), "nombre", 100);
        Validacion.longitud(empresa.getSector(), "sector", 60);
        Validacion.url(empresa.getWeb(), "web");

        try (Connection con = conexion.obtener()) {

            Empresa otra = empresaDAO.buscarPorNombre(con, empresa.getNombre());

            if (otra != null && otra.getId() != empresa.getId()) {
                throw new IllegalArgumentException(
                        "Ya existe una empresa llamada " + otra.getNombre() + ".");
            }

            if (empresa.getId() == 0) {

                if (empresa.getFechaRegistro() == null) {
                    empresa.setFechaRegistro(LocalDate.now());
                }

                empresaDAO.insertar(con, empresa);

            } else if (empresaDAO.actualizar(con, empresa) == 0) {

                throw new IllegalArgumentException(
                        "No existe ninguna empresa con el ID " + empresa.getId() + ".");
            }
        }
    }

    public int borrar(int id) throws SQLException {
        try (Connection con = conexion.obtener()) {
            con.setAutoCommit(false);
            try {
                entrevistaDAO.borrarPorEmpresa(con, id);
                int ofertas = ofertaDAO.borrarPorEmpresa(con, id);

                if (empresaDAO.borrar(con, id) == 0) {
                    throw new IllegalArgumentException("No existe ninguna empresa con el ID " + id + ".");
                }

                con.commit();
                return ofertas;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
