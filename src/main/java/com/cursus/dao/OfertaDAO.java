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

public class OfertaDAO {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final String CABECERA = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";

    private final File archivo;

    public OfertaDAO(File archivo) {
        this.archivo = archivo;
    }

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

    public static boolean esCabeceraOVacia(String linea) {
        return linea.isBlank() || linea.toLowerCase().startsWith("id;");
    }

    public static int convertirId(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número: " + texto.trim(), e);
        }
    }

    public static LocalDate convertirFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato dd/MM/yyyy: " + texto.trim(), e);
        }
    }
}
