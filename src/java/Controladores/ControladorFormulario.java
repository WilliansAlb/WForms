/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Analizadores.LexerALM;
import Analizadores.parserALM;
import POJOS.Componente;
import POJOS.Formulario;
import POJOS.Usuario;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author willi
 */
public class ControladorFormulario {

    ArrayList<Usuario> usuariosDB;
    ArrayList<Formulario> formsDB;
    private String usuarioActual;

    public ControladorFormulario() {
    }

    public String html(Formulario encontrado) {
        String retorno = " ";
        String contenedor = "contenedor" + encontrado.getTema();
        String componente = "comp" + encontrado.getTema();
        retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                + "<h3>DATOS DEL FORMULARIO</h3><p><span class=\"nombre_param\">TITULO:</span> "
                + "<span class=\"param\">"+encontrado.getTitulo()+"</span></p><p><span class=\"nombre_param\">NOMBRE:</span> "
                + "<span class=\"param\">"+encontrado.getNombre()+"</span></p>"
                + "<p><span class=\"nombre_param\">CREADOR:</span> "
                + "<span class=\"param\">"+encontrado.getUsuario()+"</span></p><p><span class=\"nombre_param\">CREADO:</span> "
                + "<span class=\"param\">"+encontrado.getFecha()+"</span></p><p><span class=\"nombre_param\">ID:</span> "
                + "<span class=\"param\">"+encontrado.getId()+"</span></p><p><span class=\"nombre_param\">TEMA:</span> "
                + "<span class=\"param\">"+encontrado.getTema()+"</span></p></div></div>";
        ArrayList<Componente> comps = encontrado.getComponentes();
        for (int i = 0; i < comps.size(); i++) {
            Componente analizado = comps.get(i);
            String alineacion = "";
            if (!analizado.getAlineacion().isEmpty()) {
                alineacion += " style=\"text-align:";
                switch (analizado.getAlineacion()) {
                    case "CENTRO":
                        alineacion += "center;\" ";
                        break;
                    case "IZQUIERDA":
                        alineacion += "left;\" ";
                        break;
                    case "DERECHA":
                        alineacion += "right;\" ";
                        break;
                    case "JUSTIFICAR":
                        alineacion += "justify;\" ";
                        break;
                    default:
                        break;
                }
            }
            String requerido = "";
            if (!analizado.getRequerido().isEmpty()) {
                if (!analizado.getRequerido().equals("NO")) {
                    requerido = " required ";
                }
            }
            switch (analizado.getClase()) {
                case "BOTON":
                    retorno += "<div class=\"contenedor " + contenedor + "\">"
                            + "<div class=\"componente " + componente + "\"><input type=\"submit\" value=\"" + analizado.getTexto_visible() + "\"></div></div>";
                    break;
                case "IMAGEN":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId()+ "\"><img src=\"" + analizado.getUrl() + "\"></div></div>";
                    break;
                case "CAMPO_TEXTO":

                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId()
                            + "\">" + analizado.getTexto_visible() + "</label><input " + alineacion + " type=\"text\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" " + requerido + "></div></div>";
                    break;
                case "AREA_TEXTO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId()+ "\">"+analizado.getTexto_visible()+"</label><textarea "+alineacion+" id=\"" + analizado.getId() + "\" rows=\"" + analizado.getFilas() + "\" "
                            + "cols=\"" + analizado.getColumnas() + "\" name=\"" + analizado.getNombre_campo() + "\" "+requerido+"></textarea></div></div>";
                    break;
                case "CHECKBOX":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId()+ "\">"+analizado.getTexto_visible()+"</label>";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<input type=\"checkbox\" id=\""+analizado.getId()+"\" name=\""+analizado.getNombre_campo()+"\" value=\""+analizado.getOpciones().get(j)+"\">"+analizado.getOpciones().get(j);
                    }
                    retorno += "</div></div>";
                    break;
                case "RADIO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId()+ "\">"+analizado.getTexto_visible()+"</label>";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<input type=\"radio\" id=\""+analizado.getId()+"\" name=\""+analizado.getNombre_campo()+"\" value=\""+analizado.getOpciones().get(j)+"\">"+analizado.getOpciones().get(j);
                    }
                    retorno += "</div></div>";
                    break;
                case "COMBO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId()+ "\">"+analizado.getTexto_visible()+"</label>"
                            + "<select id=\""+analizado.getId()+"\" name=\""+analizado.getNombre_campo()+"\" >";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<option>"+analizado.getOpciones().get(j)+"</option>";
                    }
                    retorno += "</select></div></div>";
                    break;
                case "FICHERO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId()+ "\">"+analizado.getTexto_visible()+"</label><input " + alineacion + " type=\"file\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" " + requerido + "></div></div>";
                    break;
                default:
                    retorno += "<div class=\"contenedor " + contenedor + "\"><h1>ERRORSOTE</h1></div>";
                    break;
            }

        }
        if (retorno.isEmpty()) {
            retorno += "<div class=\"contenedor " + contenedor + "\"><h1>ERRORSOTE</h1></div>";
        }
        return retorno;
    }

    public ArrayList<Formulario> tusFormularios(String usuario) throws FileNotFoundException {
        ArrayList<Formulario> retorno = new ArrayList<>();
        formsDB = new ArrayList<>();
        listado_formularios();
        for (int i = 0; i < formsDB.size(); i++) {
            if (formsDB.get(i).getUsuario().equals(usuario)) {
                retorno.add(formsDB.get(i));
            }
        }
        return retorno;
    }

    public void listado_formularios() throws FileNotFoundException {
        String rutaArchivos = "C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/formularios.txt";
        File nuevo = new File(rutaArchivos);
        parserALM par = new parserALM(new LexerALM(new FileReader(nuevo)));
        try {
            par.parse();
            formsDB = par.listado_formularios;
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
    }

    public Formulario obtener(String id) throws FileNotFoundException {
        Formulario temp = new Formulario();
        listado_formularios();
        for (int i = 0; i < formsDB.size(); i++) {
            if (formsDB.get(i).getId().equals(id)) {
                temp = formsDB.get(i);
                break;
            }
        }
        return temp;
    }
}
