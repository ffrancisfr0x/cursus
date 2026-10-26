package com.cursus;

import com.cursus.controller.CandidaturaController;
import com.cursus.dao.OfertaDAO;
import com.cursus.service.OfertaService;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        OfertaDAO dao = new OfertaDAO(new File("data", "candidaturas.txt"));
        OfertaService servicio = new OfertaService(dao);
        CandidaturaController controlador = new CandidaturaController(servicio);
        controlador.ejecutar(args);
    }
}