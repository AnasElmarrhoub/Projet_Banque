import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages a singleton JDBC connection to the MySQL database.
 * Credentials are loaded from {@code db.properties} (excluded from version control).
 * Copy {@code db.properties.example} to {@code db.properties} and fill in your details.
 */
public class ConnexionBD {

    private static final String PROPERTIES_FILE = "db.properties";

    private static String url;
    private static String user;
    private static String password;

    private static Connection connection = null;

    // Load credentials once from the properties file
    static {
        Properties props = new Properties();
        try (InputStream input = ConnexionBD.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                // Fallback: load from working directory
                try (InputStream fallback = new java.io.FileInputStream(PROPERTIES_FILE)) {
                    props.load(fallback);
                }
            } else {
                props.load(input);
            }
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("⚠  Could not load " + PROPERTIES_FILE + ": " + e.getMessage());
            System.err.println("   Please copy db.properties.example → db.properties and set your credentials.");
        }
    }

    /**
     * Returns a reusable singleton connection.
     * Opens a new connection if the current one is null or closed.
     *
     * @return a valid {@link Connection}, or {@code null} if the connection failed
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println(">>> Connexion BDD ouverte.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : driver MySQL introuvable — " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur Connexion BDD : " + e.getMessage());
        }
        return connection;
    }
}