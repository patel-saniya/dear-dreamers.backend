package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");

        if ("https://dear-dreamers-frontend.vercel.app".equals(origin)
                || "https://dear-dreamers-frontend-cm9l-hi91fl1r9.vercel.app".equals(origin)
                || "http://localhost:3000".equals(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
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
        response.getWriter().print("LoginServlet is running");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(request, response);
        response.setContentType("text/plain;charset=UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            response.getWriter().print("Email and password are required");
            return;
        }

        String sql = "SELECT student_id FROM students WHERE email = ? AND s_password = ?";

        try (
            Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, email.trim());
            ps.setString(2, password.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");

                    HttpSession session = request.getSession(true);
                    session.setAttribute("student_id", studentId);

                    response.getWriter().print("Login successful");
                } else {
                    response.getWriter().print("Invalid Email or Password");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().print("Error: " + ex.getMessage());
        }
    }
}