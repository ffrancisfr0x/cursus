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

/**
 * Servicio encargado de gestionar las entrevistas asociadas a las
 * candidaturas y aplicar las reglas de negocio correspondientes.
 */
public class EntrevistaService {

    private final ConexionBD conexion;
    private final EntrevistaDAO entrevistaDAO;
    private final OfertaDAO ofertaDAO;

    /**
     * Crea una instancia del servicio con las dependencias necesarias.
     *
     * @param conexion gestor de conexiones a la base de datos
     * @param entrevistaDAO acceso a datos de entrevistas
     * @param ofertaDAO acceso a datos de ofertas
     */
    public EntrevistaService(
            ConexionBD conexion,
            EntrevistaDAO entrevistaDAO,
            OfertaDAO ofertaDAO) {

        this.conexion = conexion;
        this.entrevistaDAO = entrevistaDAO;
        this.ofertaDAO = ofertaDAO;
    }

    /**
     * Obtiene todas las entrevistas asociadas a una oferta.
     *
     * @param idOferta identificador de la oferta
     * @return lista de entrevistas de la oferta
     * @throws SQLException si se produce un error de acceso a datos
     */
    public List<Entrevista> listarPorOferta(int idOferta)
            throws SQLException {

        try (Connection con = conexion.obtener()) {
            return entrevistaDAO.listarPorOferta(con, idOferta);
        }
    }

    /**
     * Inserta o actualiza una entrevista después de validar sus datos.
     * Cuando se crea una nueva entrevista para una candidatura pendiente,
     * el estado de la oferta pasa automáticamente a entrevista.
     *
     * @param entrevista entrevista a guardar
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si los datos no son válidos o la
     *                                  oferta asociada no existe
     */
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

    /**
     * Elimina una entrevista a partir de su identificador.
     *
     * @param id identificador de la entrevista
     * @throws SQLException si se produce un error de acceso a datos
     * @throws IllegalArgumentException si la entrevista no existe
     */
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

    /**
     * Comprueba que los datos de la entrevista sean válidos antes de
     * realizar operaciones de persistencia.
     *
     * @param entrevista entrevista a validar
     * @throws IllegalArgumentException si algún dato no es válido
     */
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