package database;

import com.zaxxer.hikari.*;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class Database {
    private static Database instance;
    private final HikariDataSource dataSource;

    private Database() throws IOException {
        Properties properties = loadPropertiesFromFile();

        String database = properties.getProperty("database");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://localhost:3306/%s?rewriteBatchedStatements=true", database));
        config.setUsername(user);
        config.setPassword(password);

        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaximumPoolSize(100);
        config.setMinimumIdle(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static synchronized Database getInstance() throws SQLException, IOException {
        if (instance == null) instance = new Database();

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private Properties loadPropertiesFromFile() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mysql.properties")) {
            if (inputStream == null)
                throw new FileNotFoundException("Property file mysql.properties not found in the classpath.");

            properties.load(inputStream);
        }

        return properties;
    }
}
