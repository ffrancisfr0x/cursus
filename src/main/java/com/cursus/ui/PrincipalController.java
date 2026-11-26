package com.cursus.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * Controlador de la ventana principal. Gestiona el menú de navegación entre las
 * pantallas de candidaturas y de empresas.
 */
public class PrincipalController {

    @FXML
    private BorderPane raiz;
    @FXML
    private Button btnCandidaturas;
    @FXML
    private Button btnEmpresas;

    /**
     * Inicializa la ventana: registra el panel principal en el navegador y
     * muestra la pantalla de candidaturas como vista inicial.
     */
    @FXML
    private void initialize() {
        Navegador.iniciar(raiz);
        mostrarCandidaturas();
    }

    /**
     * Muestra la pantalla de candidaturas y marca su botón como activo.
     */
    @FXML
    private void mostrarCandidaturas() {
        marcarActivo(btnCandidaturas);
        Navegador.mostrar("ofertas.fxml");
    }

    /**
     * Muestra la pantalla de empresas y marca su botón como activo.
     */
    @FXML
    private void mostrarEmpresas() {
        marcarActivo(btnEmpresas);
        Navegador.mostrar("empresas.fxml");
    }

    /**
     * Resalta el botón de la pantalla actual y quita el resalte al otro.
     *
     * @param activo botón del menú que corresponde a la pantalla mostrada
     */
    private void marcarActivo(Button activo) {
        btnCandidaturas.getStyleClass().remove("activo");
        btnEmpresas.getStyleClass().remove("activo");
        activo.getStyleClass().add("activo");
    }
}