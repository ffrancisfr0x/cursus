package com.cursus;

import com.cursus.controller.CandidaturaController;
import com.cursus.dao.OfertaDAO;
import com.cursus.service.OfertaService;

import java.io.File;

/**
 * Punto de entrada de Cursus: crea las capas (DAO, servicio y controller),
 * las conecta y le pasa los argumentos al controller.
 */
public class Main {

    /**
     * Arranca el programa y ejecuta una sola operación.
     *
     * @param args el comando y sus opciones, por ejemplo {@code -ver 13}
     */
    public static void main(String[] args) {
        OfertaDAO dao = new OfertaDAO(new File("data", "candidaturas.txt"));
        OfertaService servicio = new OfertaService(dao);
        CandidaturaController controlador = new CandidaturaController(servicio);
        controlador.ejecutar(args);
    }
}