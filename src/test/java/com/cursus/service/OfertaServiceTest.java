package com.cursus.service;

import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias de {@link OfertaService}.
 * <p>
 * Antes de cada prueba se crea, en una carpeta temporal, un archivo con 3 candidaturas conocidas.
 * Así las pruebas no tocan los datos reales y todas empiezan desde el mismo punto.
 */
class OfertaServiceTest {

    /** Carpeta temporal que JUnit crea antes de cada prueba y borra al terminar. */
    @TempDir
    File carpeta;

    private OfertaService servicio;

    /**
     * Crea el archivo de prueba con 3 candidaturas y el servicio que trabaja sobre él.
     *
     * @throws IOException si no se puede escribir el archivo
     */
    @BeforeEach
    void preparar() throws IOException {
        File archivo = new File(carpeta, "candidaturas.txt");
        escribir(archivo, """
                id;titulo;empresa;estado;fechaPostulacion;enlace;notas
                1;Programador Java;Accenture;rechazado;21/07/2026;;
                2;Desarrollador Android;NTT Data;pendiente;28/07/2026;;
                3;Analista Programador;Accenture;entrevista;09/09/2026;;
                """);
        servicio = new OfertaService(new OfertaDAO(archivo));
    }

    /**
     * Escribe un texto en un archivo, para preparar los datos de las pruebas.
     *
     * @param archivo archivo donde escribir
     * @param texto   contenido del archivo
     * @throws IOException si no se puede escribir
     */
    private void escribir(File archivo, String texto) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            writer.write(texto);
        }
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
        File csv = new File(carpeta, "nuevas.csv");
        escribir(csv, """
                id;titulo;empresa;estado;fechaPostulacion;enlace;notas
                1;Becario;Capgemini;pendiente;01/10/2026;;
                9;Mal;Indra;inventado;03/10/2026;;
                """);
        List<String> errores = new ArrayList<>();
        assertEquals(1, servicio.importar(csv.getPath(), errores));
        assertEquals(1, errores.size());
        assertEquals(4, servicio.listar(null, null).size());
    }
}//