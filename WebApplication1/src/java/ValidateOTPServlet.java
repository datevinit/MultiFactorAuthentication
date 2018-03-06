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
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class ValidateOTPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
        String sessionID = null;
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("JSESSIONID")){
                        sessionID = cookie.getValue();
    			System.out.println("JSESSIONID="+cookie.getValue());
    			break;
    		}
            }
    	}
    	
        if (sessionID != null) {
        EncryptHelper e = new EncryptHelper();
        sessionID = e.encrypt(sessionID);
        String otp = request.getParameter("otp");
        
         try {
            Class.forName("com.mysql.jdbc.Driver");
            String myConnStr = new MySQLConnString().getMySQLConnString();
            Connection conn = DriverManager.getConnection(myConnStr); //MySQL connect string
            
            
            //Avoid SQL injection use prepared SQL statement 
            PreparedStatement pst = conn.prepareStatement("Select session,otp from session where session=? and otp=? and status=?");
            pst.setString(1, sessionID);
            pst.setString(2, otp);
            pst.setString(3, "processed");
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                PreparedStatement pst1 = conn.prepareStatement("Update session set status = ? where session=? and otp=?");
                pst1.setString(1, "done");
                pst1.setString(2, sessionID);
                pst1.setString(3, otp);
                pst1.executeUpdate();
                response.sendRedirect("LoginSuccess.jsp");
            } else {
               // OTP was wrong. to avoid brute force OTP attack we mark the row as failed in db.
                PreparedStatement pst1 = conn.prepareStatement("Update session set status = ? where session=?");
                pst1.setString(1, "failed");
                pst1.setString(2, sessionID);
                pst1.executeUpdate();
                
                //We also destroy the cookie so that it cannot be hijacked for another session.
                HttpSession session = request.getSession(false);
                if(session != null){
                    session.invalidate();
                }
            //response.sendRedirect("login.html");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
            PrintWriter out= response.getWriter();
            out.println("<font color=red>Authentication Failed.</font>");
            rd.include(request, response);
            }
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
            
        
        //RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
        //PrintWriter out= response.getWriter();
         //       out.println(sessionID);
           //     out.println(otp);
             //   rd.include(request, response);
        }
//invalidate the session if exists
    	//HttpSession session = request.getSession(false);
    	//System.out.println("User="+session.getAttribute("user"));
    	//if(session != null){
    	//	session.invalidate();
    	//}
    	//response.sendRedirect("login.html");
    }

}
