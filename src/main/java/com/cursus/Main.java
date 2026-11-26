package com.cursus;
import com.cursus.ui.CursusApp;
import javafx.application.Application;

/**
 * Clase de arranque de la aplicación. Lanza la aplicación JavaFX
 * {@link CursusApp}.
 */
public class Main {
    /**
     * Punto de entrada del programa. Inicia la aplicación JavaFX.
     *
     * @param args argumentos de la línea de comandos, que se pasan a la aplicación
     */
    public static void main(String[] args) {
        Application.launch(CursusApp.class, args);
    }
}