package com.cursus.model;

import java.time.LocalDate;
import java.util.List;

public class Entrevista {
    public static final List<String> TIPOS = List.of("telefónica", "técnica", "presencial", "online");

    public static final List<String> RESULTADOS = List.of("pendiente", "positivo", "negativo");

    private int id;
    private int idOferta;
    private LocalDate fecha;
    private String tipo;
    private String notas;
    private String resultado;

    public Entrevista() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
