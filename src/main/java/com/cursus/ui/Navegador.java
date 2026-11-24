package com.cursus.ui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
public final class Navegador {
    private static BorderPane raiz;
    private Navegador() {
    }
    public static void iniciar(BorderPane principal) {
        raiz = principal;
    }
    public static <T> T mostrar(String fxml) {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(fxml));
        raiz.setCenter(cargar(loader, fxml));
        return loader.getController();
    }
    public static <T> T abrirDialogo(String fxml, String titulo, Consumer<T> preparar) {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(fxml));
        Parent vista = cargar(loader, fxml);
        T controlador = loader.getController();
        preparar.accept(controlador);
        Stage dialogo = new Stage();
        dialogo.initOwner(raiz.getScene().getWindow());
        dialogo.initModality(Modality.WINDOW_MODAL);
        dialogo.setTitle(titulo);
        dialogo.setResizable(false);
        dialogo.setScene(new Scene(vista));
        dialogo.showAndWait();
        return controlador;
    }
    private static Parent cargar(FXMLLoader loader, String fxml) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("No se puede cargar " + fxml, e);
        }
    }
}