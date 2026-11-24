package com.cursus.ui;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.sql.SQLException;
public final class Avisos {
    private Avisos() {
    }
    public static void info(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, mensaje);
    }
    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, mensaje);
    }
    public static void errorBD(SQLException e) {
        error("No se ha podido acceder a la base de datos.\n\n" + e.getMessage());
    }
    public static boolean confirmar(String mensaje, String accion) {
        ButtonType confirmar = new ButtonType(accion, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, cancelar, confirmar);
        alerta.setTitle("Cursus");
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(cancelar) == confirmar;
    }
    private static void mostrar(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle("Cursus");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
