/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dacnt.controller;

import dacnt.account.AccountDAO;
import dacnt.account.AccountDTO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Daniel NG
 */
public class RegisterServlet extends HttpServlet {

    private final String REGISTER_PAGE = "register.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String email = request.getParameter("txtEmail");
            String fullname = request.getParameter("txtFullname");
            String password = request.getParameter("txtPassword");
            String phone = request.getParameter("txtPhone");

            if (phone.matches("^.*[a-zA-Z]+.*$")) {
//                System.out.println(phone);
                request.setAttribute("ERROR", "the phone is invalid");
                request.getRequestDispatcher(REGISTER_PAGE)
                        .forward(request, response);
                return;
            }

            int status = 1;
            int role = 0;

            AccountDTO account = AccountDAO.getAccountByEmail(email);
            if (account != null) {
                request.setAttribute("ERROR", "Account has existed!");
                request.getRequestDispatcher(REGISTER_PAGE)
                        .forward(request, response);
                return;
            } // account does not exist in dbs

            if (AccountDAO.insertAccount(email, password, fullname, phone, status, role)) {
                request.setAttribute("email_newAccount", email);
                request.getRequestDispatcher("SendOTP").forward(request, response);
            } else {
                request.setAttribute("ERROR", "Error occurred");
                request.getRequestDispatcher(REGISTER_PAGE)
                        .forward(request, response);
            }
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        } finally {
//            response.sendRedirect(url);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
