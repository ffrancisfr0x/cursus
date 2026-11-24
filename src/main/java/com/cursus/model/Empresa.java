package com.cursus.model;

import java.time.LocalDate;

/**
 * Representa una empresa registrada en el sistema.
 */
public class Empresa {

    private int id;
    private String nombre;
    private String sector;
    private String web;
    private LocalDate fechaRegistro;

    /**
     * Crea una empresa sin inicializar sus atributos.
     */
    public Empresa() {
    }

    /**
     * Crea una empresa con todos sus atributos.
     *
     * @param id identificador de la empresa
     * @param nombre nombre de la empresa
     * @param sector sector de actividad de la empresa
     * @param web sitio web de la empresa
     * @param fechaRegistro fecha de registro de la empresa
     */
    public Empresa(int id, String nombre, String sector, String web, LocalDate fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
        this.web = web;
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene el identificador de la empresa.
     *
     * @return identificador de la empresa
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador de la empresa.
     *
     * @param id identificador de la empresa
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la empresa.
     *
     * @return nombre de la empresa
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la empresa.
     *
     * @param nombre nombre de la empresa
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el sector de actividad de la empresa.
     *
     * @return sector de la empresa
     */
    public String getSector() {
        return sector;
    }

    /**
     * Establece el sector de actividad de la empresa.
     *
     * @param sector sector de la empresa
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * Obtiene el sitio web de la empresa.
     *
     * @return sitio web de la empresa
     */
    public String getWeb() {
        return web;
    }

    /**
     * Establece el sitio web de la empresa.
     *
     * @param web sitio web de la empresa
     */
    public void setWeb(String web) {
        this.web = web;
    }

    /**
     * Obtiene la fecha de registro de la empresa.
     *
     * @return fecha de registro
     */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro de la empresa.
     *
     * @param fechaRegistro fecha de registro
     */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Devuelve la representación textual de la empresa.
     *
     * @return nombre de la empresa
     */
    @Override
    public String toString() {
        return nombre;
    }
}