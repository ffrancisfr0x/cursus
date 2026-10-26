package com.cursus.controller;

import com.cursus.service.OfertaService;

public class CandidaturaController {

    private static final String AYUDA = """
            Comandos disponibles:
              -listar [--estado X] [--empresa Y]
              -añadir --titulo X --empresa Y --estado Z [--fecha dd/MM/yyyy] [--enlace U] [--notas N]
              -ver ID
              -borrar ID
              -importar archivo.csv
            Estados: pendiente, entrevista, rechazado, aceptado""";

    private final OfertaService servicio;

    public CandidaturaController(OfertaService servicio) {
        this.servicio = servicio;
    }

    public void ejecutar(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: no se ha indicado ningún comando.");
            System.err.println(AYUDA);
            return;
        }
        try {
            switch (args[0]) {
                default -> {
                    System.err.println("Error: comando desconocido \"" + args[0] + "\".");
                    System.err.println(AYUDA);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    private String leerOpcion(String[] args, String opcion) {
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equals(opcion)) {
                // Quita las comillas tipográficas “ ” por si el texto se ha copiado de un documento
                return args[i + 1].replace("“", "").replace("”", "");
            }
        }
        return null;
    }
}