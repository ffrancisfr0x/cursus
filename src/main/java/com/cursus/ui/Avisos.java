package com.cursus.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.sql.SQLException;

/**
 * Clase de utilidad para mostrar cuadros de diálogo al usuario
 * (información, errores y confirmaciones) con el estilo común de cursus.
 */
public final class Avisos {

    /**
     * Constructor privado para evitar que se creen instancias de la clase.
     */
    private Avisos() {
    }

    /**
     * Muestra un cuadro de diálogo informativo.
     *
     * @param mensaje texto que se mostrará al usuario
     */
    public static void info(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, mensaje);
    }

    /**
     * Muestra un cuadro de diálogo de error.
     *
     * @param mensaje texto del error que se mostrará al usuario
     */
    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, mensaje);
    }

    /**
     * Muestra un error de acceso a la base de datos, incluyendo el
     * mensaje original de la excepción.
     *
     * @param e excepción SQL que se ha producido
     */
    public static void errorBD(SQLException e) {
        error("No se ha podido acceder a la base de datos.\n\n" + e.getMessage());
    }

    /**
     * Pide confirmación al usuario antes de realizar una acción.
     *
     * @param mensaje pregunta que se mostrará al usuario
     * @param accion texto del botón de confirmación
     * @return true si el usuario pulsa el botón de confirmación; false si cancela
     *         o cierra el cuadro
     */
    public static boolean confirmar(String mensaje, String accion) {
        ButtonType confirmar = new ButtonType(accion, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, cancelar, confirmar);
        alerta.setTitle("Cursus");
        alerta.setHeaderText(null);

        return alerta.showAndWait().orElse(cancelar) == confirmar;
    }

    /**
     * Crea y muestra un cuadro de diálogo del tipo indicado y espera
     * a que el usuario lo cierre.
     *
     * @param tipo tipo de alerta y sea información, error, etc...
     * @param mensaje texto que se mostrará
     */
    private static void mostrar(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle("Cursus");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}