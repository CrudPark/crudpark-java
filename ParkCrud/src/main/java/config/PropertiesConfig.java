package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {

    private static PropertiesConfig instance;
    private Properties properties;

    private PropertiesConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static PropertiesConfig getInstance() {
        if (instance == null) {
            synchronized (PropertiesConfig.class) {
                if (instance == null) {
                    instance = new PropertiesConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try {
            // Primero intenta cargar desde el classpath (resources)
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

            // Si no existe en resources, intenta cargar desde archivo externo
            if (input == null) {
                input = new FileInputStream("config.properties");
            }

            properties.load(input);
            System.out.println("Archivo de configuración cargado exitosamente");
            input.close();

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo de configuración: " + e.getMessage());
            e.printStackTrace();
            // Cargar valores por defecto si falla
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

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getPropertyAsInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir propiedad a entero: " + key);
            }
        }
        return defaultValue;
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void printAllProperties() {
        System.out.println("=== Configuración actual ===");
        properties.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
        System.out.println("===========================");
    }
}