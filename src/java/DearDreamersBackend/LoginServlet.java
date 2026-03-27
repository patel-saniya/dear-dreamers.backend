package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LoginServlet extends HttpServlet {

   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
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
        response.setHeader("Access-Control-Allow-Credentials","true");
        
        response.setContentType("application/json");
   
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter pw= response.getWriter();
        
        String email = request.getParameter("email");
        String passW=request.getParameter("password");
        
        Connection con = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        
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

            // Create connection
            con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            ps = con.prepareStatement("select * from students where email = ? AND s_password = ?");
            
            ps.setString(1, email);
            ps.setString(2, passW);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                int studentId = rs.getInt("student_id");
                HttpSession session = request.getSession();
                session.setAttribute("student_id", studentId);
                pw.print("Login successful");
            } else{
                pw.print("Invalid Email or Password");
            }
            
        } catch (ClassNotFoundException ex) {
            System.getLogger(LoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (SQLException ex) {
            System.getLogger(LoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps !=null){
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ignored){}
        }
        
    }
}
