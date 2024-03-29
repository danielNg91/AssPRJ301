/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dacnt.controller;

import dacnt.account.AccountDTO;
import dacnt.order.OrderDAO;
import dacnt.order.OrderDTO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author dacng
 */
@WebServlet(name = "ViewOrdersServlet", urlPatterns = {"/ViewOrdersServlet"})
public class ViewOrdersServlet extends HttpServlet {

    private final String PERSONAL_PAGE = "personalPage.jsp";
    private final String LOGIN_PAGE = "login.jsp";

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

        String url = LOGIN_PAGE;

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                request.setAttribute("ERROR", "You must login to view orders");
                return;
            } // end if session != null

            AccountDTO currentUser = (AccountDTO) session.getAttribute("USER");
            if (currentUser == null) {
                request.setAttribute("ERROR", "You must login to view orders");
                return;
            } // end if current user != null

            String email = currentUser.getEmail();

            OrderDAO dao = OrderDAO.getInstance();
            url = PERSONAL_PAGE;
            String action = request.getParameter("action");
            if (action == null) {
                action = "viewOrders";
            }
            if (action.equals("viewOrders")) {
                String category = request.getParameter("category");

                if (category == null) {
                    category = "";
                }

                switch (category.trim()) {
                    case "completed":
                    case "canceled":
                    case "processing":
                        // all 3 situation ==> call dao
                        dao.getOrdersByCategory(email, category.trim());
                        break;
                    default:
                        dao.getOrders(email);
                        break;
                }
            } else if (action.equals("searchOrdersByDate")) {
                // action.equals("searchOrdersByDate")
                String from = request.getParameter("from");
                String to = request.getParameter("to");
                if (from.isEmpty() || to.isEmpty()) {
                    url = "viewOrders";
                    return;
                } else {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date fromDate = new Date(df.parse(from).getTime());
                    Date toDate = new Date(df.parse(to).getTime());
                    dao.getOrdersByDate(email, fromDate, toDate);
                    request.setAttribute("fromDate", fromDate);
                    request.setAttribute("toDate", toDate);
                }

            }

            ArrayList<OrderDTO> orders = dao.getOrdersList();
//            System.out.println(orders);
            request.setAttribute("ORDERS", orders);
//        } catch (NamingException | SQLException | ParseException ex) {
        } catch (NamingException | SQLException | ParseException ex) {
            ex.printStackTrace();
        } finally {
            if (url.equals("viewOrders")) {
                response.sendRedirect(url);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
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
