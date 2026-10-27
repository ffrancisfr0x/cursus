package com.cursus.model;

import java.time.LocalDate;

/**
 * Candidatura a una oferta de empleo.
 * <p>
 * Cada candidatura ocupa una línea del archivo de datos.
 */
public class Oferta {
    /** Identificador único de la candidatura. */
    private int id;
    /** Puesto al que se opta, por ejemplo "Desarrollador Java". */
    private String titulo;
    /** Empresa que ofrece el puesto. */
    private String empresa;
    /** Estado actual del proceso de selección. */
    private Estado estado;
    /** Fecha en la que se envió la candidatura. */
    private LocalDate fechaPostulacion;
    /** Enlace a la oferta. Puede estar vacío. */
    private String enlace;
    /** Notas libres sobre la candidatura. Pueden estar vacías. */
    private String notas;

    /**
     * Crea una candidatura con todos sus datos.
     *
     * @param id               identificador único
     * @param titulo           puesto al que se opta
     * @param empresa          empresa que ofrece el puesto
     * @param estado           estado del proceso de selección
     * @param fechaPostulacion fecha en la que se envió la candidatura
     * @param enlace           enlace a la oferta, o cadena vacía
     * @param notas            notas sobre la candidatura, o cadena vacía
     */
    public Oferta(int id, String titulo, String empresa, Estado estado, LocalDate fechaPostulacion, String enlace, String notas) {
        this.id = id;
        this.titulo = titulo;
        this.empresa = empresa;
        this.estado = estado;
        this.fechaPostulacion = fechaPostulacion;
        this.enlace = enlace;
        this.notas = notas;
    }

    /**
     * Devuelve el identificador de la candidatura.
     *
     * @return el ID
     */
    public int getId() {
        return id;
    }

    /**
     * Cambia el identificador. Se usa al importar, cuando el ID del CSV ya existe.
     *
     * @param id nuevo identificador
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el puesto al que se opta.
     *
     * @return el título del puesto
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Devuelve la empresa que ofrece el puesto.
     *
     * @return el nombre de la empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * Devuelve el estado del proceso de selección.
     *
     * @return el estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Devuelve la fecha en la que se envió la candidatura.
     *
     * @return la fecha de postulación
     */
    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    /**
     * Devuelve el enlace a la oferta.
     *
     * @return el enlace, o cadena vacía si no tiene
     */
    public String getEnlace() {
        return enlace;
    }

    /**
     * Devuelve las notas de la candidatura.
     *
     * @return las notas, o cadena vacía si no tiene
     */
    public String getNotas() {
        return notas;
    }
}