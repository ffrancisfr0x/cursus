package com.cursus.ui;
import com.cursus.service.Validacion;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;
import java.time.LocalDate;

import java.time.format.DateTimeParseException;
import java.util.Locale;
public final class Campos {
    private static final String ERROR = "campo-error";
    private static final Locale ESPANA = Locale.forLanguageTag("es-ES");
    private Campos() {
    }
    public static Double leerNumero(TextInputControl campo, String nombre) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            marcar(campo, false);
            return null;
        }
        try {
            double numero = Double.parseDouble(texto.replace(",", "."));
            marcar(campo, false);
            return numero;
        } catch (NumberFormatException e) {
            marcar(campo, true);
            throw new IllegalArgumentException("El campo " + nombre + " debe ser un número, por ejemplo 24000.");
        }
    }
    public static boolean requerido(TextInputControl campo) {
        boolean vacio = campo.getText() == null || campo.getText().isBlank();
        marcar(campo, vacio);
        return !vacio;
    }
    public static boolean requerido(ComboBoxBase<?> campo) {
        boolean vacio = campo.getValue() == null;
        marcar(campo, vacio);
        return !vacio;
    }
    public static void marcar(Control campo, boolean error) {
        campo.getStyleClass().remove(ERROR);
        if (error) {
            campo.getStyleClass().add(ERROR);
        }
    }
    public static void formatoFecha(DatePicker selector) {
        selector.setPromptText("dd/mm/aaaa");
        selector.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : fecha.format(Validacion.FORMATO_FECHA);
            }
            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), Validacion.FORMATO_FECHA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
    }
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(Validacion.FORMATO_FECHA);
    }
    public static String salario(Double salario) {return salario == null ? "—" : String.format(ESPANA, "%,.0f €", salario);
    }
    public static String texto(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }
}
