package com.cursus.db;


import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    private final String url;
    private final String usuario;
    private final String password;

    public ConexionBD(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    public static ConexionBD desdeArchivo(String ruta) throws IOException {
        Properties propiedades = new Properties();
        try (FileReader lector = new FileReader(ruta, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
        }

        String url = propiedades.getProperty("db.url");

        if (url == null || url.isBlank()) {
            throw new IOException("Falta la propiedad db.url en " + ruta);
        }

        return new ConexionBD(url, propiedades.getProperty("db.usuario", ""),
                propiedades.getProperty("db.password", ""));
    }
    public Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}
