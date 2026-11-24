package com.cursus.ui;
import com.cursus.db.ConexionBD;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
public class CursusApp extends Application {
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
