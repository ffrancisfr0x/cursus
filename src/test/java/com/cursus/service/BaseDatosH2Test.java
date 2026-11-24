package com.cursus.service;

import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.model.Empresa;
import com.cursus.model.Entrevista;
import com.cursus.model.Estado;
import com.cursus.model.FiltroOferta;
import com.cursus.model.Oferta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseDatosH2Test {

    private ConexionBD conexion;

    private OfertaService ofertas;
    private EmpresaService empresas;
    private EntrevistaService entrevistas;

    @BeforeEach
    void preparar() throws Exception {

        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

        conexion = new ConexionBD(url, "sa", "");
        crearEsquema();

        EmpresaDAO empresaDAO = new EmpresaDAO();
        OfertaDAO ofertaDAO = new OfertaDAO();
        EntrevistaDAO entrevistaDAO = new EntrevistaDAO();

        ofertas = new OfertaService(
                conexion,
                ofertaDAO,
                empresaDAO,
                entrevistaDAO
        );

        empresas = new EmpresaService(
                conexion,
                empresaDAO,
                ofertaDAO,
                entrevistaDAO
        );

        entrevistas = new EntrevistaService(
                conexion,
                entrevistaDAO,
                ofertaDAO
        );

        ofertas.importar(
                new File("data/candidaturas-prueba.csv"),
                new ArrayList<>()
        );
    }

    private void crearEsquema()
            throws IOException, SQLException {

        String script = Files.readString(Path.of("sql/schema.sql"), StandardCharsets.UTF_8).replaceAll("(?m)^--.*$", "");

        try (Connection con = conexion.obtener(); Statement st = con.createStatement()) {
            for (String sentencia : script.split(";")) {

                String limpia = sentencia.trim();
                String mayusculas = limpia.toUpperCase();

                if (!limpia.isEmpty()
                        && !mayusculas.startsWith("CREATE DATABASE")
                        && !mayusculas.startsWith("USE")
                        && !mayusculas.startsWith("SET NAMES")) {

                    st.execute(limpia);
                }
            }
        }
    }

    @Test
    void importarCreaLasEmpresasYLasOfertas() throws SQLException {
        assertEquals(15, ofertas.buscar(new FiltroOferta()).size());
        assertEquals(6, empresas.listar().size());
    }

    @Test
    void filtroCombinadoEmpresaYEstadoUsaElJoin() throws SQLException {
        FiltroOferta filtro = new FiltroOferta();

        filtro.setEmpresa("indra");
        filtro.setEstado(Estado.ENTREVISTA);

        List<Oferta> resultado = ofertas.buscar(filtro);

        assertEquals(2, resultado.size());

        assertEquals("Indra", resultado.get(0).getEmpresa().getNombre());
    }

    @Test
    void filtroPorSalarioYFecha() throws SQLException {

        Oferta oferta = ofertas.buscar(new FiltroOferta()).get(0);

        oferta.setSalario(30000.0);

        ofertas.guardar(oferta);

        FiltroOferta filtro = new FiltroOferta();

        filtro.setSalarioMinimo(25000.0);
        filtro.setFechaDesde(oferta.getFechaPostulacion());

        assertEquals(1, ofertas.buscar(filtro).size());
    }

    @Test
    void anadirEntrevistaAUnaOfertaPendienteLaPasaAEntrevista() throws SQLException {
        FiltroOferta pendientes = new FiltroOferta();
        pendientes.setEstado(Estado.PENDIENTE);

        Oferta oferta = ofertas.buscar(pendientes).get(0);

        Entrevista entrevista = new Entrevista();

        entrevista.setIdOferta(oferta.getId());
        entrevista.setFecha(LocalDate.now());

        entrevista.setTipo("técnica");
        entrevista.setResultado("pendiente");

        entrevistas.guardar(entrevista);

        assertEquals(Estado.ENTREVISTA, ofertas.ver(oferta.getId()).getEstado());

        assertEquals(1, entrevistas.listarPorOferta(oferta.getId()).size());
    }

    @Test
    void borrarEmpresaBorraSusOfertasYEntrevistas() throws SQLException {
        Empresa indra =
                empresas.listar()
                        .stream()
                        .filter(e -> e.getNombre().equals("Indra"))
                        .findFirst()
                        .orElseThrow();

        assertEquals(2, empresas.borrar(indra.getId()));
        assertEquals(13, ofertas.buscar(new FiltroOferta()).size());
        assertEquals(5, empresas.listar().size());
    }

    @Test
    void empresaConNombreRepetidoLanzaError() {

        Empresa repetida = new Empresa();
        repetida.setNombre("accenture");

        assertThrows(IllegalArgumentException.class,
                () -> empresas.guardar(repetida)
        );
    }

    @Test
    void siFallaLaBaseDeDatosAlImportarNoSeGuardaNada() throws Exception {
        OfertaDAO daoQueFalla =
                new OfertaDAO() {
                    private int insertadas;

                    @Override
                    public void insertar(Connection con, Oferta oferta) throws SQLException {

                        if (++insertadas == 3) {
                            throw new SQLException("Fallo simulado");
                        }

                        super.insertar(con, oferta);
                    }
                };

        OfertaService servicioQueFalla =
                new OfertaService(
                        conexion,
                        daoQueFalla,
                        new EmpresaDAO(),
                        new EntrevistaDAO()
                );

        File csv = File.createTempFile("cursus", ".csv");
        csv.deleteOnExit();

        Files.writeString(
                csv.toPath(),
                """
                id;titulo;empresa;estado;fechaPostulacion;enlace;notas
                1;Uno;Empresa Nueva A;pendiente;01/10/2026;;
                2;Dos;Empresa Nueva B;pendiente;02/10/2026;;
                3;Tres;Empresa Nueva C;pendiente;03/10/2026;;
                """,
                StandardCharsets.UTF_8
        );

        assertThrows(
                SQLException.class,
                () -> servicioQueFalla.importar(
                        csv,
                        new ArrayList<>()
                )
        );

        assertEquals(
                15, ofertas.buscar(new FiltroOferta()).size()
        );

        assertTrue(
                empresas.listar()
                        .stream()
                        .noneMatch(e -> e.getNombre().startsWith("Empresa Nueva"))
        );
    }
}