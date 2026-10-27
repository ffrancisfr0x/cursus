package com.cursus.controller;

import com.cursus.service.OfertaService;
import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                case "-listar" -> listar(args);
                default -> {
                    System.err.println("Error: comando desconocido \"" + args[0] + "\".");
                    System.err.println(AYUDA);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }catch (IOException e) {
            System.err.println("Error al acceder al archivo de datos: " + e.getMessage());
        }
    }

    private void listar(String[] args) throws IOException {
        String textoEstado = leerOpcion(args, "--estado");
        Estado estado = textoEstado == null ? null : Estado.desdeTexto(textoEstado);
        List<Oferta> ofertas = servicio.listar(estado, leerOpcion(args, "--empresa"));
        if (ofertas.isEmpty()) {
            System.out.println("No hay candidaturas que mostrar.");
            return;
        }
        System.out.printf("%-4s %-28s %-18s %-11s %s%n", "ID", "PUESTO", "EMPRESA", "ESTADO", "FECHA");
        for (Oferta o : ofertas) {
            System.out.printf("%-4d %-28s %-18s %-11s %s%n", o.getId(), o.getTitulo(), o.getEmpresa(),
                    o.getEstado(), o.getFechaPostulacion().format(OfertaDAO.FORMATO_FECHA));
        }
        System.out.println(ofertas.size() + " candidatura(s).");
    }

    private void anadir(String[] args) throws IOException {
        Oferta nueva = servicio.anadir(
                leerOpcion(args, "--titulo"),
                leerOpcion(args, "--empresa"),
                obligatoria(args, "--estado"),
                leerOpcion(args, "--fecha"),
                leerOpcion(args, "--enlace"),
                leerOpcion(args, "--notas"));
        System.out.println("Candidatura añadida con el ID " + nueva.getId() + ".");
    }

    private void ver(String[] args) throws IOException {
        Oferta o = servicio.ver(leerId(args));
        System.out.println("Candidatura #" + o.getId());
        System.out.println(" Puesto: " + o.getTitulo());
        System.out.println(" Empresa: " + o.getEmpresa());
        System.out.println(" Estado: " + o.getEstado());
        System.out.println(" Fecha: " + o.getFechaPostulacion().format(OfertaDAO.FORMATO_FECHA));
        System.out.println(" Enlace: " + o.getEnlace());
        System.out.println(" Notas: " + o.getNotas());
    }

    private void borrar(String[] args) throws IOException {
        Oferta borrada = servicio.borrar(leerId(args));
        System.out.println("Candidatura #" + borrada.getId() + " eliminada.");
    }


    private void importar(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Falta la ruta del archivo CSV. Ejemplo: -importar datos.csv");
        }
        List<String> errores = new ArrayList<>();
        int importadas = servicio.importar(args[1], errores);
        System.out.println(importadas + " candidatura(s) importada(s).");
        for (String error : errores) {
            System.out.println(" No importada. " + error);
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

    private String obligatoria(String[] args, String opcion) {

        String valor = leerOpcion(args, opcion);
        if (valor == null) {
            throw new IllegalArgumentException("Falta el argumento " + opcion + ".");
        }
        return valor;
    }

    private int leerId(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Falta el ID. Ejemplo: " + args[0] + " 13");
        }
        return OfertaDAO.convertirId(args[1]);
    }
}

