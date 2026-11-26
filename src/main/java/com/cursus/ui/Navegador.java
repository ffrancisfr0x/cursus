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

/**
 * Clase de utilidad que gestiona la navegación de la aplicación: cambia la
 * vista central de la ventana principal y abre ventanas de diálogo modales.
 */
public final class Navegador {
    private static BorderPane raiz;

    /**
     * Constructor privado para impedir que se creen instancias de la clase.
     */
    private Navegador() {
    }

    /**
     * Registra el panel principal en el que se mostrarán las vistas. Debe
     * llamarse una vez antes de usar el resto de métodos.
     *
     * @param principal panel raíz de la ventana principal
     */
    public static void iniciar(BorderPane principal) {
        raiz = principal;
    }

    /**
     * Carga una vista FXML y la muestra en el centro de la ventana principal.
     *
     * @param fxml ruta del archivo FXML, relativa a esta clase
     * @param <T> tipo del controlador de la vista
     * @return el controlador asociado a la vista cargada
     * @throws UncheckedIOException si no se puede cargar el archivo FXML
     */
    public static <T> T mostrar(String fxml) {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(fxml));
        raiz.setCenter(cargar(loader, fxml));
        return loader.getController();
    }

    /**
     * Abre una ventana de diálogo modal con una vista FXML y espera a que se
     * cierre. Antes de mostrarla, permite preparar su controlador.
     *
     * @param fxml ruta del archivo FXML, relativa a esta clase
     * @param titulo título de la ventana del diálogo
     * @param preparar acción que configura el controlador antes de mostrar el diálogo
     * @param <T> tipo del controlador del diálogo
     * @return el controlador del diálogo, para consultar su estado al cerrarse
     * @throws UncheckedIOException si no se puede cargar el archivo FXML
     */
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

    /**
     * Carga una vista con el cargador indicado y convierte el error de lectura
     * en una excepción no comprobada.
     *
     * @param loader cargador FXML ya creado para el archivo
     * @param fxml ruta del archivo, usada en el mensaje de error
     * @return la vista cargada
     * @throws UncheckedIOException si no se puede cargar el archivo FXML
     */
    private static Parent cargar(FXMLLoader loader, String fxml) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("No se puede cargar " + fxml, e);
        }
    }
}