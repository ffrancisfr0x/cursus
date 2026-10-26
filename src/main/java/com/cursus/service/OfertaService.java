package com.cursus.service;

import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public Oferta anadir(String titulo, String empresa, String estado, String fecha,
                         String enlace, String notas) throws IOException {

        comprobarTexto("titulo", titulo);
        comprobarTexto("empresa", empresa);

        List<Oferta> ofertas = ofertaDAO.leer();

        Oferta nueva = new Oferta(siguienteId(ofertas), titulo, empresa, Estado.desdeTexto(estado),
                fecha == null ? LocalDate.now() : OfertaDAO.convertirFecha(fecha), enlace == null ? "" :
                enlace, notas == null ? "" : notas);

        ofertas.add(nueva);
        ofertaDAO.guardar(ofertas);

        return nueva;
    }

    public Oferta ver(int id) throws IOException {
        for (Oferta o : ofertaDAO.leer()) {
            if (o.getId() == id) {
                return o;
            }
        }
        throw new IllegalArgumentException("No existe ninguna candidatura con el ID " + id + ".");
    }

    public Oferta borrar(int id) throws IOException {
        List<Oferta> ofertas = ofertaDAO.leer();
        Oferta borrada = ver(id);
        ofertas.removeIf(o -> o.getId() == id);
        ofertaDAO.guardar(ofertas);
        return borrada;
    }

    public int importar(String rutaCsv, List<String> errores) throws IOException {
        File csv = new File(rutaCsv);
        if (!csv.exists()) {
            throw new IllegalArgumentException("No se encuentra el archivo " + rutaCsv + ".");
        }
        List<Oferta> ofertas = ofertaDAO.leer();
        int importadas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csv, StandardCharsets.UTF_8))) {
            String linea;
            int num = 0;
            while ((linea = br.readLine()) != null) {
                num++;
                if (OfertaDAO.esCabeceraOVacia(linea)) {
                    continue;
                }
                try {
                    Oferta oferta = OfertaDAO.convertirLinea(linea);
                    if (existeId(ofertas, oferta.getId())) {
                        oferta.setId(siguienteId(ofertas));
                    }
                    ofertas.add(oferta);
                    importadas++;
                } catch (IllegalArgumentException e) {
                    errores.add("Línea " + num + ": " + e.getMessage());
                }
            }
        }
        ofertaDAO.guardar(ofertas);
        return importadas;
    }

    private int siguienteId(List<Oferta> ofertas) {
        int maximo = 0;
        for (Oferta o : ofertas) {
            maximo = Math.max(maximo, o.getId());
        }
        return maximo + 1;
    }

    private boolean existeId(List<Oferta> ofertas, int id) {
        for (Oferta o : ofertas) {
            if (o.getId() == id) {
                return true;
            }
        }
        return false;
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
