package com.cursus.dao;

import com.cursus.model.Empresa;
import com.cursus.model.Estado;
import com.cursus.model.FiltroOferta;
import com.cursus.model.Oferta;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar las operaciones de acceso a datos de la
 * entidad Oferta, incluyendo búsquedas, inserciones, actualizaciones
 * y eliminaciones.
 */
public class OfertaDAO {

    private static final String SELECT_CON_EMPRESA = """

            SELECT o.*, e.nombre, e.sector, e.web, e.fechaRegistro
    FROM oferta o
    JOIN empresa e ON o.id_empresa = e.id
    """;

    /**
     * Busca ofertas aplicando los criterios indicados en el filtro.
     *
     * @param con conexión activa a la base de datos
     * @param filtro criterios de búsqueda de ofertas
     * @return lista de ofertas que cumplen los criterios especificados
     * @throws SQLException si se produce un error durante la consulta
     */
    public List<Oferta> buscar(Connection con, FiltroOferta filtro) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_CON_EMPRESA).append(" WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();

        if (filtro.getTitulo() != null) {
            sql.append(" AND LOWER(o.titulo) LIKE ?");
            parametros.add("%" + filtro.getTitulo().toLowerCase() + "%");
        }

        if (filtro.getEmpresa() != null) {
            sql.append(" AND LOWER(e.nombre) LIKE ?");
            parametros.add("%" + filtro.getEmpresa().toLowerCase() + "%");
        }

        if (filtro.getEstado() != null) {
            sql.append(" AND o.estado = ?");
            parametros.add(filtro.getEstado().toString());
        }

        if (filtro.getSalarioMinimo() != null) {
            sql.append(" AND o.salario >= ?");
            parametros.add(filtro.getSalarioMinimo());
        }

        if (filtro.getSalarioMaximo() != null) {
            sql.append(" AND o.salario <= ?");
            parametros.add(filtro.getSalarioMaximo());
        }

        if (filtro.getFechaDesde() != null) {
            sql.append(" AND o.fechaPostulacion >= ?");
            parametros.add(filtro.getFechaDesde());
        }

        if (filtro.getFechaHasta() != null) {
            sql.append(" AND o.fechaPostulacion <= ?");
            parametros.add(filtro.getFechaHasta());
        }

        sql.append(" ORDER BY o.fechaPostulacion DESC, o.id DESC");

        List<Oferta> ofertas = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ofertas.add(leerFila(rs));
                }
            }
        }

        return ofertas;
    }

    /**
     * Busca una oferta a partir de su identificador.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la oferta
     * @return oferta encontrada o null si no existe
     * @throws SQLException si se produce un error durante la consulta
     */
    public Oferta buscarPorId(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_CON_EMPRESA + " WHERE o.id = ?")) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    /**
     * Inserta una nueva oferta en la base de datos y asigna el
     * identificador generado automáticamente al objeto recibido.
     *
     * @param con conexión activa a la base de datos
     * @param oferta oferta que se desea insertar
     * @throws SQLException si se produce un error durante la inserción
     */
    public void insertar(Connection con, Oferta oferta) throws SQLException {
        String sql = """
        INSERT INTO oferta (id_empresa, titulo, descripcion, estado, fechaPostulacion, enlace, salario,
        notas)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            rellenarParametros(ps, oferta);
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    oferta.setId(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza los datos de una oferta existente.
     *
     * @param con conexión activa a la base de datos
     * @param oferta oferta con los datos actualizados
     * @return número de filas modificadas
     * @throws SQLException si se produce un error durante la actualización
     */
    public int actualizar(Connection con, Oferta oferta) throws SQLException {
        String sql = """
        UPDATE oferta SET id_empresa = ?, titulo = ?, descripcion = ?, estado = ?,
        fechaPostulacion = ?, enlace = ?, salario = ?, notas = ?
        WHERE id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            rellenarParametros(ps, oferta);
            ps.setInt(9, oferta.getId());
            return ps.executeUpdate();
        }
    }

    /**
     * Modifica el estado de una oferta.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la oferta
     * @param estado nuevo estado de la oferta
     * @return número de filas modificadas
     * @throws SQLException si se produce un error durante la actualización
     */
    public int cambiarEstado(Connection con, int id, Estado estado) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE oferta SET estado = ? WHERE id = ?")) {
            ps.setString(1, estado.toString());
            ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina una oferta a partir de su identificador.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la oferta
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrar(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM oferta WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina todas las ofertas asociadas a una empresa.
     *
     * @param con conexión activa a la base de datos
     * @param idEmpresa identificador de la empresa
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrarPorEmpresa(Connection con, int idEmpresa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM oferta WHERE id_empresa = ?")) {
            ps.setInt(1, idEmpresa);
            return ps.executeUpdate();
        }
    }

    /**
     * Rellena los parámetros de un PreparedStatement con los datos
     * de una oferta.
     *
     * @param ps sentencia preparada a completar
     * @param oferta oferta cuyos datos se utilizarán
     * @throws SQLException si se produce un error al asignar parámetros
     */
    private void rellenarParametros(PreparedStatement ps, Oferta oferta) throws SQLException {
        ps.setInt(1, oferta.getEmpresa().getId());
        ps.setString(2, oferta.getTitulo());
        ps.setString(3, oferta.getDescripcion());
        ps.setString(4, oferta.getEstado().toString());
        ps.setObject(5, oferta.getFechaPostulacion());
        ps.setString(6, oferta.getEnlace());

        if (oferta.getSalario() == null) {
            ps.setNull(7, Types.DECIMAL);
        } else {
            ps.setBigDecimal(7, BigDecimal.valueOf(oferta.getSalario()));
        }

        ps.setString(8, oferta.getNotas());
    }

    /**
     * Construye un objeto Oferta a partir de la fila actual
     * del ResultSet.
     *
     * @param rs resultado de una consulta posicionado sobre una fila válida
     * @return objeto Oferta con los datos recuperados
     * @throws SQLException si se produce un error al leer los datos
     */
    private Oferta leerFila(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa(
                rs.getInt("id_empresa"),
                rs.getString("nombre"),
                rs.getString("sector"),
                rs.getString("web"),
                rs.getObject("fechaRegistro", LocalDate.class)
        );

        Oferta oferta = new Oferta();

        oferta.setId(rs.getInt("id"));
        oferta.setEmpresa(empresa);
        oferta.setTitulo(rs.getString("titulo"));
        oferta.setDescripcion(rs.getString("descripcion"));
        oferta.setEstado(Estado.desdeTexto(rs.getString("estado")));
        oferta.setFechaPostulacion(rs.getObject("fechaPostulacion", LocalDate.class));
        oferta.setEnlace(rs.getString("enlace"));

        BigDecimal salario = rs.getBigDecimal("salario");

        oferta.setSalario(salario == null ? null : salario.doubleValue());
        oferta.setNotas(rs.getString("notas"));

        return oferta;
    }
}