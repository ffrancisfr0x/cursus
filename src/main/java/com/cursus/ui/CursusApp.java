package com.cursus.ui;

import com.cursus.db.ConexionBD;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Clase principal de la aplicación JavaFX. Lee la configuración, comprueba
 * la conexión con la base de datos y abre la ventana principal de Cursus.
 */
public class CursusApp extends Application {

    /**
     * Arranca la aplicación. Antes de abrir la ventana comprueba que existe
     * {@code config.properties} y que se puede conectar con MySQL; si algo
     * falla, muestra un aviso y cierra la aplicación.
     *
     * @param ventana ventana principal proporcionada por JavaFX
     * @throws IOException si no se puede cargar el archivo FXML de la ventana principal
     */
    @Override
    public void start(Stage ventana) throws IOException {
        try {
            ConexionBD conexion = ConexionBD.desdeArchivo("config.properties");
            conexion.obtener().close(); // comprueba que se puede conectar antes de abrir la ventana
            Servicios.iniciar(conexion);
        } catch (IOException e) {
            Avisos.error("No se puede leer config.properties.\n"
                    + "Copia config.properties.example como config.properties y pon tus datos de MySQL.");
            Platform.exit();
            return;
        } catch (SQLException e) {
            Avisos.error("No se puede conectar con MySQL. Comprueba que está arrancado y los datos de config.properties.\n\n" + e.getMessage());
            Platform.exit();
            return;
        }

        FXMLLoader loader = new FXMLLoader(CursusApp.class.getResource("principal.fxml"));
        ventana.setScene(new Scene(loader.load()));
        ventana.setTitle("Cursus · Gestor de candidaturas");
        ventana.setMinWidth(1100);
        ventana.setMinHeight(700);
        ventana.show();
    }
}