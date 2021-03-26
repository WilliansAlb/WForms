/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Controladores.ControladorFormulario;
import POJOS.Formulario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author willi
 */
@WebServlet(name = "Ver", urlPatterns = {"/Ver"})
public class Ver extends HttpServlet {

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
        String id = request.getParameter("id");
        ControladorFormulario control = new ControladorFormulario();
        Formulario actual = control.obtener(id);
        response.setContentType("text/html;charset=UTF-8");
        String getHtml = control.html(actual);
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" media=\"screen\" />");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style2.css\" media=\"screen\" />");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"estilos/ver.css\" media=\"screen\" />");
            out.println("<title>Formulario</title>");     
            out.println("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"img/forms.svg\" />");
            out.println("</head>");
            out.println("<body class=\"comp"+actual.getTema()+"\">");
            out.println("<div id=\"blurry-filter\"></div>\n" +
"        <header>\n" +
"            <div>\n" +
"                <article id=\"title\"><span class=\"parent\">"+actual.getId()+"</span><br /><span class=\"name\">WF<a style=\"display:contents;text-decoration: none;color:#84d404;\"><img src=\"img/forms.svg\" style=\"width: 25px;height: 25px;\"></a>RMS</span>\n" +
"                </article>\n" +
"                <article id=\"reference\"><div class=\"cerrar\"><a href=\"http://localhost:8080/WForms/Login?logout=si\" style=\"display:contents;text-decoration: none;\"><img src=\"img/salir.svg\" style=\"width: 25px;height: 25px;\"><span style=\"font-size:12px;\">CERRAR SESION</span></a></div></article>\n" +
"            </div>\n" +
"        </header>");
            out.println("<div class=\"paraFlex\">");
            out.println(getHtml);
            out.println("</div></div>");
            out.println("</body>");
            out.println("</html>");
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
