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

public class ResultServlet extends HttpServlet {

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
        response.getWriter().print("ResultServlet is running");
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
            pw.print("[]");
            return;
        }

        int studentId = (int) session.getAttribute("student_id");

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();

            ps = con.prepareStatement(
                    "SELECT alphabet, correct_count, wrong_count, score FROM quiz_score WHERE student_id = ?"
            );
            ps.setInt(1, studentId);
            rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
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

        } catch (Exception e) {
            e.printStackTrace();
            pw.print("[]");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}