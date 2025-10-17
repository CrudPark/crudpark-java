package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DatabaseConfig instance;
    private Connection connection;

    private String url;
    private String user;
    private String password;

    private DatabaseConfig() {
        try {

            PropertiesConfig props = PropertiesConfig.getInstance();

            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");


            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión establecida exitosamente");
        }
        return connection;
    }

    public Connection getNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }


}