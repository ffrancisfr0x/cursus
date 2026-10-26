package com.cursus.dao;

import com.cursus.model.Oferta;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class OfertaDAO {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.STRICT);
    private static final String CABECERA = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";

    private final Path archivo;

    public OfertaDAO(Path archivo) {
        this.archivo = archivo;
    }

    public List<Oferta> leer() {
        List<Oferta> ofertas = new ArrayList<>();
        if (!Files.exists(this.archivo)) {
            return ofertas;
        }
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {

        }
        return ofertas;
    }

    public static boolean esCabeceraOVacia(String linea) {
        return linea.isBlank() || linea.toLowerCase().startsWith("id;");
    }

    public static int convertirId(String linea) {
        try {
            return Integer.parseInt(linea.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("\"El ID debe ser un número: " + linea.trim(), e);
        }
    }
}
