package com.cursus.service;

import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.IOException;
import java.time.LocalDate;
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

    private int siguienteId(List<Oferta> ofertas) {
        int maximo = 0;
        for (Oferta o : ofertas) {
            maximo = Math.max(maximo, o.getId());
        }
        return maximo + 1;
    }

    private void comprobarTexto(String campo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta el argumento --" + campo + ".");
        }
        if (valor.contains(";")) {
            throw new IllegalArgumentException("El campo " + campo + " no puede contener \";\".");
        }
    }

}
