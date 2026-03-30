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

public class ResultServlet extends HttpServlet {

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
        response.getWriter().print("ResultServlet is running");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(request, response);
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter pw = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("student_id") == null) {
            pw.print("[]");
            return;
        }

        int studentId = (Integer) session.getAttribute("student_id");

        String sql = "SELECT alphabet, correct_count, wrong_count, score FROM quiz_score WHERE student_id = ? ORDER BY alphabet";

        try (
            Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder json = new StringBuilder();
                json.append("[");

                boolean first = true;

                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;

                    String alphabet = rs.getString("alphabet");
                    if (alphabet == null) {
                        alphabet = "";
                    }

                    alphabet = alphabet.replace("\\", "\\\\").replace("\"", "\\\"");

                    json.append("{");
                    json.append("\"alphabet\":\"").append(alphabet).append("\",");
                    json.append("\"correct_count\":").append(rs.getInt("correct_count")).append(",");
                    json.append("\"wrong_count\":").append(rs.getInt("wrong_count")).append(",");
                    json.append("\"score\":").append(rs.getInt("score"));
                    json.append("}");
                }

                json.append("]");
                pw.print(json.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("[]");
        }
    }
}