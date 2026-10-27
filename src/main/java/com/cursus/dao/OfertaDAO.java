package com.cursus.dao;

import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso al archivo de texto donde se guardan las candidaturas.
 * <p>
 * Es la única clase que lee y escribe el archivo. Cada línea es una candidatura
 * con sus campos separados por punto y coma:
 * {@code id;titulo;empresa;estado;fechaPostulacion;enlace;notas}
 */
public class OfertaDAO {

    /** Formato de las fechas (dd/MM/uuuu). El modo estricto rechaza fechas imposibles como el 31/02. */
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Primera línea del archivo, con el nombre de cada campo. */
    private static final String CABECERA = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";

    /** Archivo de datos, por ejemplo data/candidaturas.txt. */
    private final File archivo;

    /**
     * Crea el DAO sobre un archivo de datos.
     *
     * @param archivo archivo donde se leen y guardan las candidaturas
     */
    public OfertaDAO(File archivo) {
        this.archivo = archivo;
    }

    /**
     * Lee todas las candidaturas del archivo.
     * Se salta la cabecera y las líneas vacías. Si el archivo todavía no existe, devuelve una lista vacía.
     *
     * @return lista con todas las candidaturas del archivo
     * @throws IOException si el archivo no se puede leer
     * @throws IllegalArgumentException si alguna línea tiene datos no válidos
     */
    public List<Oferta> leer() throws IOException {
        List<Oferta> ofertas = new ArrayList<>();
        if (!archivo.exists()) {
            return ofertas;
        }
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!esCabeceraOVacia(linea)) {
                    ofertas.add(convertirLinea(linea));
                }
            }
        }
        return ofertas;
    }

    /**
     * Sobrescribe el archivo con las candidaturas indicadas: primero la cabecera y después una candidatura por línea.
     * Si la carpeta del archivo no existe, la crea.
     *
     * @param ofertas candidaturas que debe contener el archivo
     * @throws IOException si el archivo no se puede escribir
     */
    public void guardar(List<Oferta> ofertas) throws IOException {
        File carpeta = archivo.getParentFile();
        if (carpeta != null) {
            carpeta.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            writer.write(CABECERA);
            writer.newLine();

            for (Oferta oferta : ofertas) {
                writer.write(oferta.getId() + ";" + oferta.getTitulo() + ";" + oferta.getEmpresa() + ";" + oferta.getEstado() + ";"
                        + oferta.getFechaPostulacion().format(FORMATO_FECHA) + ";" + oferta.getEnlace() + ";" + oferta.getNotas());
                writer.newLine();
            }
        }
    }

    /**
     * Convierte una línea de texto del archivo o del CSV en una candidatura.
     *
     * @param linea línea con los 7 campos separados por punto y coma
     * @return la candidatura con los datos de la línea
     * @throws IllegalArgumentException si faltan campos o algún dato no es válido (ID, estado o fecha)
     */
    public static Oferta convertirLinea(String linea) {
        String[] partes = linea.split(";", -1);

        if (partes.length < 7) {
            throw new IllegalArgumentException("Faltan campos en la línea: " + linea);
        }

        int id = convertirId(partes[0]);
        String titulo = partes[1];
        String empresa = partes[2];
        Estado estado = Estado.desdeTexto(partes[3]);
        LocalDate fechaPostulacion = convertirFecha(partes[4]);
        String enlace = partes[5];
        String notas = partes[6];

        return new Oferta(id, titulo, empresa, estado, fechaPostulacion, enlace, notas);
    }

    /**
     * Indica si una línea es la cabecera o está vacía, para saltarla al leer.
     *
     * @param linea línea leída del archivo
     * @return {@code true} si la línea está vacía o empieza por "id;"
     */
    public static boolean esCabeceraOVacia(String linea) {
        return linea.isBlank() || linea.toLowerCase().startsWith("id;");
    }

    /**
     * Convierte un texto en un ID numérico.
     *
     * @param texto texto con el ID, por ejemplo "13"
     * @return el ID como número entero
     * @throws IllegalArgumentException si el texto no es un número
     */
    public static int convertirId(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número: " + texto.trim(), e);
        }
    }

    /**
     * Convierte un texto con formato dd/MM/yyyy en una fecha.
     *
     * @param texto fecha como texto, por ejemplo "15/10/2026"
     * @return la fecha
     * @throws IllegalArgumentException si el formato no es correcto o la fecha no existe
     */
    public static LocalDate convertirFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato dd/MM/yyyy: " + texto.trim(), e);
        }
    }
}