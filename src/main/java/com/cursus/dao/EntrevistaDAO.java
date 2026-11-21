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

public class EntrevistaDAO {
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

    public int borrar(Connection con, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM entrevista WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public int borrarPorOferta(Connection con, int idOferta) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM entrevista WHERE id_oferta = ?")) {
            ps.setInt(1, idOferta);
            return ps.executeUpdate();
        }
    }

    public int borrarPorEmpresa(Connection con, int idEmpresa) throws SQLException {
        String sql = "DELETE FROM entrevista WHERE id_oferta IN (SELECT id FROM oferta WHERE id_empresa = ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            return ps.executeUpdate();
        }
    }

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
