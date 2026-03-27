package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupServlet extends HttpServlet {

   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SignupServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SignupServlet at " + request.getContextPath() + "</h1>");
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
        //processRequest(request, response);
        
        response.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods","POST");
        response.setHeader("Access-Control-Allow-Headers","Content-Type");
        
        response.setContentType("application/json");
   
        response.setCharacterEncoding("UTF-8");
        
        
        PrintWriter pw=response.getWriter();
        
        String name=request.getParameter("name");
        String lname=request.getParameter("lastName");
        String age=request.getParameter("age");
        String email=request.getParameter("email");
        String passW=request.getParameter("password");
         
        
        Connection con = null;
        PreparedStatement ps =null;
        
         try {

            // Load config.properties
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

            // Create database connection
            con = DriverManager.getConnection(dbUrl, dbUser, dbPass);

            // SQL Query
            ps = con.prepareStatement(
                    "INSERT INTO students(first_name,last_name,age,email,s_password) VALUES(?,?,?,?,?)"
            );

            ps.setString(1, name);
            ps.setString(2, lname);
            ps.setString(3, age);
            ps.setString(4, email);
            ps.setString(5, passW);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                pw.print("{\"message\":\"Registration successful!\"}");
            } else {
                pw.print("{\"message\":\"Registration failed.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("{\"message\":\"Error: " + e.getMessage() + "\"}");
        } finally {
            try{
                if (ps != null) ps.close();
                if (con != null) con.close();
        } catch (SQLException ignored) {}
        }
     
        } 
        
    }
