package com.cursus.service;

import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class OfertaServiceTest {
    @TempDir
    Path carpeta;
    private OfertaService servicio;
    @BeforeEach
    void preparar() throws IOException {
        Path archivo = carpeta.resolve("candidaturas.txt");
        Files.writeString(archivo, """
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
1;Programador Java;Accenture;rechazado;21/07/2026;;
2;Desarrollador Android;NTT Data;pendiente;28/07/2026;;
3;Analista Programador;Accenture;entrevista;09/09/2026;;
""");
        servicio = new OfertaService(new OfertaDAO(archivo.toFile()));
    }
    @Test
    void listarSinFiltrosDevuelveTodas() throws IOException {
        assertEquals(3, servicio.listar(null, null).size());
    }
    @Test
    void listarFiltraPorEstado() throws IOException {
        assertEquals(1, servicio.listar(Estado.PENDIENTE, null).size());
    }
    @Test
    void listarFiltraPorEmpresaSinDistinguirMayusculas() throws IOException {
        assertEquals(2, servicio.listar(null, "accen").size());
    }
    @Test
    void anadirAsignaElSiguienteId() throws IOException {
        Oferta nueva = servicio.anadir("QA Tester", "Indra", "pendiente", "15/10/2026", null, null);
        assertEquals(4, nueva.getId());
    }
    @Test
    void anadirSinFechaUsaLaDeHoy() throws IOException {
        Oferta nueva = servicio.anadir("Dev Java", "Indra", "pendiente", null, null, null);
        assertEquals(LocalDate.now(), nueva.getFechaPostulacion());
    }
    @Test
    void anadirRechazaFechaImposible() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.anadir("QA", "Indra", "pendiente", "31/02/2026", null, null));
    }
    @Test
    void verIdInexistenteLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> servicio.ver(99));
    }
    @Test
    void borrarEliminaLaCandidatura() throws IOException {
        servicio.borrar(2);
        assertEquals(2, servicio.listar(null, null).size());
    }
    @Test
    void importarSaltaLasLineasConErrores() throws IOException {
        Path csv = carpeta.resolve("nuevas.csv");
        Files.writeString(csv, """
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
1;Becario;Capgemini;pendiente;01/10/2026;;
9;Mal;Indra;inventado;03/10/2026;;
""");
        List<String> errores = new ArrayList<>();
        assertEquals(1, servicio.importar(csv.toString(), errores));
        assertEquals(1, errores.size());
        assertEquals(4, servicio.listar(null, null).size());
    }
}
