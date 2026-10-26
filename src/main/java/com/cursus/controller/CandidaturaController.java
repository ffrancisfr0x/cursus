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

}