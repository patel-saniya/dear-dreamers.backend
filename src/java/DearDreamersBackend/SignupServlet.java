package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupServlet extends HttpServlet {

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");

        if (origin != null &&
            (origin.equals("http://localhost:3000")
            || origin.equals("https://dear-dreamers-frontend.vercel.app")
            || origin.endsWith(".vercel.app"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(request, response);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().print("SignupServlet is running");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(request, response);
        response.setContentType("text/plain;charset=UTF-8");

        PrintWriter pw = response.getWriter();

        String name = request.getParameter("name");
        String lname = request.getParameter("lastName");
        String age = request.getParameter("age");
        String email = request.getParameter("email");
        String passW = request.getParameter("password");

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBUtil.getConnection();

            ps = con.prepareStatement(
                "INSERT INTO students(first_name, last_name, age, email, s_password) VALUES (?, ?, ?, ?, ?)"
            );

            ps.setString(1, name);
            ps.setString(2, lname);
            ps.setString(3, age);
            ps.setString(4, email);
            ps.setString(5, passW);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                pw.print("Registration successful!");
            } else {
                pw.print("Registration failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("Error: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}