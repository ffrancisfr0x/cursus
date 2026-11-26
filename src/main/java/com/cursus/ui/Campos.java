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

/**
 * Clase de utilidad con métodos para leer, validar y dar formato a los
 * campos de los formularios de la interfaz.
 */
public final class Campos {

    private static final String ERROR = "campo-error";
    private static final Locale ESPANA = Locale.forLanguageTag("es-ES");

    /**
     * Constructor privado para evitar que se creen instancias de la clase.
     */
    private Campos() {
    }

    /**
     * Lee un número decimal de un campo de texto. Acepta tanto el punto como
     * la coma como separador decimal y marca el campo en caso de error.
     *
     * @param campo campo de texto del que se lee el valor
     * @param nombre nombre del campo, usado en el mensaje de error
     * @return el número leído o null si el campo está vacío
     * @throws IllegalArgumentException si el texto no es un número válido
     */
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

    /**
     * Comprueba que un campo de texto no esté vacío y lo marca como
     * erróneo si lo está.
     *
     * @param campo campo de texto que se desea comprobar
     * @return true si el campo tiene contenido; false si está vacío
     */
    public static boolean requerido(TextInputControl campo) {
        boolean vacio = campo.getText() == null || campo.getText().isBlank();
        marcar(campo, vacio);
        return !vacio;
    }

    /**
     * Comprueba que un desplegable o selector de fecha tenga un valor
     * seleccionado y lo marca como mal si no lo tiene.
     *
     * @param campo desplegable o selector que se desea comprobar
     * @return true si hay un valor seleccionado; false en caso contrario
     */
    public static boolean requerido(ComboBoxBase<?> campo) {
        boolean vacio = campo.getValue() == null;
        marcar(campo, vacio);
        return !vacio;
    }

    /**
     * Añade o quita la clase de estilo de error de un campo.
     *
     * @param campo control sobre el que se actúa
     * @param error true para marcar el campo como erróneo; false para quitar la marca
     */
    public static void marcar(Control campo, boolean error) {
        campo.getStyleClass().remove(ERROR);
        if (error) {
            campo.getStyleClass().add(ERROR);
        }
    }

    /**
     * Configura un selector de fecha para que muestre y acepte fechas con
     * el formato dd/MM/yyyy. Si el texto escrito no es válido, el valor
     * resultante es null.
     *
     * @param selector selector de fecha que se desea configurar
     */
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

    /**
     * Da formato a una fecha para mostrarla en pantalla.
     *
     * @param fecha fecha que se desea formatear
     * @return la fecha en formato dd/MM/yyyy o "—" si es null
     */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(Validacion.FORMATO_FECHA);
    }

    /**
     * Da formato a un salario para mostrarlo en pantalla, con separador de
     * miles y el símbolo del euro.
     *
     * @param salario salario que se desea formatear
     * @return el salario con formato español (por ejemplo, «24.000 €») o "—" si es null
     */
    public static String salario(Double salario) {
        return salario == null ? "—" : String.format(ESPANA, "%,.0f €", salario);
    }

    /**
     * Devuelve un texto listo para mostrar en pantalla.
     *
     * @param texto texto original, que puede ser null o estar en blanco
     * @return el propio texto o "—" si es null o está en blanco
     */
    public static String texto(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }
}