package DearDreamersBackend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SaveScoreServlet extends HttpServlet {

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
        response.getWriter().print("SaveScoreServlet is running");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(request, response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            pw.print("{\"message\":\"Not logged in\"}");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        String alphabet = request.getParameter("alphabet");
        int correctCount = Integer.parseInt(request.getParameter("correct_count"));
        int wrongCount = Integer.parseInt(request.getParameter("wrong_count"));
        int score = Integer.parseInt(request.getParameter("score"));

        Connection con = null;
        PreparedStatement checkPs = null;
        PreparedStatement insertPs = null;
        PreparedStatement updatePs = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();

            checkPs = con.prepareStatement(
                    "SELECT student_id FROM quiz_score WHERE student_id = ? AND alphabet = ?"
            );
            checkPs.setInt(1, studentId);
            checkPs.setString(2, alphabet);

            rs = checkPs.executeQuery();

            if (rs.next()) {
                updatePs = con.prepareStatement(
                        "UPDATE quiz_score SET correct_count = ?, wrong_count = ?, score = ? WHERE student_id = ? AND alphabet = ?"
                );
                updatePs.setInt(1, correctCount);
                updatePs.setInt(2, wrongCount);
                updatePs.setInt(3, score);
                updatePs.setInt(4, studentId);
                updatePs.setString(5, alphabet);

                updatePs.executeUpdate();
                pw.print("{\"message\":\"Score updated successfully\"}");
            } else {
                insertPs = con.prepareStatement(
                        "INSERT INTO quiz_score (student_id, alphabet, correct_count, wrong_count, score) VALUES (?, ?, ?, ?, ?)"
                );
                insertPs.setInt(1, studentId);
                insertPs.setString(2, alphabet);
                insertPs.setInt(3, correctCount);
                insertPs.setInt(4, wrongCount);
                insertPs.setInt(5, score);

                insertPs.executeUpdate();
                pw.print("{\"message\":\"Score inserted successfully\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("{\"message\":\"Error: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkPs != null) checkPs.close();
                if (insertPs != null) insertPs.close();
                if (updatePs != null) updatePs.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}