package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {

    private static PropertiesConfig instance;
    private final Properties properties;
    private static final String CONFIG_FILE = "config.properties";

    private PropertiesConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static PropertiesConfig getInstance() {
        if (instance == null) {
            // Uso de synchronized para asegurar que solo una instancia sea creada en entornos multihilo
            synchronized (PropertiesConfig.class) {
                if (instance == null) {
                    instance = new PropertiesConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        // Usamos try-with-resources para asegurar que el InputStream se cierre automáticamente
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            // Verificamos si el ClassLoader encontró el recurso DENTRO del JAR/Classpath
            if (input == null) {
                // Si input es null, el archivo no se incluyó en el JAR.
                // No intentamos usar FileInputStream para evitar dependencias de rutas externas.
                throw new IOException("El archivo de configuración '" + CONFIG_FILE + "' NO fue encontrado en el classpath (resources/).");
            }

            properties.load(input);
            System.out.println("Archivo de configuración cargado exitosamente");

        } catch (IOException e) {
            // Capturamos el error (ya sea de archivo no encontrado o de lectura)
            System.err.println("Error grave al cargar el archivo de configuración: " + e.getMessage());
            e.printStackTrace();

            // Si falla la carga, cargamos la configuración por defecto
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        System.out.println("Cargando configuración por defecto...");
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/crudpark_db");
        properties.setProperty("db.user", "postgres");
        properties.setProperty("db.password", "postgres");
        properties.setProperty("app.name", "CrudPark");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("ticket.tiempo_gracia", "30");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}