package DearDreamersBackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUser == null || dbPass == null
                || dbUrl.trim().isEmpty()
                || dbUser.trim().isEmpty()
                || dbPass.trim().isEmpty()) {
            throw new SQLException("Database environment variables are missing. Please set DB_URL, DB_USER, and DB_PASSWORD.");
        }

        dbUrl = dbUrl.trim();
        dbUser = dbUser.trim();
        dbPass = dbPass.trim();

        // If accidentally stored like: DB_URL=mysql://...
        if (dbUrl.startsWith("DB_URL=")) {
            dbUrl = dbUrl.substring("DB_URL=".length()).trim();
        }

        // Convert Railway style mysql://... to jdbc:mysql://...
        if (dbUrl.startsWith("mysql://")) {
            dbUrl = "jdbc:" + dbUrl;
        }

        // If already jdbc url but missing params
        if (dbUrl.startsWith("jdbc:mysql://")) {
            if (!dbUrl.contains("?")) {
                dbUrl += "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            } else {
                if (!dbUrl.contains("useSSL=")) {
                    dbUrl += "&useSSL=false";
                }
                if (!dbUrl.contains("allowPublicKeyRetrieval=")) {
                    dbUrl += "&allowPublicKeyRetrieval=true";
                }
                if (!dbUrl.contains("serverTimezone=")) {
                    dbUrl += "&serverTimezone=UTC";
                }
            }
        } else {
            throw new SQLException("Invalid DB_URL format. Expected MySQL JDBC URL.");
        }

        Class.forName(MYSQL_DRIVER);
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }
}