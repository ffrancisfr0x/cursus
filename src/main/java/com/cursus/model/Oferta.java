package com.cursus.model;

import java.time.LocalDate;

public class Oferta {
    private int id;
    private String titulo;
    private String empresa;
    private Estado estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private String notas;

    public Oferta(int id, String titulo, String empresa, Estado estado, LocalDate fechaPostulacion, String enlace, String notas) {
        this.id = id;
        this.titulo = titulo;
        this.empresa = empresa;
        this.estado = estado;
        this.fechaPostulacion = fechaPostulacion;
        this.enlace = enlace;
        this.notas = notas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public String getEnlace() {
        return enlace;
    }

    public String getNotas() {
        return notas;
    }
}
