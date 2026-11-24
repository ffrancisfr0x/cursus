package com.cursus.dao;

import com.cursus.model.Entrevista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar las operaciones CRUD de la entidad Entrevista.
 */
public class EntrevistaDAO {

    /**
     * Obtiene todas las entrevistas asociadas a una oferta de empleo.
     *
     * @param con conexión activa a la base de datos
     * @param idOferta identificador de la oferta
     * @return lista de entrevistas ordenadas por fecha e identificador
     * @throws SQLException si se produce un error durante la consulta
     */
    public List<Entrevista> listarPorOferta(Connection con, int idOferta) throws SQLException {
        List<Entrevista> entrevistas = new ArrayList<>();
        String sql = "SELECT * FROM entrevista WHERE id_oferta = ? ORDER BY fecha, id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOferta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entrevistas.add(leerFila(rs));
                }
            }
        }

        return entrevistas;
    }

    /**
     * Inserta una nueva entrevista en la base de datos y asigna el
     * identificador generado automáticamente al objeto recibido.
     *
     * @param con conexión activa a la base de datos
     * @param entrevista entrevista que se desea insertar
     * @throws SQLException si se produce un error durante la inserción
     */
    public void insertar(Connection con, Entrevista entrevista) throws SQLException {
        String sql = "INSERT INTO entrevista (id_oferta, fecha, tipo, notas, resultado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entrevista.getIdOferta());
            ps.setObject(2, entrevista.getFecha());
            ps.setString(3, entrevista.getTipo());
            ps.setString(4, entrevista.getNotas());
            ps.setString(5, entrevista.getResultado());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entrevista.setId(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza los datos de una entrevista existente.
     *
     * @param con conexión activa a la base de datos
     * @param entrevista entrevista con los datos actualizados
     * @return número de filas modificadas
     * @throws SQLException si se produce un error durante la actualización
     */
    public int actualizar(Connection con, Entrevista entrevista) throws SQLException {
        String sql = "UPDATE entrevista SET fecha = ?, tipo = ?, notas = ?, resultado = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, entrevista.getFecha());
            ps.setString(2, entrevista.getTipo());
            ps.setString(3, entrevista.getNotas());
            ps.setString(4, entrevista.getResultado());
            ps.setInt(5, entrevista.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Elimina una entrevista a partir de su identificador.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la entrevista
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrar(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM entrevista WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina todas las entrevistas asociadas a una oferta.
     *
     * @param con conexión activa a la base de datos
     * @param idOferta identificador de la oferta
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrarPorOferta(Connection con, int idOferta) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM entrevista WHERE id_oferta = ?")) {
            ps.setInt(1, idOferta);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina todas las entrevistas asociadas a las ofertas de una empresa.
     *
     * @param con conexión activa a la base de datos
     * @param idEmpresa identificador de la empresa
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrarPorEmpresa(Connection con, int idEmpresa) throws SQLException {
        String sql = "DELETE FROM entrevista WHERE id_oferta IN (SELECT id FROM oferta WHERE id_empresa = ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            return ps.executeUpdate();
        }
    }

    /**
     * Construye un objeto Entrevista a partir de la fila actual del ResultSet.
     *
     * @param rs resultado de una consulta posicionado sobre una fila válida
     * @return objeto Entrevista con los datos de la fila
     * @throws SQLException si se produce un error al leer los datos
     */
    private Entrevista leerFila(ResultSet rs) throws SQLException {
        Entrevista entrevista = new Entrevista();
        entrevista.setId(rs.getInt("id"));
        entrevista.setIdOferta(rs.getInt("id_oferta"));
        entrevista.setFecha(rs.getObject("fecha", LocalDate.class));
        entrevista.setTipo(rs.getString("tipo"));
        entrevista.setNotas(rs.getString("notas"));
        entrevista.setResultado(rs.getString("resultado"));
        return entrevista;
    }
}