package com.cursus.dao;

import com.cursus.model.Empresa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar las operaciones CRUD de la entidad Empresa.
 */
public class EmpresaDAO {

    /**
     * Obtiene todas las empresas registradas ordenadas por nombre.
     *
     * @param con conexión activa a la base de datos
     * @return lista de empresas ordenadas por nombre
     * @throws SQLException si se produce un error durante la consulta
     */
    public List<Empresa> listar(Connection con) throws SQLException {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa ORDER BY nombre";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empresas.add(leerFila(rs));
            }
        }

        return empresas;
    }

    /**
     * Busca una empresa por su identificador.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la empresa
     * @return la empresa encontrada o null si no existe
     * @throws SQLException si se produce un error durante la consulta
     */
    public Empresa buscarPorId(Connection con, int id) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    /**
     * Busca una empresa por su nombre ignorando mayúsculas y minúsculas.
     *
     * @param con conexión activa a la base de datos
     * @param nombre nombre de la empresa
     * @return la empresa encontrada o null si no existe
     * @throws SQLException si se produce un error durante la consulta
     */
    public Empresa buscarPorNombre(Connection con, String nombre) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE LOWER(nombre) = LOWER(?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    /**
     * Inserta una nueva empresa en la base de datos y actualiza su id
     * con la clave generada automáticamente.
     *
     * @param con conexión activa a la base de datos
     * @param empresa empresa que se desea insertar
     * @throws SQLException si se produce un error durante la inserción
     */
    public void insertar(Connection con, Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresa (nombre, sector, web, fechaRegistro) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getSector());
            ps.setString(3, empresa.getWeb());
            ps.setObject(4, empresa.getFechaRegistro());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    empresa.setId(claves.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza los datos de una empresa existente.
     *
     * @param con conexión activa a la base de datos
     * @param empresa empresa con los datos actualizados
     * @return número de filas afectadas
     * @throws SQLException si se produce un error durante la actualización
     */
    public int actualizar(Connection con, Empresa empresa) throws SQLException {
        String sql = "UPDATE empresa SET nombre = ?, sector = ?, web = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getSector());
            ps.setString(3, empresa.getWeb());
            ps.setInt(4, empresa.getId());

            return ps.executeUpdate();
        }
    }

    /**
     * Elimina una empresa de la base de datos según su identificador.
     *
     * @param con conexión activa a la base de datos
     * @param id identificador de la empresa
     * @return número de filas eliminadas
     * @throws SQLException si se produce un error durante la eliminación
     */
    public int borrar(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM empresa WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    /**
     * Construye un objeto Empresa a partir de la fila actual del ResultSet.
     *
     * @param rs resultado de una consulta posicionado sobre una fila válida
     * @return objeto Empresa con los datos de la fila
     * @throws SQLException si se produce un error al leer los datos
     */
    private Empresa leerFila(ResultSet rs) throws SQLException {
        return new Empresa(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("sector"),
                rs.getString("web"),
                rs.getObject("fechaRegistro", LocalDate.class)
        );
    }
}