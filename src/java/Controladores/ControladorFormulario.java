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
import POJOS.Ingreso;
import POJOS.Registro;
import POJOS.Usuario;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author willi
 */
public class ControladorFormulario {

    ArrayList<Usuario> usuariosDB;
    ArrayList<Formulario> formsDB;
    ArrayList<Formulario> datosDB;
    private String usuarioActual;

    public ControladorFormulario() {
    }

    public String html(Formulario encontrado) {
        String retorno = " ";
        String contenedor = "contenedor" + encontrado.getTema().toUpperCase();
        String componente = "comp" + encontrado.getTema().toUpperCase();
        retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                + "<h3>DATOS DEL FORMULARIO</h3><p><span class=\"nombre_param\">TITULO:</span> "
                + "<span class=\"param\">" + encontrado.getTitulo() + "</span></p><p><span class=\"nombre_param\">NOMBRE:</span> "
                + "<span class=\"param\">" + encontrado.getNombre() + "</span></p>"
                + "<p><span class=\"nombre_param\">CREADOR:</span> "
                + "<span class=\"param\">" + encontrado.getUsuario() + "</span></p><p><span class=\"nombre_param\">CREADO:</span> "
                + "<span class=\"param\">" + encontrado.getFecha() + "</span></p><p><span class=\"nombre_param\">ID:</span> "
                + "<span class=\"param\">" + encontrado.getId() + "</span></p><p><span class=\"nombre_param\">TEMA:</span> "
                + "<span class=\"param\">" + encontrado.getTema() + "</span></p></div></div>";
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
            String para_requerido = "";
            if (!analizado.getRequerido().isEmpty()) {
                if (!analizado.getRequerido().equals("NO")) {
                    requerido = " required ";
                    para_requerido = "*";
                }
            }
            retorno += "<form id=\"formulario\" method=\"POST\" action=\"Ingresar\">";
            switch (analizado.getClase()) {
                case "BOTON":
                    retorno += "<div class=\"contenedor " + contenedor + "\">"
                            + "<div class=\"componente " + componente + "\"><input type=\"submit\" value=\"" + analizado.getTexto_visible() + "\"></div></div>";
                    break;
                case "IMAGEN":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId() + "\"><img src=\"" + analizado.getUrl() + "\"></div></div>";
                    break;
                case "CAMPO_TEXTO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId()
                            + "\">" + analizado.getTexto_visible()+para_requerido + "</label><input " + alineacion + " type=\"text\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" " + requerido + "></div></div>";
                    break;
                case "AREA_TEXTO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId() + "\">" + analizado.getTexto_visible()+para_requerido + "</label><textarea " + alineacion + " id=\"" + analizado.getId() + "\" rows=\"" + analizado.getFilas() + "\" "
                            + "cols=\"" + analizado.getColumnas() + "\" name=\"" + analizado.getNombre_campo() + "\" " + requerido + "></textarea></div></div>";
                    break;
                case "CHECKBOX":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId() + "\">" + analizado.getTexto_visible()+para_requerido + "</label><div class=\"paraRadio\">";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<input type=\"checkbox\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" value=\"" + analizado.getOpciones().get(j) + "\">" + analizado.getOpciones().get(j);
                    }
                    retorno += "</div></div></div>";
                    break;
                case "RADIO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId() + "\">" + analizado.getTexto_visible()+para_requerido + "</label><div class=\"paraRadio\">";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<input type=\"radio\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" value=\"" + analizado.getOpciones().get(j) + "\">" + analizado.getOpciones().get(j);
                        retorno += "<br>";
                    }
                    retorno += "</div></div></div>";
                    break;
                case "COMBO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\"><label for=\"" + analizado.getId() + "\">" + analizado.getTexto_visible()+ para_requerido + "</label>"
                            + "<select id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" >";
                    for (int j = 0; j < analizado.getOpciones().size(); j++) {
                        retorno += "<option>" + analizado.getOpciones().get(j) + "</option>";
                    }
                    retorno += "</select></div></div>";
                    break;
                case "FICHERO":
                    retorno += "<div class=\"contenedor " + contenedor + "\"><div class=\"componente " + componente + "\">"
                            + "<label for=\"" + analizado.getId() + "\">" + analizado.getTexto_visible()+para_requerido + "</label><input " + alineacion + " type=\"file\" id=\"" + analizado.getId() + "\" name=\"" + analizado.getNombre_campo() + "\" " + requerido + "></div></div>";
                    break;
                default:
                    retorno += "<div class=\"contenedor " + contenedor + "\"><h1>ERRORSOTE</h1></div>";
                    break;
            }

        }
        retorno += "</form>";
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

    public void listado_datos() throws FileNotFoundException {
        String rutaArchivos = "C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/datos.txt";
        File nuevo = new File(rutaArchivos);
        parserALM par = new parserALM(new LexerALM(new FileReader(nuevo)));
        try {
            par.parse();
            datosDB = par.listado_datos;
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
    }

    public String ingresarDatos(Registro[] re, String id, String nombre) {
        String retorno = "";
        try {
            listado_datos();
        } catch (FileNotFoundException ex) {
            System.out.println("error en ingresar datos por: " + ex.toString());
        }
        ArrayList<Ingreso> ingresos = new ArrayList<>();
        for (int i = 0; i < re.length; i++) {
            ingresos.add(new Ingreso(re[i].getId(), re[i].getNombre(), re[i].getRegistro()));
        }
        if (!datosDB.isEmpty()) {
            int pos = -1;
            for (int j = 0; j < datosDB.size(); j++) {
                if (datosDB.get(j).getId().equals(id)) {
                    pos = j;
                    break;
                }
            }
            if (pos != -1) {
                int registro = datosDB.get(pos).getRegistros().size() + 1;
                String nombre_re = "REGISTRO_" + registro;
                Registro nuevore = new Registro(nombre_re, ingresos);
                datosDB.get(pos).getRegistros().add(nuevore);
                retorno += "SE AGREGARON LOS DATOS NUEVOS AL CONJUNTO DE DATOS DEL FORMULARIO";
            } else {
                String nombre_re = "REGISTRO_1";
                Registro nuevore = new Registro(nombre_re, ingresos);
                Formulario nuevofo = new Formulario();
                nuevofo.setId(id);
                nuevofo.setNombre(nombre);
                nuevofo.getRegistros().add(nuevore);
                datosDB.add(nuevofo);
                retorno+="SE AGREGO EL NUEVO ESPACIO PARA LOS DATOS DEL FORMULARIO";
            }
        } else {
            String nombre_re = "REGISTRO_1";
            Registro nuevore = new Registro(nombre_re, ingresos);
            Formulario nuevofo = new Formulario();
            nuevofo.setId(id);
            nuevofo.setNombre(nombre);
            nuevofo.getRegistros().add(nuevore);
            datosDB.add(nuevofo);
            retorno+="SE INICIO LA BASE DE DATOS";
        }
        if(retorno.isEmpty()){
            retorno+="ERROR";
        } else {
            actualizarDatos();
        }
        return retorno;
    }

    public void actualizarFormularios() {
        try (FileWriter fw = new FileWriter("C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/formularios.txt", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println("db.formularios(");
            for (int i = 0; i < formsDB.size(); i++) {
                Formulario temp = formsDB.get(i);
                out.println("\t{");
                out.println("\t\t\"ID_FORM\":\"" + temp.getId() + "\",");
                out.println("\t\t\"TITULO\":\"" + temp.getTitulo() + "\",");
                out.println("\t\t\"NOMBRE\":\"" + temp.getNombre() + "\",");
                out.println("\t\t\"TEMA\":\"" + temp.getTema() + "\",");
                out.println("\t\t\"USUARIO_CREACION\":\"" + temp.getUsuario() + "\",");
                out.println("\t\t\"FECHA_CREACION\":\"" + temp.getFecha() + "\",");
                if (!formsDB.get(i).getComponentes().isEmpty()) {
                    out.println("\t\t\"COMPONENTES\":(");
                    ArrayList<Componente> comps = formsDB.get(i).getComponentes();
                    int conteo = 0;
                    for (Componente compt : comps) {
                        String posibles = "";
                        out.println("\t\t{");
                        out.println("\t\t\t\"ID_COMP\":\"" + compt.getId() + "\",");
                        if (!compt.getNombre_campo().isEmpty()) {
                            out.println("\t\t\t\"NOMBRE_CAMPO\":\"" + compt.getNombre_campo() + "\",");
                        }
                        out.println("\t\t\t\"CLASE\":\"" + compt.getClase() + "\",");
                        out.println("\t\t\t\"TEXTO_VISIBLE\":\"" + compt.getTexto_visible() + "\",");
                        posibles += "\t\t\t\"INDICE\":\"" + (conteo) + "\",\n";
                        if (!compt.getAlineacion().isEmpty()) {
                            posibles += "\t\t\t\"ALINEACION\":\"" + compt.getAlineacion() + "\",\n";
                        }
                        if (!compt.getRequerido().isEmpty()) {
                            posibles += "\t\t\t\"REQUERIDO\":\"" + compt.getRequerido() + "\",\n";
                        }
                        if (!compt.getOpciones().isEmpty()) {
                            posibles += "\t\t\t\"OPCIONES\" : \"";
                            for (int j = 0; j < compt.getOpciones().size(); j++) {
                                posibles += compt.getOpciones().get(j);
                                if ((j + 1) != compt.getOpciones().size()) {
                                    posibles += "|";
                                }
                            }
                            posibles += "\",\n";
                        }
                        if (compt.getFilas() != -1) {
                            posibles += "\t\t\t\"FILAS\":\"" + compt.getFilas() + "\",\n";
                        }
                        if (compt.getColumnas() != -1) {
                            posibles += "\t\t\t\"COLUMNAS\":\"" + compt.getColumnas() + "\",\n";
                        }
                        if (!compt.getUrl().isEmpty()) {
                            posibles += "\t\t\t\"URL\":\"" + compt.getUrl() + "\",\n";
                        }
                        out.println(posibles.substring(0, posibles.length() - 2));
                        if ((conteo + 1) == comps.size()) {
                            out.println("\t\t}");
                        } else {
                            out.println("\t\t},");
                        }
                        conteo++;
                    }
                    out.println("\t\t)");
                } else {
                    out.println("\t\t\"COMPONENTES\":()");
                }

                if (i + 1 != formsDB.size()) {
                    out.println("\t},");
                } else {
                    out.println("\t}");
                }
            }
            out.print(")");
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public void actualizarDatos() {
        try (FileWriter fw = new FileWriter("C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/datos.txt", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println("db.datos(");
            for (int i = 0; i < datosDB.size(); i++) {
                Formulario temp = datosDB.get(i);
                out.println("\t{");
                out.println("\t\t\"ID_FORM\":\"" + temp.getId() + "\",");
                out.println("\t\t\"NOMBRE\":\"" + temp.getNombre() + "\",");
                if (!datosDB.get(i).getRegistros().isEmpty()) {
                    out.println("\t\t\"REGISTROS\":(");
                    ArrayList<Registro> comps = datosDB.get(i).getRegistros();
                    int conteo = 0;
                    for (Registro compt : comps) {
                        String posibles = "";
                        out.println("\t\t{");
                        out.println("\t\t\t\""+compt.getNoregistro()+"\":(");
                        ArrayList<Ingreso> ings = compt.getValores();
                        for (int j = 0; j < ings.size(); j++) {
                            Ingreso in = ings.get(j);
                            out.println("\t\t\t\t{");
                            out.println("\t\t\t\t\t\"ID_COMP\":\""+in.getIdc()+"\",");
                            out.println("\t\t\t\t\t\"NOMBRE_CAMPO\":\""+in.getNombrec()+"\",");
                            out.println("\t\t\t\t\t\"VALOR\":\""+in.getDato()+"\"");
                            if ((j+1)==ings.size()){
                                out.println("\t\t\t\t}");
                            } else {
                                out.println("\t\t\t\t},");
                            }
                        }
                        out.println("\t\t\t)");
                        if ((conteo + 1) == comps.size()) {
                            out.println("\t\t}");
                        } else {
                            out.println("\t\t},");
                        }
                        conteo++;
                    }
                    out.println("\t\t)");
                } else {
                    out.println("\t\t\"REGISTROS\":()");
                }

                if ((i + 1) != datosDB.size()) {
                    out.println("\t},");
                } else {
                    out.println("\t}");
                }
            }
            out.print(")");
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
    
    public void escribirParaDescarga(String id) {
        try {
            listado_formularios();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ControladorFormulario.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (FileWriter fw = new FileWriter("C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/formulario.form", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println("new.formulario(");
            for (int i = 0; i < formsDB.size(); i++) {
                Formulario temp = formsDB.get(i);
                if (temp.getId().equals(id)) {
                    out.println("\t{");
                    out.println("\t\t\"ID_FORM\":\"" + temp.getId() + "\",");
                    out.println("\t\t\"TITULO\":\"" + temp.getTitulo() + "\",");
                    out.println("\t\t\"NOMBRE\":\"" + temp.getNombre() + "\",");
                    out.println("\t\t\"TEMA\":\"" + temp.getTema() + "\",");
                    out.println("\t\t\"USUARIO_CREACION\":\"" + temp.getUsuario() + "\",");
                    out.println("\t\t\"FECHA_CREACION\":\"" + temp.getFecha() + "\",");
                    if (!formsDB.get(i).getComponentes().isEmpty()) {
                        out.println("\t\t\"COMPONENTES\":(");
                        ArrayList<Componente> comps = formsDB.get(i).getComponentes();
                        int conteo = 0;
                        for (Componente compt : comps) {
                            String posibles = "";
                            out.println("\t\t{");
                            out.println("\t\t\t\"ID_COMP\":\"" + compt.getId() + "\",");
                            if (!compt.getNombre_campo().isEmpty()) {
                                out.println("\t\t\t\"NOMBRE_CAMPO\":\"" + compt.getNombre_campo() + "\",");
                            }
                            out.println("\t\t\t\"CLASE\":\"" + compt.getClase() + "\",");
                            out.println("\t\t\t\"TEXTO_VISIBLE\":\"" + compt.getTexto_visible() + "\",");
                            posibles += "\t\t\t\"INDICE\":\"" + (conteo+1) + "\",\n";
                            if (!compt.getAlineacion().isEmpty()) {
                                posibles += "\t\t\t\"ALINEACION\":\"" + compt.getAlineacion() + "\",\n";
                            }
                            if (!compt.getRequerido().isEmpty()) {
                                posibles += "\t\t\t\"REQUERIDO\":\"" + compt.getRequerido() + "\",\n";
                            }
                            if (!compt.getOpciones().isEmpty()) {
                                posibles += "\t\t\t\"OPCIONES\" : \"";
                                for (int j = 0; j < compt.getOpciones().size(); j++) {
                                    posibles += compt.getOpciones().get(j);
                                    if ((j + 1) != compt.getOpciones().size()) {
                                        posibles += "|";
                                    }
                                }
                                posibles += "\",\n";
                            }
                            if (compt.getFilas() != -1) {
                                posibles += "\t\t\t\"FILAS\":\"" + compt.getFilas() + "\",\n";
                            }
                            if (compt.getColumnas() != -1) {
                                posibles += "\t\t\t\"COLUMNAS\":\"" + compt.getColumnas() + "\",\n";
                            }
                            if (!compt.getUrl().isEmpty()) {
                                posibles += "\t\t\t\"URL\":\"" + compt.getUrl() + "\",\n";
                            }
                            out.println(posibles.substring(0, posibles.length() - 2));
                            if ((conteo + 1) == comps.size()) {
                                out.println("\t\t}");
                            } else {
                                out.println("\t\t},");
                            }
                            conteo++;
                        }
                        out.println("\t\t)");
                    } else {
                        out.println("\t\t\"COMPONENTES\":()");
                    }
                    out.println("\t}");
                }
            }
            out.print(")");
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
}
