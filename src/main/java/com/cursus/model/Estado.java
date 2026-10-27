package com.cursus.model;

/**
 * Estados posibles de una candidatura.
 * <p>
 * En el archivo y en los comandos se escriben en minúsculas: pendiente, entrevista, rechazado o aceptado.
 */
public enum Estado {
    /** Se ha enviado el CV y no hay respuesta todavía. */
    PENDIENTE,
    /** La empresa ha llamado para hacer una entrevista. */
    ENTREVISTA,
    /** La empresa ha descartado la candidatura. */
    RECHAZADO,
    /** La empresa ha ofrecido el puesto. */
    ACEPTADO;

    /**
     * Convierte un texto en un estado, sin distinguir mayúsculas ni espacios de más.
     *
     * @param texto estado como texto, por ejemplo "pendiente"
     * @return el estado correspondiente
     * @throws IllegalArgumentException si el texto no es ningún estado válido
     */
    public static Estado desdeTexto(String texto) {
        try {
            return Estado.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + texto
                    + ". Usa pendiente, entrevista, rechazado o aceptado");
        }
    }

    /**
     * Devuelve el estado en minúsculas, que es como se guarda en el archivo y se muestra por consola.
     *
     * @return el nombre del estado en minúsculas, por ejemplo "pendiente"
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}