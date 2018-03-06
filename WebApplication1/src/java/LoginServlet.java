/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author webapp
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    

    protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get request parameters for userID and password
        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");
	   
        //Encrypt data at idle state
        EncryptHelper e = new EncryptHelper();
        String encrypt_user = e.encrypt(user);
        String encrypt_pass = e.encrypt(pwd);

                
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String myConnStr = new MySQLConnString().getMySQLConnString();
            Connection conn = DriverManager.getConnection(myConnStr); //MySQL connect string
            
            
            //Avoid SQL injection use prepared SQL statement 
            PreparedStatement pst = conn.prepareStatement("Select username,password from user where username=? and password=?");
            pst.setString(1, encrypt_user);
            pst.setString(2, encrypt_pass);
            ResultSet rs = pst.executeQuery();
            
            
            if (rs.next()){
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                //setting session to expiry in 30 mins
                session.setMaxInactiveInterval(30*60);
                Cookie userName = new Cookie("user", user);
                
                //Some security parameters for the cookie
                    //max age 30 min 
                    // httponly = true
                    
                userName.setMaxAge(30*60);
                userName.setHttpOnly(true);
                response.addCookie(userName);
                
                
                String sessionID = session.getId();
                String otp = new OTPGenerator().toString();
                
                if (PersistSession(sessionID, otp, encrypt_user)){
                    response.sendRedirect("validateOTP.jsp");
                } else {
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
                PrintWriter out= response.getWriter();
                out.println("<font color=red>Error Occured while Creating Session</font>");
                rd.include(request, response);
                }
            }
            else {
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
                PrintWriter out= response.getWriter();
                out.println("<font color=red>Authentication failed.</font>");
                rd.include(request, response);
            }
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean PersistSession (String sessionID, String otp, String user) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String myConnStr = new MySQLConnString().getMySQLConnString();
            Connection conn = DriverManager.getConnection(myConnStr); //MySQL connect string
            
            //We encrypt the session ID for extra security.
            EncryptHelper e = new EncryptHelper();
            sessionID = e.encrypt(sessionID);
            
                            
             String sql = "INSERT INTO session (session, otp, user) VALUES (?, ?, ?)";
             
 
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, sessionID);
                pst.setString(2, otp);
		pst.setString(3, user);
                
                
                int rowsUpdated = pst.executeUpdate();
                
                if (rowsUpdated > 0) { 
                    return true; 
                } else {
                     return false;
                }

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
