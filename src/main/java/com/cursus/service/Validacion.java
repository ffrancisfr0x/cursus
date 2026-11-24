package com.cursus.service;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Clase de utilidades para realizar validaciones de datos y proporcionar
 * elementos comunes relacionados con la validación.
 */
public class Validacion {

    /**
     * Formato de fecha utilizado por la aplicación.
     * El patrón esperado es dd/MM/uuuu con validación estricta.
     */
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Evita la instanciación de esta clase de utilidades.
     */
    private Validacion() {
    }

    /**
     * Elimina espacios en blanco al inicio y al final de un texto.
     * Si el texto es nulo o está vacío, devuelve null.
     *
     * @param texto texto a limpiar
     * @return texto limpio o null si no contiene información
     */
    public static String limpiar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }

    /**
     * Comprueba que un campo de texto sea obligatorio y que no supere
     * la longitud máxima permitida.
     *
     * @param texto valor del campo
     * @param campo nombre del campo para los mensajes de error
     * @param maximo número máximo de caracteres permitidos
     * @throws IllegalArgumentException si el campo es obligatorio o supera
     *                                  la longitud máxima
     */
    public static void obligatorio(String texto, String campo, int maximo) {
        if (texto == null) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio.");
        }

        longitud(texto, campo, maximo);
    }

    /**
     * Comprueba que la longitud de un texto no supere el límite indicado.
     *
     * @param texto valor del campo
     * @param campo nombre del campo para los mensajes de error
     * @param maximo número máximo de caracteres permitidos
     * @throws IllegalArgumentException si el texto supera la longitud máxima
     */
    public static void longitud(String texto, String campo, int maximo) {
        if (texto != null && texto.length() > maximo) {
            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " no puede tener más de "
                            + maximo
                            + " caracteres.");
        }
    }

    /**
     * Comprueba que una URL tenga un formato válido para la aplicación
     * y que no supere la longitud máxima permitida.
     *
     * @param url dirección URL a validar
     * @param campo nombre del campo para los mensajes de error
     * @throws IllegalArgumentException si la URL no comienza por
     *                                  http:// o https://
     */
    public static void url(String url, String campo) {
        if (url != null
                && !url.startsWith("http://")
                && !url.startsWith("https://")) {

            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " debe empezar por http:// o https://");
        }

        longitud(url, campo, 255);
    }
}