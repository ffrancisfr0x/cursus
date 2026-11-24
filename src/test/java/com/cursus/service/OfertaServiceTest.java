package com.cursus.service;

import com.cursus.dao.EmpresaDAO;
import com.cursus.dao.EntrevistaDAO;
import com.cursus.dao.OfertaDAO;
import com.cursus.db.ConexionBD;
import com.cursus.model.Empresa;
import com.cursus.model.Estado;
import com.cursus.model.FiltroOferta;
import com.cursus.model.Oferta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OfertaServiceTest {

    private Connection con;
    private OfertaDAO ofertaDAO;
    private EntrevistaDAO entrevistaDAO;
    private OfertaService servicio;

    @BeforeEach
    void preparar() throws SQLException {
        ConexionBD conexion = mock(ConexionBD.class);
        con = mock(Connection.class);

        when(conexion.obtener()).thenReturn(con);

        ofertaDAO = mock(OfertaDAO.class);
        entrevistaDAO = mock(EntrevistaDAO.class);

        servicio = new OfertaService(
                conexion,
                ofertaDAO,
                mock(EmpresaDAO.class),
                entrevistaDAO
        );
    }

    private Oferta ofertaValida() {
        Oferta oferta = new Oferta();

        oferta.setTitulo(" Desarrollador Java ");
        oferta.setEmpresa(
                new Empresa(
                        1,
                        "Indra",
                        null,
                        null,
                        LocalDate.now()
                )
        );

        oferta.setEstado(Estado.PENDIENTE);
        oferta.setFechaPostulacion(LocalDate.now());

        return oferta;
    }

    @Test
    void guardarOfertaNuevaLlamaAInsertarConElTituloLimpio() throws SQLException {
        Oferta oferta = ofertaValida();
        servicio.guardar(oferta);

        verify(ofertaDAO).insertar(con, oferta);
        assertEquals("Desarrollador Java", oferta.getTitulo());
    }

    @Test
    void guardarSinEmpresaLanzaErrorYNoTocaLaBaseDeDatos() throws SQLException {
        Oferta oferta = ofertaValida();
        oferta.setEmpresa(null);
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(oferta)
        );

        verify(ofertaDAO, never()).insertar(any(), any());
    }

    @Test
    void guardarConFechaFuturaLanzaError() {
        Oferta oferta = ofertaValida();
        oferta.setFechaPostulacion(
                LocalDate.now().plusDays(1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(oferta)
        );
    }

    @Test
    void guardarConSalarioNegativoLanzaError() {
        Oferta oferta = ofertaValida();
        oferta.setSalario(-100.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(oferta)
        );
    }

    @Test
    void guardarConEnlaceSinHttpLanzaError() {
        Oferta oferta = ofertaValida();
        oferta.setEnlace("www.indra.es");

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(oferta)
        );
    }

    @Test
    void buscarConSalarioMinimoMayorQueMaximoLanzaError() throws SQLException {
        FiltroOferta filtro = new FiltroOferta();

        filtro.setSalarioMinimo(30000.0);
        filtro.setSalarioMaximo(20000.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscar(filtro)
        );

        verify(ofertaDAO, never()).buscar(any(), any());
    }

    @Test
    void borrarOfertaInexistenteHaceRollback() throws SQLException {
        when(ofertaDAO.borrar(con, 99)).thenReturn(0);
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.borrar(99)
        );

        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    void borrarOfertaBorraAntesSusEntrevistasYConfirma() throws SQLException {
        when(ofertaDAO.borrar(con, 5)).thenReturn(1);

        servicio.borrar(5);

        verify(entrevistaDAO).borrarPorOferta(con, 5);
        verify(con).commit();
    }

    @Test
    void cambiarEstadoSinEstadoLanzaError() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.cambiarEstado(1, null)
        );
    }
}