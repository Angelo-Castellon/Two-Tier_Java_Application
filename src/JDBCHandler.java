import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCHandler {
    private Connection connection;
    private String databaseUrl;
    private String username;
    private String password;

    public JDBCHandler() {
        this.connection = null;
    }

    public String connect(String dbPropertiesFile, String userPropertiesFile)
            throws IOException, SQLException, IllegalArgumentException {

        // Load database properties
        Properties dbProperties = loadProperties(dbPropertiesFile);
        databaseUrl = dbProperties.getProperty("MYSQL_DB_URL");
        if (databaseUrl == null) {
            throw new IllegalArgumentException("Database URL not found in properties file: " + dbPropertiesFile);
        }

        // Load user credentials
        Properties userProperties = loadProperties(userPropertiesFile);
        username = userProperties.getProperty("MYSQL_DB_USERNAME");
        password = userProperties.getProperty("MYSQL_DB_PASSWORD");

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password not found in properties file: " + userPropertiesFile);
        }

        // Debugging: Print connection details
        System.out.println("Connecting to database...");
        System.out.println("URL: " + databaseUrl);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Explicitly register the MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        // Establish connection
        connection = DriverManager.getConnection(databaseUrl, username, password);
        System.out.println("Connected to database: " + databaseUrl);
        return databaseUrl;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to database.");
        }
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    public int executeUpdate(String query) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to database.");
        }
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(query);
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("Properties/" + filename)) {
            if (input == null) {
                throw new FileNotFoundException("Properties file not found: " + filename);
            }
            properties.load(input);
        }
        return properties;
    }
    public void updateOperationsCount(String username, boolean isUpdate) {
        Connection operationsLogConnection = null;
        try {
            // Load database properties
            Properties operationsProperties = loadProperties("operationslog.properties");
            String operationsLogUrl = operationsProperties.getProperty("MYSQL_DB_URL");

            // Create a new connection to the operationslog database
            operationsLogConnection = DriverManager.getConnection(operationsLogUrl, this.username, this.password);

            // Prepare the query
            String query;
            if (isUpdate) {
                query = "INSERT INTO operationscount (login_username, num_queries, num_updates) " +
                        "VALUES (?, 0, 1) " +
                        "ON DUPLICATE KEY UPDATE num_updates = num_updates + 1";
            } else {
                query = "INSERT INTO operationscount (login_username, num_queries, num_updates) " +
                        "VALUES (?, 1, 0) " +
                        "ON DUPLICATE KEY UPDATE num_queries = num_queries + 1";
            }

            // Execute the query
            try (PreparedStatement pstmt = operationsLogConnection.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (operationsLogConnection != null) {
                try {
                    operationsLogConnection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
