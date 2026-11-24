package com.cursus.model;

import java.time.LocalDate;

/**
 * Representa una oferta de empleo registrada en el sistema.
 */
public class Oferta {

    private int id;
    private Empresa empresa;
    private String titulo;
    private String descripcion;
    private Estado estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private Double salario;
    private String notas;

    /**
     * Crea una oferta sin inicializar sus atributos.
     */
    public Oferta() {
    }

    /**
     * Obtiene el identificador de la oferta.
     *
     * @return identificador de la oferta
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador de la oferta.
     *
     * @param id identificador de la oferta
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene la empresa asociada a la oferta.
     *
     * @return empresa de la oferta
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    /**
     * Establece la empresa asociada a la oferta.
     *
     * @param empresa empresa de la oferta
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * Obtiene el título de la oferta.
     *
     * @return título de la oferta
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título de la oferta.
     *
     * @param titulo título de la oferta
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripción de la oferta.
     *
     * @return descripción de la oferta
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la oferta.
     *
     * @param descripcion descripción de la oferta
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el estado actual de la candidatura.
     *
     * @return estado de la candidatura
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual de la candidatura.
     *
     * @param estado estado de la candidatura
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la fecha en la que se realizó la postulación.
     *
     * @return fecha de postulación
     */
    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    /**
     * Establece la fecha de postulación.
     *
     * @param fechaPostulacion fecha de postulación
     */
    public void setFechaPostulacion(LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    /**
     * Obtiene el enlace a la oferta de empleo.
     *
     * @return URL de la oferta
     */
    public String getEnlace() {
        return enlace;
    }

    /**
     * Establece el enlace a la oferta de empleo.
     *
     * @param enlace URL de la oferta
     */
    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    /**
     * Obtiene el salario asociado a la oferta.
     *
     * @return salario ofertado o null si no está especificado
     */
    public Double getSalario() {
        return salario;
    }

    /**
     * Establece el salario asociado a la oferta.
     *
     * @param salario salario ofertado
     */
    public void setSalario(Double salario) {
        this.salario = salario;
    }

    /**
     * Obtiene las notas relacionadas con la oferta.
     *
     * @return notas de seguimiento
     */
    public String getNotas() {
        return notas;
    }

    /**
     * Establece las notas relacionadas con la oferta.
     *
     * @param notas notas de seguimiento
     */
    public void setNotas(String notas) {
        this.notas = notas;
    }
}