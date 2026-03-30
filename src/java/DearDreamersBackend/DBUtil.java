package DearDreamersBackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection() throws Exception {

        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");

        // 🔍 DEBUG LOGS (temporary - remove later)
        System.out.println("DEBUG DB_URL = " + dbUrl);
        System.out.println("DEBUG DB_USER = " + dbUser);
        System.out.println("DEBUG DB_PASSWORD exists = " + (dbPass != null));

        // ❌ Check missing values
        if (dbUrl == null || dbUser == null || dbPass == null
                || dbUrl.trim().isEmpty()
                || dbUser.trim().isEmpty()
                || dbPass.trim().isEmpty()) {

            throw new SQLException("Database environment variables are missing. Set DB_URL, DB_USER and DB_PASSWORD.");
        }

        dbUrl = dbUrl.trim();
        dbUser = dbUser.trim();
        dbPass = dbPass.trim();

        // ❌ If someone mistakenly saved "DB_URL=..."
        if (dbUrl.startsWith("DB_URL=")) {
            dbUrl = dbUrl.substring(7).trim();
        }

        // ❌ If still contains ${DB_URL} → wrong config
        if (dbUrl.contains("${")) {
            throw new SQLException("DB_URL is not resolved properly. Found: " + dbUrl);
        }

        // 🔄 Convert Railway format
        if (dbUrl.startsWith("mysql://")) {
            dbUrl = "jdbc:" + dbUrl;
        }

        // ✅ Ensure proper MySQL JDBC format
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
            throw new SQLException("Invalid DB_URL format: " + dbUrl);
        }

        // ✅ Load MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // ✅ Create connection
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }
}