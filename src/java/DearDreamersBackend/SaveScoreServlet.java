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
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter pw = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("student_id") == null) {
            pw.print("{\"message\":\"Not logged in\"}");
            return;
        }

        int studentId = (Integer) session.getAttribute("student_id");

        String alphabet = request.getParameter("alphabet");
        String correctCountStr = request.getParameter("correct_count");
        String wrongCountStr = request.getParameter("wrong_count");
        String scoreStr = request.getParameter("score");

        if (alphabet == null || correctCountStr == null || wrongCountStr == null || scoreStr == null
                || alphabet.trim().isEmpty()) {
            pw.print("{\"message\":\"Missing required fields\"}");
            return;
        }

        int correctCount;
        int wrongCount;
        int score;

        try {
            correctCount = Integer.parseInt(correctCountStr);
            wrongCount = Integer.parseInt(wrongCountStr);
            score = Integer.parseInt(scoreStr);
        } catch (NumberFormatException e) {
            pw.print("{\"message\":\"Invalid numeric values\"}");
            return;
        }

        String checkSql = "SELECT student_id FROM quiz_score WHERE student_id = ? AND alphabet = ?";
        String insertSql = "INSERT INTO quiz_score (student_id, alphabet, correct_count, wrong_count, score) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE quiz_score SET correct_count = ?, wrong_count = ?, score = ? WHERE student_id = ? AND alphabet = ?";

        try (
            Connection con = DBUtil.getConnection();
            PreparedStatement checkPs = con.prepareStatement(checkSql)
        ) {
            checkPs.setInt(1, studentId);
            checkPs.setString(2, alphabet.trim());

            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                        updatePs.setInt(1, correctCount);
                        updatePs.setInt(2, wrongCount);
                        updatePs.setInt(3, score);
                        updatePs.setInt(4, studentId);
                        updatePs.setString(5, alphabet.trim());
                        updatePs.executeUpdate();
                    }
                    pw.print("{\"message\":\"Score updated successfully\"}");
                } else {
                    try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                        insertPs.setInt(1, studentId);
                        insertPs.setString(2, alphabet.trim());
                        insertPs.setInt(3, correctCount);
                        insertPs.setInt(4, wrongCount);
                        insertPs.setInt(5, score);
                        insertPs.executeUpdate();
                    }
                    pw.print("{\"message\":\"Score inserted successfully\"}");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("{\"message\":\"Error saving score: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}