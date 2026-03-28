package DearDreamersBackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection() throws Exception {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUser == null || dbPass == null
                || dbUrl.trim().isEmpty()
                || dbUser.trim().isEmpty()
                || dbPass.trim().isEmpty()) {
            throw new SQLException("Database environment variables are missing.");
        }

        dbUrl = dbUrl.trim();

        // If user accidentally stored "DB_URL=..." in Render
        if (dbUrl.startsWith("DB_URL=")) {
            dbUrl = dbUrl.substring(7);
        }

        // If Railway URL is mysql://..., convert it to jdbc:mysql://...
        if (dbUrl.startsWith("mysql://")) {
            dbUrl = "jdbc:" + dbUrl;
        }

        // Add params if not already present
        if (dbUrl.startsWith("jdbc:mysql://") && !dbUrl.contains("?")) {
            dbUrl = dbUrl + "?useSSL=false&serverTimezone=UTC";
        } else if (dbUrl.startsWith("jdbc:mysql://") && dbUrl.contains("?")) {
            if (!dbUrl.contains("useSSL=")) {
                dbUrl = dbUrl + "&useSSL=false";
            }
            if (!dbUrl.contains("serverTimezone=")) {
                dbUrl = dbUrl + "&serverTimezone=UTC";
            }
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }
}