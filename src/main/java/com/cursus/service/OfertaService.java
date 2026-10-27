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

/**
 * Lógica de las candidaturas: listar, añadir, ver, borrar e importar.
 * <p>
 * Usa {@link OfertaDAO} para leer y guardar el archivo de datos. Los errores de datos
 * se avisan con {@link IllegalArgumentException} y un mensaje claro, que muestra el controller.
 */
public class OfertaService {

    /** Acceso al archivo de datos. */
    private final OfertaDAO ofertaDAO;

    /**
     * Crea el servicio.
     *
     * @param ofertaDAO acceso al archivo de candidaturas
     */
    public OfertaService(OfertaDAO ofertaDAO) {
        this.ofertaDAO = ofertaDAO;
    }

    /**
     * Devuelve las candidaturas que cumplen los filtros indicados.
     * Si un filtro es {@code null}, no se aplica; si los dos son {@code null}, devuelve todas.
     *
     * @param estado  estado por el que filtrar, o {@code null} para no filtrar
     * @param empresa texto que debe contener el nombre de la empresa, sin distinguir mayúsculas,
     *                o {@code null} para no filtrar
     * @return las candidaturas que cumplen los filtros
     * @throws IOException si el archivo no se puede leer
     */
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

    /**
     * Añade una candidatura nueva con el siguiente ID libre y la guarda en el archivo.
     *
     * @param titulo  puesto al que se opta (obligatorio)
     * @param empresa empresa que ofrece el puesto (obligatoria)
     * @param estado  estado como texto, por ejemplo "pendiente"
     * @param fecha   fecha como texto dd/MM/yyyy, o {@code null} para usar la de hoy
     * @param enlace  enlace a la oferta, o {@code null}
     * @param notas   notas sobre la candidatura, o {@code null}
     * @return la candidatura creada, con su ID
     * @throws IOException si el archivo no se puede leer o escribir
     * @throws IllegalArgumentException si falta el título o la empresa, o si el estado o la fecha no son válidos
     */
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

    /**
     * Busca una candidatura por su ID.
     *
     * @param id identificador de la candidatura
     * @return la candidatura con ese ID
     * @throws IOException si el archivo no se puede leer
     * @throws IllegalArgumentException si no existe ninguna candidatura con ese ID
     */
    public Oferta ver(int id) throws IOException {
        for (Oferta o : ofertaDAO.leer()) {
            if (o.getId() == id) {
                return o;
            }
        }
        throw new IllegalArgumentException("No existe ninguna candidatura con el ID " + id + ".");
    }

    /**
     * Elimina una candidatura y guarda el archivo.
     *
     * @param id identificador de la candidatura
     * @return la candidatura eliminada
     * @throws IOException si el archivo no se puede leer o escribir
     * @throws IllegalArgumentException si no existe ninguna candidatura con ese ID
     */
    public Oferta borrar(int id) throws IOException {
        List<Oferta> ofertas = ofertaDAO.leer();
        Oferta borrada = ver(id);
        ofertas.removeIf(o -> o.getId() == id);
        ofertaDAO.guardar(ofertas);
        return borrada;
    }

    /**
     * Importa las candidaturas de un archivo CSV y las añade al archivo de datos.
     * <p>
     * Las líneas con errores no se importan: se apuntan en {@code errores} y se sigue con la siguiente.
     * Si el ID de una línea ya existe, se le asigna el siguiente ID libre.
     *
     * @param rutaCsv ruta del archivo CSV
     * @param errores lista donde se apuntan las líneas que no se han podido importar
     * @return número de candidaturas importadas
     * @throws IOException si algún archivo no se puede leer o escribir
     * @throws IllegalArgumentException si el archivo CSV no existe
     */
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

    /**
     * Calcula el siguiente ID libre: el mayor que haya más uno.
     *
     * @param ofertas candidaturas actuales
     * @return el siguiente ID libre, o 1 si no hay ninguna
     */
    private int siguienteId(List<Oferta> ofertas) {
        int maximo = 0;
        for (Oferta o : ofertas) {
            maximo = Math.max(maximo, o.getId());
        }
        return maximo + 1;
    }

    /**
     * Indica si ya hay una candidatura con ese ID.
     *
     * @param ofertas candidaturas actuales
     * @param id      identificador a buscar
     * @return {@code true} si el ID ya existe
     */
    private boolean existeId(List<Oferta> ofertas, int id) {
        for (Oferta o : ofertas) {
            if (o.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba que un campo de texto obligatorio no está vacío y no contiene ";".
     *
     * @param campo nombre del campo, para el mensaje de error
     * @param valor valor escrito por el usuario
     * @throws IllegalArgumentException si el valor falta, está vacío o contiene ";"
     */
    private void comprobarTexto(String campo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta el argumento --" + campo + ".");
        }
        if (valor.contains(";")) {
            throw new IllegalArgumentException("El campo " + campo + " no puede contener \";\".");
        }
    }

}