package com.cursus.db;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la configuración y creación de conexiones con la base de datos.
 */
public class ConexionBD {

    private final String url;
    private final String usuario;
    private final String password;

    /**
     * Crea una configuración de conexión a partir de los parámetros indicados.
     *
     * @param url URL de conexión a la base de datos
     * @param usuario nombre de usuario de acceso
     * @param password contraseña de acceso
     */
    public ConexionBD(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    /**
     * Carga la configuración de conexión desde un archivo de propiedades.
     *
     * @param ruta ruta del archivo de configuración
     * @return instancia configurada de ConexionBD
     * @throws IOException si ocurre un error al leer el archivo o falta la
     *                     propiedad obligatoria db.url
     */
    public static ConexionBD desdeArchivo(String ruta) throws IOException {
        Properties propiedades = new Properties();

        try (FileReader lector = new FileReader(ruta, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
        }

        String url = propiedades.getProperty("db.url");

        if (url == null || url.isBlank()) {
            throw new IOException("Falta la propiedad db.url en " + ruta);
        }

        return new ConexionBD(
                url,
                propiedades.getProperty("db.usuario", ""),
                propiedades.getProperty("db.password", "")
        );
    }

    /**
     * Obtiene una nueva conexión a la base de datos utilizando la
     * configuración almacenada.
     *
     * @return conexión abierta a la base de datos
     * @throws SQLException si se produce un error al establecer la conexión
     */
    public Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}