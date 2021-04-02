/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import Analizadores.Lexer;
import Analizadores.LexerALM;
import Analizadores.parser;
import Analizadores.parserALM;
import POJOS.Solicitud;
import POJOS.Usuario;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java_cup.runtime.Symbol;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author willi
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

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
            out.println("<title>Servlet Login</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Login at " + request.getContextPath() + "</h1>");
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
        String logout = request.getParameter("logout");
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        if (logout != null) {
            HttpSession temp = request.getSession();
            temp.setAttribute("USUARIO", null);
            response.sendRedirect("http://localhost:80/WForms/");
        } else {
            if (usuario != null) {
                response.setContentType("text/plain");
                if (password != null) {
                    ArrayList<Usuario> listado = usuario();
                    String correcto = "ERROR";
                    
                    for (int i = 0; i < listado.size(); i++) {
                        Usuario temp = listado.get(i);
                        if (temp.getUsuario().equals(usuario) && temp.getPassword().equals(password)) {
                            HttpSession temp2 = request.getSession();
                            temp2.setAttribute("USUARIO", temp.getUsuario());
                            correcto = "USUARIO";
                            break;
                        }
                    }
                    if (correcto.equals("ERROR")) {
                        if (usuario.equals("admin") && password.equals("123")) {
                            HttpSession temp2 = request.getSession();
                            temp2.setAttribute("USUARIO", "admin");
                            correcto = "USUARIO";
                        }
                    }
                    response.getWriter().write(correcto);
                } else {
                    response.getWriter().write("ERROR");
                }
            } else {
                response.sendRedirect("http://localhost:80/WForms/forms.jsp");
            }
        }
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
        processRequest(request,response);
    }
    
    public ArrayList<Usuario> usuario() throws FileNotFoundException {
        Path rutaSym = Paths.get("usuarios.txt");
        if (Files.exists(rutaSym)) {
            String rutaArchivos = "usuarios.txt";
            File nuevo = new File(rutaArchivos);
            parserALM par = new parserALM(new LexerALM(new FileReader(nuevo)));
            ArrayList<Usuario> halla = new ArrayList<>();
            try {
                par.parse();
                halla = par.listado_usuarios;
            } catch (Exception ex) {
                System.out.println("Error por: " + ex.toString());
            }
            return halla;
        } else {
            return new ArrayList<>();
        }
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
