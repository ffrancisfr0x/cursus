package com.cursus.controller;

import com.cursus.service.OfertaService;
import com.cursus.dao.OfertaDAO;
import com.cursus.model.Estado;
import com.cursus.model.Oferta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de entrada del programa: interpreta los argumentos de la línea de comandos,
 * llama al servicio y muestra por consola los resultados o los errores.
 */
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

    /**
     * Crea el controller conectado a su servicio.
     *
     * @param servicio servicio con la lógica de las candidaturas
     */
    public CandidaturaController(OfertaService servicio) {
        this.servicio = servicio;
    }

    /**
     * Ejecuta el comando indicado en el primer argumento. Los errores de datos y de
     * archivo se muestran por la salida de error sin detener el programa.
     *
     * @param args argumentos recibidos en main: el comando y sus opciones
     */
    public void ejecutar(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: no se ha indicado ningún comando.");
            System.err.println(AYUDA);
            return;
        }
        try {
            switch (args[0]) {
                case "-listar" -> listar(args);
                case "-añadir" -> anadir(args);
                case "-ver"    -> ver(args);
                case "-borrar" -> borrar(args);
                case "-importar" -> importar(args);
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

    /**
     * Comando -listar: muestra las candidaturas en una tabla, con los filtros
     * opcionales --estado y --empresa.
     *
     * @param args argumentos del comando
     * @throws IOException si el archivo de datos no se puede leer
     */
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

    /**
     * Comando -añadir: crea una candidatura con --titulo, --empresa y --estado
     * (obligatorios) y los opcionales --fecha, --enlace y --notas.
     *
     * @param args argumentos del comando
     * @throws IOException si el archivo de datos no se puede leer o escribir
     */
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

    /**
     * Comando -ver: muestra todos los datos de la candidatura cuyo ID se indica.
     *
     * @param args argumentos del comando; el segundo es el ID
     * @throws IOException si el archivo de datos no se puede leer
     */
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

    /**
     * Comando -borrar: elimina la candidatura cuyo ID se indica.
     *
     * @param args argumentos del comando; el segundo es el ID
     * @throws IOException si el archivo de datos no se puede leer o escribir
     */
    private void borrar(String[] args) throws IOException {
        Oferta borrada = servicio.borrar(leerId(args));
        System.out.println("Candidatura #" + borrada.getId() + " eliminada.");
    }

    /**
     * Comando -importar: añade las candidaturas de un archivo CSV y muestra
     * cuántas se han importado y qué líneas se han descartado.
     *
     * @param args argumentos del comando; el segundo es la ruta del CSV
     * @throws IOException si algún archivo no se puede leer o escribir
     */
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

    /**
     * Busca una opción (por ejemplo --estado) y devuelve el valor que la sigue.
     * Elimina las comillas tipográficas por si el texto se copió de un documento.
     *
     * @param args   argumentos del comando
     * @param opcion nombre de la opción a buscar
     * @return el valor de la opción, o null si no aparece
     */
    private String leerOpcion(String[] args, String opcion) {
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equals(opcion)) {

                return args[i + 1].replace("“", "").replace("”", "");
            }
        }
        return null;
    }

    /**
     * Igual que {@link #leerOpcion}, pero la opción es obligatoria.
     *
     * @param args   argumentos del comando
     * @param opcion nombre de la opción a buscar
     * @return el valor de la opción
     * @throws IllegalArgumentException si la opción no aparece
     */
    private String obligatoria(String[] args, String opcion) {

        String valor = leerOpcion(args, opcion);
        if (valor == null) {
            throw new IllegalArgumentException("Falta el argumento " + opcion + ".");
        }
        return valor;
    }

    /**
     * Lee el ID que acompaña al comando (-ver 13, -borrar 13).
     *
     * @param args argumentos del comando
     * @return el ID como número
     * @throws IllegalArgumentException si falta el ID o no es un número
     */
    private int leerId(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Falta el ID. Ejemplo: " + args[0] + " 13");
        }
        return OfertaDAO.convertirId(args[1]);
    }
}

