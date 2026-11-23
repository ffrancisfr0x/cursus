package com.cursus.service;

import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.model.Entrevista;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EntrevistaService {

    private final ConexionBD conexion;
    private final EntrevistaDAO entrevistaDAO;
    private final OfertaDAO ofertaDAO;

    public EntrevistaService(
            ConexionBD conexion,
            EntrevistaDAO entrevistaDAO,
            OfertaDAO ofertaDAO) {

        this.conexion = conexion;
        this.entrevistaDAO = entrevistaDAO;
        this.ofertaDAO = ofertaDAO;
    }

    public List<Entrevista> listarPorOferta(int idOferta)
            throws SQLException {

        try (Connection con = conexion.obtener()) {
            return entrevistaDAO.listarPorOferta(con, idOferta);
        }
    }

    public void guardar(Entrevista entrevista)
            throws SQLException {

        validar(entrevista);

        try (Connection con = conexion.obtener()) {

            if (entrevista.getId() != 0) {

                if (entrevistaDAO.actualizar(con, entrevista) == 0) {
                    throw new IllegalArgumentException(
                            "No existe ninguna entrevista con el ID "
                                    + entrevista.getId() + ".");
                }

                return;
            }

            con.setAutoCommit(false);

            try {

                Oferta oferta =
                        ofertaDAO.buscarPorId(con, entrevista.getIdOferta());

                if (oferta == null) {
                    throw new IllegalArgumentException(
                            "No existe la candidatura de esta entrevista.");
                }

                entrevistaDAO.insertar(con, entrevista);

                if (oferta.getEstado() == Estado.PENDIENTE) {
                    ofertaDAO.cambiarEstado(
                            con,
                            oferta.getId(),
                            Estado.ENTREVISTA);
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }

    public void borrar(int id)
            throws SQLException {

        try (Connection con = conexion.obtener()) {

            if (entrevistaDAO.borrar(con, id) == 0) {
                throw new IllegalArgumentException(
                        "No existe ninguna entrevista con el ID "
                                + id + ".");
            }
        }
    }

    private void validar(Entrevista entrevista) {

        entrevista.setNotas(
                Validacion.limpiar(entrevista.getNotas()));

        if (entrevista.getFecha() == null) {
            throw new IllegalArgumentException(
                    "La fecha de la entrevista es obligatoria.");
        }

        if (!Entrevista.TIPOS.contains(
                entrevista.getTipo())) {

            throw new IllegalArgumentException(
                    "Elige un tipo de entrevista.");
        }

        if (!Entrevista.RESULTADOS.contains(
                entrevista.getResultado())) {

            throw new IllegalArgumentException(
                    "Elige un resultado.");
        }
    }
}