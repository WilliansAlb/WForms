/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Analizadores.Lexer;
import Analizadores.parser;
import Controladores.ControladorUsuario;
import POJOS.Solicitud;
import POJOS.Usuario;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author willi
 */
@WebServlet(name = "Creacion", urlPatterns = {"/Creacion"})
public class Creacion extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Creacion</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Creacion at " + request.getContextPath() + "</h1>");
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
        response.setContentType("application/json");
        String parametro = request.getParameter("entrada");
        String parametro2 = request.getParameter("usuario");
        ControladorUsuario control = new ControladorUsuario();
        String pruebaFunciona = "";
        Map<String, String> respuestas = new HashMap<>();
        if (parametro2 != null) {
            if (!parametro2.isEmpty()) {
                pruebaFunciona = control.analizarSolicitudes(parametro, parametro2);
                if (control.tieneError) {
                    respuestas.put("errores", control.errorades);
                } else {
                    respuestas.put("usuario", parametro2);
                    respuestas.put("respuesta", pruebaFunciona);
                    if (!control.consultades.isEmpty()) {
                        respuestas.put("reportes", control.consultades);
                    }
                }
            } else {
                pruebaFunciona = control.analizarSolicitudes(parametro);
                if (control.tieneError) {
                    respuestas.put("errores", control.errorades);
                } else {
                    if (control.getUsuarioActual().isEmpty()) {
                        respuestas.put("ERROR", pruebaFunciona);
                    } else {
                        respuestas.put("usuario", control.getUsuarioActual());
                        respuestas.put("respuesta", pruebaFunciona);
                        if (!control.consultades.isEmpty()) {
                            respuestas.put("reportes", control.consultades);
                        }
                    }
                }
            }
        }
        //respuestas.put("respuesta", control.dePrueba(parametro, "ozymandias"));
        //control.listado_datos();
        String jsonString = new Gson().toJson(respuestas);
        response.getWriter().write(jsonString);
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
