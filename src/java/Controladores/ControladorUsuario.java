/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Analizadores.Lexer;
import Analizadores.LexerALM;
import Analizadores.parser;
import Analizadores.parserALM;
import POJOS.Formulario;
import POJOS.Solicitud;
import POJOS.Usuario;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author willi
 */
public class ControladorUsuario {

    ArrayList<Usuario> usuariosDB;
    ArrayList<Formulario> formsDB;

    public ControladorUsuario() {

    }

    public String analizarSolicitudes(String texto, String usuario) throws FileNotFoundException {
        parser par = new parser(new Lexer(new StringReader(texto)));
        usuariosDB = listado_usuarios();
        formsDB = listado_formularios();
        String retorno = "<!ini_respuestas>\n";
        try {
            par.parse();
            ArrayList<Solicitud> halla = par.lista_solicitudes;
            for (int i = 0; i < halla.size(); i++) {
                Solicitud temp = halla.get(i);
                switch (temp.getTipo()) {
                    case "CREAR_USUARIO":
                        retorno += crearUsuario(temp);
                        break;
                    case "MODIFICAR_USUARIO":
                        //codigo para modificar usuario
                        break;
                    default:
                        break;
                }
                System.out.println("--------------------------------------------");
            }
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
        actualizarUsuarios();

        retorno += "<!fin_solicitudes>";
        return retorno;
    }

    public ArrayList<Usuario> listado_usuarios() throws FileNotFoundException {
        String rutaArchivos = "C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/usuarios.txt";
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
    }

    public ArrayList<Formulario> listado_formularios() throws FileNotFoundException {
        String rutaArchivos = "C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/formularios.txt";
        File nuevo = new File(rutaArchivos);
        parserALM par = new parserALM(new LexerALM(new FileReader(nuevo)));
        ArrayList<Formulario> halla = new ArrayList<>();
        try {
            par.parse();
            halla = par.listado_formularios;
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
        return halla;
    }

    public String crearUsuario(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"CREAR_USUARIO\">\n      {\"CREDENCIALES_USUARIO\":[\n";
        ArrayList<Usuario> usuarios = new ArrayList<>();
        if (!crearU.isTieneErrores()) {
            for (int j = 0; j < crearU.getCuantas().size(); j++) {
                Map<String, String> mapeado = crearU.getCuantas().get(j);
                Usuario temporal = new Usuario();
                mapeado.entrySet().forEach(entry -> {
                    if (entry.getKey().equals("USUARIO")) {
                        temporal.setUsuario(entry.getValue());
                    }
                    if (entry.getKey().equals("CONTRA")) {
                        temporal.setPassword(entry.getValue());
                    }
                    if (entry.getKey().equals("FECHA")) {
                        temporal.setFecha(entry.getValue());
                    }
                });
                if (!temporal.getUsuario().isEmpty() && !temporal.getPassword().isEmpty()) {
                    if (temporal.getFecha().isEmpty()) {
                        temporal.setFecha(fechaActual());
                    }
                } else {
                    temporal.setFecha("FALTA");
                }
                usuarios.add(temporal);
            }
            if (!usuarios.isEmpty()) {
                if (usuarios.size() == 1) {
                    usuarios = ingresadoUsuarioDB(usuarios);
                    retorno += mensaje(usuarios);
                } else {
                    usuarios = verificarIds(usuarios);
                    usuarios = ingresadoUsuarioDB(usuarios);
                }
                retorno += mensaje(usuarios);
            }
        } else {
            retorno += crearU.getDescripcion_error() + "\n";
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificarUsuario(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"MODIFICAR_USUARIO\">\n      {\"CREDENCIALES_USUARIO\":[\n";
        Map<String, String> analizadas = new HashMap<>();
        ArrayList<String> idsUs = new ArrayList<>();
        ArrayList<Usuario> usuarios = new ArrayList<>();
        if (!crearU.isTieneErrores()) {
            for (int j = 0; j < crearU.getCuantas().size(); j++) {
                Map<String, String> mapeado = crearU.getCuantas().get(j);
                if (mapeado.containsKey("USUARIO_ANTIGUO")) {
                    if (idsUs.isEmpty()) {
                        idsUs.add(mapeado.get("USUARIO_ANTIGUO"));
                        if (mapeado.containsKey("USUARIO_NUEVO") && mapeado.containsKey("CONTRA_NUEVA")) {
                            if (mapeado.containsKey("FECHA_MODIFICACION")) {
                                modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"),mapeado.get("USUARIO_NUEVO"),mapeado.get("CONTRA_NUEVA"),mapeado.get("FECHA_MODIFICACION"));
                            } else {
                                modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"),mapeado.get("USUARIO_NUEVO"),mapeado.get("CONTRA_NUEVA"),fechaActual());
                            }
                        } else {
                            crearU.getCuantas().get(j).put("ERROR", "FALTAN");
                        }
                    } else {
                        if (idsUs.contains(mapeado.get("USUARIO_ANTIGUO"))) {
                            crearU.getCuantas().get(j).put("ERROR", "REPETIDO");
                        } else {
                            idsUs.add(mapeado.get("USUARIO_ANTIGUO"));
                            if (mapeado.containsKey("USUARIO_NUEVO") && mapeado.containsKey("CONTRA_NUEVA")) {
                                if (mapeado.containsKey("FECHA_MODIFICACION")) {
                                    modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"),mapeado.get("USUARIO_NUEVO"),mapeado.get("CONTRA_NUEVA"),mapeado.get("FECHA_MODIFICACION"));
                                } else {
                                    modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"),mapeado.get("USUARIO_NUEVO"),mapeado.get("CONTRA_NUEVA"),fechaActual());
                                }
                            } else {
                                crearU.getCuantas().get(j).put("ERROR", "FALTAN");
                            }
                        }
                    }
                } else {
                    crearU.getCuantas().get(j).put("ERROR", "FALTA");
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"ESTADO\":\"Falta el parametro mas importante (USUARIO_ANTIGUO)\"\n      }\n";
                }
            }
        } else {
            retorno += crearU.getDescripcion_error() + "\n";
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificandoUsuario(String usuario_antiguo, String usuario_nuevo, String contra_nueva, String fecha_modificacion) {
        String retorno = "";
        if (!usuario_antiguo.equals(usuario_nuevo)){
            int posicion = -1;
            int posicion2 = -1;
            for (int i = 0; i < usuariosDB.size(); i++) {
                if (usuariosDB.get(i).getUsuario().equals(usuario_antiguo)){
                    posicion = i;
                } 
                if (usuariosDB.get(i).getUsuario().equals(usuario_nuevo)){
                    posicion2 = i;
                } 
            }
            if (posicion!=-1 && posicion2==-1){
                usuariosDB.get(posicion).setUsuario(usuario_nuevo);
                usuariosDB.get(posicion).setPassword(contra_nueva);
                usuariosDB.get(posicion).setFecha_mod(fecha_modificacion);
            } else {
                if (posicion==-1){
                    retorno += "         \"USUARIO_ANTIGUO\":\""+usuario_antiguo+"\"\n      }\n";
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No existe el usuario que intentas modificar\"\n      }\n";
                } else {
                    if (posicion2!=-1){
                        retorno += "         \"USUARIO_ANTIGUO\":\""+usuario_antiguo+"\"\n      }\n";
                        retorno += "         \"USUARIO_ANTIGUO\":\""+usuario_nuevo+"\"\n      }\n";
                        retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"El nuevo usuario ya existe en la base de datos\"\n      }\n";
                    }
                }
            }
        } else {
            retorno += "         \"USUARIO_ANTIGUO\":\""+usuario_antiguo+"\"\n      }\n";
            retorno += "         \"USUARIO_ANTIGUO\":\""+usuario_nuevo+"\"\n      }\n";
            retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
            retorno += "         \"DESCRIPCION_ERROR\":\"El nuevo usuario ya existe en la base de datos\"\n      }\n";
        }
        return retorno;
    }

    public ArrayList<Usuario> verificarIds(ArrayList<Usuario> ant) {
        ArrayList<Usuario> lis = new ArrayList<>();

        for (int i = 0; i < ant.size(); i++) {
            Usuario temp = ant.get(i);
            int conteo = 0;
            if (lis.isEmpty()) {
                lis.add(ant.get(i));
            } else {
                for (int j = 0; j < lis.size(); j++) {
                    if (temp.getUsuario().equals(lis.get(j).getUsuario())) {
                        conteo++;
                    }
                }
                if (conteo > 0) {
                    if (!ant.get(i).getFecha().equals("FALTA")) {
                        ant.get(i).setFecha("REPETIDO");
                    }
                }
                lis.add(ant.get(i));
            }
        }

        return lis;
    }

    public ArrayList<Usuario> ingresadoUsuarioDB(ArrayList<Usuario> ant) {
        ArrayList<Usuario> lis = ant;
        for (int i = 0; i < lis.size(); i++) {
            Usuario temp = lis.get(i);
            int conteo = 0;
            if (!temp.getFecha().equals("FALTA") && !temp.getFecha().equals("REPETIDO")) {
                for (int j = 0; j < usuariosDB.size(); j++) {
                    if (temp.getUsuario().equals(usuariosDB.get(j).getUsuario())) {
                        conteo++;
                    }
                }
                if (conteo > 0) {
                    lis.get(i).setFecha("EXISTE");
                } else {
                    usuariosDB.add(temp);
                }
            }
        }

        return lis;
    }

    public String mensaje(ArrayList<Usuario> ant) {
        String retorno = "";
        String cT = "            ";
        for (int i = 0; i < ant.size(); i++) {
            Usuario temp = ant.get(i);
            retorno += "         {\n";
            switch (temp.getFecha()) {
                case "FALTA":
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"ERROR\":\"Hacen falta parametros importantes(USUARIO,PASSWORD)\"\n         }";
                    break;
                case "REPETIDO":
                    retorno += cT + "\"USUARIO\":\"" + temp.getUsuario() + "\",\n";
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"DESCRIPCION_ERROR\":\"Este usuario ya fue solicitado para su creacion en esta misma solicitud\"\n         }";
                    break;
                case "EXISTE":
                    retorno += cT + "\"USUARIO\":\"" + temp.getUsuario() + "\",\n";
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"DESCRIPCION_ERROR\":\"Este usuario ya existe en la base de datos\"\n         }";
                    break;
                default:
                    retorno += cT + "\"USUARIO\":\"" + temp.getUsuario() + "\",\n";
                    retorno += cT + "\"ESTADO_SOLICITUD\":\"Usuario ingresado correctamente\"\n         }";
                    break;
            }
            if ((i + 1) != ant.size()) {
                retorno += ",\n";
            } else {
                retorno += "\n";
            }
        }
        return retorno;
    }

    public void actualizarUsuarios() {
        try (FileWriter fw = new FileWriter("C:/Users/willi/OneDrive/Documentos/NetBeansProjects/WForms/src/java/DB/usuarios.txt", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println("db.usuarios(");
            for (int i = 0; i < usuariosDB.size(); i++) {
                Usuario temp = usuariosDB.get(i);
                out.println("\t{");
                out.println("\t\t\"USUARIO\":\"" + temp.getUsuario() + "\",");
                out.println("\t\t\"PASSWORD\":\"" + temp.getPassword() + "\",");
                if (temp.getFecha_mod().isEmpty()) {
                    out.println("\t\t\"FECHA_CREACION\":\"" + temp.getFecha() + "\"");
                } else {
                    out.println("\t\t\"FECHA_CREACION\":\"" + temp.getFecha() + "\",");
                    out.println("\t\t\"FECHA_MODIFICACION\":\"" + temp.getFecha_mod() + "\"");
                }
                if (i + 1 != usuariosDB.size()) {
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

    public String fechaActual() {
        java.util.Date fecha = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // Aqui usamos la instancia formatter para darle el formato a la fecha. Es importante ver que el resultado es un string.
        return formatter.format(fecha);
    }
}
