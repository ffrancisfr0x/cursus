package com.cursus.service;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class Validacion {
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Validacion() {
    }

    public static String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    public static void obligatorio(String texto, String campo, int maximo) {
        if (texto == null) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        longitud(texto, campo, maximo);
    }

    public static void longitud(String texto, String campo, int maximo) {
        if (texto != null && texto.length() > maximo) {
            throw new IllegalArgumentException("El campo " + campo + " no puede tener más de " + maximo + " caracteres.");
        }
    }

    public static void url(String url, String campo) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("El campo " + campo + " debe empezar por http:// o https://");
        }
        longitud(url, campo, 255);
    }
}
