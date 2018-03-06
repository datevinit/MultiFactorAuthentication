/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author webapp
 */
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.*;
import javax.servlet.*;
import java.sql.*;

/**
 * Servlet implementation class Authenticate
 */

public class Register extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        String repass = request.getParameter("repass");
        String phone = request.getParameter("phone");
        
        if (pass.equals(repass)) {
            EncryptHelper e = new EncryptHelper();
            user = e.encrypt(user);
            pass = e.encrypt(pass);
            //phone = e.encrypt(phone);
            
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String myConnStr = new MySQLConnString().getMySQLConnString();
                Connection conn = DriverManager.getConnection(myConnStr);
                
                String sql = "INSERT INTO user (username, password, phone) VALUES (?, ?, ?)";
 
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, user);
                pst.setString(2, pass);
                pst.setString(3, phone);
                
                int rowsUpdated = pst.executeUpdate();
                if (rowsUpdated > 0) {
                    out.println("<h>Done</h>");
                } 
                else {
                out.println("Not Done");
                }
            } 
            catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            out.println("password don't match");
        }
    }
}
