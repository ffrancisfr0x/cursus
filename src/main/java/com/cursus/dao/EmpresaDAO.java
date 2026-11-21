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

public class EmpresaDAO {
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

    public Empresa buscarPorId(Connection con, int id) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

    public Empresa buscarPorNombre(Connection con, String nombre) throws SQLException {
        String sql = "SELECT * FROM empresa WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerFila(rs) : null;
            }
        }
    }

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

    public int borrar(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM empresa WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private Empresa leerFila(ResultSet rs) throws SQLException {
        return new Empresa(rs.getInt("id"), rs.getString("nombre"), rs.getString("sector"),
                rs.getString("web"), rs.getObject("fechaRegistro", LocalDate.class));
    }
}
