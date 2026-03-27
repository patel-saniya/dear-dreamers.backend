package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ResultServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ResultServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResultServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Method", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("student_id") == null) {
            pw.print("[]");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        try {
             // 🔹 Load config.properties
            Properties prop = new Properties();
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            prop.load(input);

            String dbUrl = prop.getProperty("db.url");
            String dbUser = prop.getProperty("db.username");
            String dbPass = prop.getProperty("db.password");

            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            Connection  con = DriverManager.getConnection(dbUrl, dbUser, dbPass);

            PreparedStatement ps = con.prepareStatement(
                    "SELECT alphabet, correct_count, wrong_count, score FROM quiz_score WHERE student_id = ?"
            );

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"alphabet\":\"").append(rs.getString("alphabet")).append("\",");
                json.append("\"correct_count\":").append(rs.getInt("correct_count")).append(",");
                json.append("\"wrong_count\":").append(rs.getInt("wrong_count")).append(",");
                json.append("\"score\":").append(rs.getInt("score"));
                json.append("}");
            }

            json.append("]");

            pw.print(json.toString());

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("[]");
        }
    }
}

