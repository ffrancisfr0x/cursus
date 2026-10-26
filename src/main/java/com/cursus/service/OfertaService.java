package com.cursus.service;

import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OfertaService {

    private final OfertaDAO ofertaDAO;

    public OfertaService(OfertaDAO ofertaDAO) {
        this.ofertaDAO = ofertaDAO;
    }

    public List<Oferta> listar(Estado estado, String empresa) throws IOException {
        List<Oferta> resultado = new ArrayList<>();

        for (Oferta o : ofertaDAO.leer()) {

            boolean cumpleEstado;

            if (estado == null) {
                cumpleEstado = true;
            } else {
                cumpleEstado = o.getEstado() == estado;
            }

            boolean cumpleEmpresa;

            if (empresa == null) {
                cumpleEmpresa = true;
            } else {
                String empresaOferta = o.getEmpresa().toLowerCase();
                String empresaBuscada = empresa.toLowerCase();
                cumpleEmpresa = empresaOferta.contains(empresaBuscada);
            }

            if (cumpleEstado && cumpleEmpresa) {
                resultado.add(o);
            }
        }

        return resultado;
    }
}
