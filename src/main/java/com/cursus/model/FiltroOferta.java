package com.cursus.model;

import java.time.LocalDate;

/**
 * Representa los criterios utilizados para filtrar búsquedas de ofertas
 * de empleo.
 */
public class FiltroOferta {

    private String titulo;
    private String empresa;
    private Estado estado;
    private Double salarioMinimo;
    private Double salarioMaximo;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    /**
     * Obtiene el título que debe contener la oferta.
     *
     * @return texto utilizado para filtrar por título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el criterio de búsqueda por título.
     *
     * @param titulo texto que debe contener el título de la oferta
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el nombre de la empresa utilizado como filtro.
     *
     * @return nombre de la empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * Establece el criterio de búsqueda por empresa.
     *
     * @param empresa nombre de la empresa
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * Obtiene el estado utilizado como filtro.
     *
     * @return estado de la oferta
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece el estado por el que se filtrarán las ofertas.
     *
     * @param estado estado de la oferta
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el salario mínimo utilizado como filtro.
     *
     * @return salario mínimo
     */
    public Double getSalarioMinimo() {
        return salarioMinimo;
    }

    /**
     * Establece el salario mínimo para la búsqueda.
     *
     * @param salarioMinimo salario mínimo deseado
     */
    public void setSalarioMinimo(Double salarioMinimo) {
        this.salarioMinimo = salarioMinimo;
    }

    /**
     * Obtiene el salario máximo utilizado como filtro.
     *
     * @return salario máximo
     */
    public Double getSalarioMaximo() {
        return salarioMaximo;
    }

    /**
     * Establece el salario máximo para la búsqueda.
     *
     * @param salarioMaximo salario máximo deseado
     */
    public void setSalarioMaximo(Double salarioMaximo) {
        this.salarioMaximo = salarioMaximo;
    }

    /**
     * Obtiene la fecha mínima de postulación.
     *
     * @return fecha inicial del rango
     */
    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    /**
     * Establece la fecha mínima de postulación.
     *
     * @param fechaDesde fecha inicial del rango
     */
    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    /**
     * Obtiene la fecha máxima de postulación.
     *
     * @return fecha final del rango
     */
    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    /**
     * Establece la fecha máxima de postulación.
     *
     * @param fechaHasta fecha final del rango
     */
    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}