package com.cursus.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa una entrevista asociada a una oferta de empleo.
 */
public class Entrevista {

    /**
     * Tipos de entrevista admitidos por la aplicación.
     */
    public static final List<String> TIPOS =
            List.of("telefónica", "técnica", "presencial", "online");

    /**
     * Posibles resultados de una entrevista.
     */
    public static final List<String> RESULTADOS =
            List.of("pendiente", "positivo", "negativo");

    private int id;
    private int idOferta;
    private LocalDate fecha;
    private String tipo;
    private String notas;
    private String resultado;

    /**
     * Crea una entrevista sin inicializar sus atributos.
     */
    public Entrevista() {
    }

    /**
     * Obtiene el identificador de la entrevista.
     *
     * @return identificador de la entrevista
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador de la entrevista.
     *
     * @param id identificador de la entrevista
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador de la oferta asociada.
     *
     * @return identificador de la oferta
     */
    public int getIdOferta() {
        return idOferta;
    }

    /**
     * Establece el identificador de la oferta asociada.
     *
     * @param idOferta identificador de la oferta
     */
    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

    /**
     * Obtiene la fecha programada para la entrevista.
     *
     * @return fecha de la entrevista
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha programada para la entrevista.
     *
     * @param fecha fecha de la entrevista
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el tipo de entrevista.
     *
     * @return tipo de entrevista
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de entrevista.
     *
     * @param tipo tipo de entrevista
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene las notas registradas sobre la entrevista.
     *
     * @return notas de la entrevista
     */
    public String getNotas() {
        return notas;
    }

    /**
     * Establece las notas de la entrevista.
     *
     * @param notas observaciones o comentarios de la entrevista
     */
    public void setNotas(String notas) {
        this.notas = notas;
    }

    /**
     * Obtiene el resultado de la entrevista.
     *
     * @return resultado de la entrevista
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Establece el resultado de la entrevista.
     *
     * @param resultado resultado de la entrevista
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}