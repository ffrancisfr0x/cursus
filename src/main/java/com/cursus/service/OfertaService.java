package com.cursus.service;

import com.cursus.dao.OfertaDAO;

public class OfertaService {

    private final OfertaDAO ofertaDAO;

    public OfertaService(OfertaDAO ofertaDAO) {
        this.ofertaDAO = ofertaDAO;
    }


}
