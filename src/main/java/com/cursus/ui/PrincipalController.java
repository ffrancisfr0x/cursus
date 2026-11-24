package com.cursus.ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
public class PrincipalController {
    @FXML
    private BorderPane raiz;
    @FXML
    private Button btnCandidaturas;
    @FXML
    private Button btnEmpresas;
    @FXML
    private void initialize() {
        Navegador.iniciar(raiz);
        mostrarCandidaturas();
    }
    @FXML
    private void mostrarCandidaturas() {
        marcarActivo(btnCandidaturas);
        Navegador.mostrar("ofertas.fxml");
    }
    @FXML
    private void mostrarEmpresas() {
        marcarActivo(btnEmpresas);
    }
    private void marcarActivo(Button activo) {
        btnCandidaturas.getStyleClass().remove("activo");
        btnEmpresas.getStyleClass().remove("activo");
        activo.getStyleClass().add("activo");
    }
}
