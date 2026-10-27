package com.cursus.model;

public enum Estado {
    PENDIENTE, ENTREVISTA, RECHAZADO, ACEPTADO;

    public static Estado desdeTexto(String texto) {
        try {
            return Estado.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + texto
                    + ". Usa pendiente, entrevista, rechazado o aceptado");
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
