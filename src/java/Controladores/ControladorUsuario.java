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
import POJOS.Componente;
import POJOS.Consulta;
import POJOS.Formulario;
import POJOS.Ingreso;
import POJOS.Registro;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java_cup.runtime.Symbol;

/**
 *
 * @author willi
 */
public class ControladorUsuario {

    public static final int TODOS_LOS_CAMPOS = 0;
    public static final int AMBOS = 1;
    public static final int SOLO_CAMPOS = 2;
    public static final int SOLO_RESTRICCIONES = 3;
    ArrayList<Usuario> usuariosDB;
    ArrayList<Formulario> formsDB;
    ArrayList<Formulario> datosDB;
    public String consultades = "";
    Map<String, ArrayList<String>> mapa = new HashMap<>();
    private String usuarioActual;

    public ControladorUsuario() {
        usuarioActual = "";
    }

    public String analizarSolicitudes(String texto, String usuario) throws FileNotFoundException {
        parser par = new parser(new Lexer(new StringReader(texto)));
        this.usuarioActual = usuario;
        usuariosDB = listado_usuarios();
        formsDB = listado_formularios();
        listado_datos();
        String retorno = "<!ini_respuestas>\n";
        try {
            par.parse();
            ArrayList<Solicitud> halla = par.lista_solicitudes;
            for (int i = 0; i < halla.size(); i++) {
                Solicitud temp = halla.get(i);
                switch (temp.getTipo()) {
                    case "CREAR_USUARIO":
                        retorno += crearUsuario2(temp);
                        break;
                    case "MODIFICAR_USUARIO":
                        retorno += modificarUsuario(temp);
                        break;
                    case "ELIMINAR_USUARIO":
                        retorno += eliminarUsuario(temp);
                        break;
                    case "NUEVO_FORMULARIO":
                        retorno += crearFormulario(temp);
                        break;
                    case "ELIMINAR_FORMULARIO":
                        retorno += eliminarFormulario(temp);
                        break;
                    case "MODIFICAR_FORMULARIO":
                        retorno += modificarFormulario(temp);
                        break;
                    case "AGREGAR_COMPONENTE":
                        retorno += agregarComponente(temp);
                        break;
                    case "ELIMINAR_COMPONENTE":
                        retorno += eliminarComponente(temp);
                        break;
                    case "MODIFICAR_COMPONENTE":
                        retorno += modificarComponente(temp);
                        break;
                    case "CONSULTAR_DATOS":
                        retorno += realizarConsultas(temp);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
        actualizarUsuarios();
        actualizarFormularios();
        retorno += "<!fin_solicitudes>";
        return retorno;
    }

    public String realizarConsultas(Solicitud temp) {
        String retorno = "   <!ini_respuesta:\"CONSULTAR_DATOS\">\n      {\"CONSULTAS\":[\n";
        ArrayList<Consulta> cons = temp.getConsulta();
        for (int i = 0; i < cons.size(); i++) {
            Consulta analizando = cons.get(i);
            retorno += "\t{\n";
            int pos = -1;
            for (int j = 0; j < datosDB.size(); j++) {
                if (datosDB.get(j).getId().equals(analizando.getForm()) || datosDB.get(j).getNombre().equals(analizando.getForm())) {
                    pos = j;
                    break;
                }
            }
            if (pos != -1) {
                Formulario tp = new Formulario();
                for (int j = 0; j < formsDB.size(); j++) {
                    if (formsDB.get(j).getId().equals(analizando.getForm()) || formsDB.get(j).getNombre().equals(analizando.getForm())) {
                        tp = formsDB.get(j);
                        break;
                    }
                }
                if (analizando.getRestricciones().isEmpty() && analizando.getCampos().isEmpty()) {

                    //SI SE PIDEN TODOS LOS DATOS QUE CONTENGA EL FORMULARIO
                    ArrayList<Componente> co = tp.getComponentes();
                    ArrayList<String> campos_totales = new ArrayList<>();
                    for (int j = 0; j < co.size(); j++) {
                        campos_totales.add(co.get(j).getId());
                    }
                    Formulario sp = datosDB.get(pos);
                    ArrayList<Registro> res = sp.getRegistros();
                    retorno += respuestaConsulta(res, analizando, co, TODOS_LOS_CAMPOS);
                } else {
                    if (analizando.getCampos().isEmpty()) {

                        //SI NO HAY CAMPOS, PERO SI RESTRICCIONES
                        ArrayList<Componente> co = tp.getComponentes();
                        int conteo_res = 0;
                        int conteo_total = 0;
                        ArrayList<Map<String, String>> soloCondiciones = new ArrayList<>();
                        ArrayList<String> oplogic = new ArrayList<>();
                        for (int j = 0; j < analizando.getRestricciones().size(); j++) {
                            if (!analizando.getRestricciones().get(j).containsKey("OPLOGICO")) {
                                for (int k = 0; k < co.size(); k++) {
                                    if (co.get(k).getId().equals(analizando.getRestricciones().get(j).get("CAMPO")) || co.get(k).getNombre_campo().equals(analizando.getRestricciones().get(j).get("CAMPO"))) {
                                        soloCondiciones.add(analizando.getRestricciones().get(j));
                                        conteo_res++;
                                    }
                                }
                                conteo_total++;
                            } else {
                                oplogic.add(analizando.getRestricciones().get(j).get("OPLOGICO"));
                            }
                        }
                        if (conteo_res != conteo_total) {
                            retorno += "         \"ESTADO\":\"CONSULTA NO REALIZADA\",\n";
                            retorno += "         \"DESCRIPCION\":\"UNA O VARIAS RESTRICCIONES INCLUIAN CAMPOS QUE NO EXISTEN EN EL FORMULARIO\"\n      }";
                        } else {
                            ArrayList<ArrayList<Registro>> conjunto = new ArrayList<>();
                            for (int j = 0; j < soloCondiciones.size(); j++) {
                                conjunto.add(concuerda(pos, analizando.getCampos(), soloCondiciones.get(j), co));
                            }
                            while (!oplogic.isEmpty()) {
                                int an = -1;
                                for (int j = 0; j < oplogic.size(); j++) {
                                    if (oplogic.get(j).equals("AND")) {
                                        an = j;
                                        break;
                                    }
                                }
                                if (an != -1) {
                                    ArrayList<Registro> r1 = conjunto.get(an);
                                    ArrayList<Registro> r2 = conjunto.get(an + 1);
                                    ArrayList<Registro> ret = new ArrayList<>();
                                    for (int j = 0; j < r1.size(); j++) {
                                        for (int k = 0; k < r2.size(); k++) {
                                            if (r1.get(j).getNoregistro().equals(r2.get(k).getNoregistro())) {
                                                ret.add(r1.get(j));
                                                break;
                                            }
                                        }
                                    }
                                    conjunto.set(an, ret);
                                    conjunto.remove(an + 1);
                                    oplogic.remove(an);
                                } else {
                                    an = -1;
                                    for (int j = 0; j < oplogic.size(); j++) {
                                        if (oplogic.get(j).equals("OR")) {
                                            an = j;
                                            break;
                                        }
                                    }
                                    if (an != -1) {
                                        ArrayList<Registro> r1 = conjunto.get(an);
                                        ArrayList<Registro> r2 = conjunto.get(an + 1);
                                        ArrayList<Registro> ret = new ArrayList<>();
                                        ret.addAll(r2);
                                        for (int j = 0; j < r1.size(); j++) {
                                            boolean ingresado = false;
                                            for (int k = 0; k < r2.size(); k++) {
                                                if (r1.get(j).getNoregistro().equals(r2.get(k).getNoregistro())) {
                                                    ingresado = true;
                                                    break;
                                                }
                                            }
                                            if (!ingresado) {
                                                ret.add(r1.get(j));
                                            }
                                        }
                                        conjunto.set(an, ret);
                                        conjunto.remove(an + 1);
                                        oplogic.remove(an);
                                    }
                                }
                            }
                            ArrayList<Registro> enviar = conjunto.get(0);
                            retorno += respuestaConsulta(enviar, analizando, co, SOLO_RESTRICCIONES);
                        }
                    } else {
                        ArrayList<Componente> co = tp.getComponentes();
                        int conteo = 0;
                        for (int j = 0; j < co.size(); j++) {
                            if (analizando.getCampos().contains(co.get(j).getNombre_campo())) {
                                conteo++;
                            }
                        }
                        if (conteo == analizando.getCampos().size()) {
                            if (analizando.getRestricciones().isEmpty()) {

                                //SI SE ENVIAN CAMPOS SIN RESTRICCIONES
                                Formulario sp = datosDB.get(pos);
                                ArrayList<Registro> res = sp.getRegistros();
                                retorno += respuestaConsulta(res, analizando, co, SOLO_CAMPOS);
                            } else {
                                int conteo_res = 0;
                                int conteo_total = 0;
                                ArrayList<Map<String, String>> soloCondiciones = new ArrayList<>();
                                ArrayList<String> oplogic = new ArrayList<>();
                                for (int j = 0; j < analizando.getRestricciones().size(); j++) {
                                    if (!analizando.getRestricciones().get(j).containsKey("OPLOGICO")) {
                                        for (int k = 0; k < co.size(); k++) {
                                            if (co.get(k).getId().equals(analizando.getRestricciones().get(j).get("CAMPO")) || co.get(k).getNombre_campo().equals(analizando.getRestricciones().get(j).get("CAMPO"))) {
                                                soloCondiciones.add(analizando.getRestricciones().get(j));
                                                conteo_res++;
                                            }
                                        }
                                        conteo_total++;
                                    } else {
                                        oplogic.add(analizando.getRestricciones().get(j).get("OPLOGICO"));
                                    }
                                }
                                if (conteo_res != conteo_total) {
                                    retorno += "         \"ESTADO\":\"CONSULTA NO REALIZADA\",\n";
                                    retorno += "         \"DESCRIPCION\":\"UNA O VARIAS RESTRICCIONES INCLUIAN CAMPOS QUE NO EXISTEN EN EL FORMULARIO\"\n      }";
                                } else {
                                    ArrayList<ArrayList<Registro>> conjunto = new ArrayList<>();
                                    for (int j = 0; j < soloCondiciones.size(); j++) {
                                        conjunto.add(concuerda(pos, analizando.getCampos(), soloCondiciones.get(j), co));
                                    }
                                    while (!oplogic.isEmpty()) {
                                        int an = -1;
                                        for (int j = 0; j < oplogic.size(); j++) {
                                            if (oplogic.get(j).equals("AND")) {
                                                an = j;
                                                break;
                                            }
                                        }
                                        if (an != -1) {
                                            ArrayList<Registro> r1 = conjunto.get(an);
                                            ArrayList<Registro> r2 = conjunto.get(an + 1);
                                            ArrayList<Registro> ret = new ArrayList<>();
                                            for (int j = 0; j < r1.size(); j++) {
                                                for (int k = 0; k < r2.size(); k++) {
                                                    if (r1.get(j).getNoregistro().equals(r2.get(k).getNoregistro())) {
                                                        ret.add(r1.get(j));
                                                        break;
                                                    }
                                                }
                                            }
                                            conjunto.set(an, ret);
                                            conjunto.remove(an + 1);
                                            oplogic.remove(an);
                                        } else {
                                            an = -1;
                                            for (int j = 0; j < oplogic.size(); j++) {
                                                if (oplogic.get(j).equals("OR")) {
                                                    an = j;
                                                    break;
                                                }
                                            }
                                            if (an != -1) {
                                                ArrayList<Registro> r1 = conjunto.get(an);
                                                ArrayList<Registro> r2 = conjunto.get(an + 1);
                                                ArrayList<Registro> ret = new ArrayList<>();
                                                ret.addAll(r2);
                                                for (int j = 0; j < r1.size(); j++) {
                                                    boolean ingresado = false;
                                                    for (int k = 0; k < r2.size(); k++) {
                                                        if (r1.get(j).getNoregistro().equals(r2.get(k).getNoregistro())) {
                                                            ingresado = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!ingresado) {
                                                        ret.add(r1.get(j));
                                                    }
                                                }
                                                conjunto.set(an, ret);
                                                conjunto.remove(an + 1);
                                                oplogic.remove(an);
                                            }
                                        }
                                    }
                                    ArrayList<Registro> enviar = conjunto.get(0);
                                    retorno += respuestaConsulta(enviar, analizando, co, AMBOS);
                                }
                            }
                        } else {
                            retorno += "         \"ESTADO\":\"CONSULTA NO REALIZADA\",\n";
                            retorno += "         \"DESCRIPCION\":\"UNO O VARIOS CAMPOS QUE SOLICITASTE NO FORMAN PARTE DE LOS COMPONENTES DEL FORMULARIO\"\n      }";
                        }
                    }
                }

            } else {
                retorno += "         \"ESTADO\":\"CONSULTA NO REALIZADA\",\n";
                retorno += "         \"DESCRIPCION\":\"EL FORMULARIO AUN NO TIENE DATOS INGRESADOS, POR LO QUE TU CONSULTA NO DEVUELVE NADA\"\n      }";
            }
            if ((i + 1) != cons.size()) {
                retorno += "\t},\n";
            } else {
                retorno += "\t}\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    /**
     *
     * @param res ArrayList con los registros encontrados y que se le mostraran
     * al usuario
     * @param analizando Consulta para agarrar los datos correspondientes y
     * volver a escribir la consulta
     * @param co ArrayList de los componentes para el orden de los mismos
     * @param opcion TODOS_LOS_CAMPOS si no se mandan ni campos ni restricciones
     * AMBOS si se mandan restricciones y campos SOLO_CAMPOS solo los campos y
     * SOLO_RESTRICCIONES SI SOLO SE MANDAN RESTRICCIONES
     * @return
     */
    public String respuestaConsulta(ArrayList<Registro> res, Consulta analizando, ArrayList<Componente> co, int opcion) {
        String retorno = "";
        String t3 = "\t\t";
        retorno += t3 + "\"ID_CONSULTA\":\"" + analizando.getNoconsulta() + "\",\n";
        consultades += analizando.getNoconsulta() + "\n";
        switch (opcion) {
            case TODOS_LOS_CAMPOS:
                retorno += t3 + "\"CONSULTA\" : \"SELECT TO FORM -> " + analizando.getForm() + "[]\",\n";
                consultades += "SELECT TO FORM -> " + analizando.getForm() + "[]\n";
                break;
            case AMBOS:
                retorno += t3 + "\"CONSULTA\" : \"SELECT TO FORM -> " + analizando.getForm() + " [";
                consultades += "SELECT TO FORM -> " + analizando.getForm() + "[";
                for (int i = 0; i < analizando.getCampos().size(); i++) {
                    retorno += analizando.getCampos().get(i);
                    consultades += analizando.getCampos().get(i);
                    if ((i + 1) != analizando.getCampos().size()) {
                        retorno += ",";
                        consultades += ",";
                    }
                }
                retorno += "] ";
                retorno += "WHERE [ ";
                consultades += "] WHERE [";
                for (int i = 0; i < analizando.getRestricciones().size(); i++) {
                    Map<String, String> mp = analizando.getRestricciones().get(i);
                    if (mp.containsKey("OPLOGICO")) {
                        retorno += mp.get("OPLOGICO") + " ";
                        consultades += mp.get("OPLOGICO") + " ";
                    } else {
                        if (mp.containsKey("NOT")) {
                            retorno += "NOT ";
                            consultades += "NOT ";
                        }
                        retorno += mp.get("CAMPO") + " " + mp.get("OPRELACIONAL") + " ";
                        consultades += mp.get("CAMPO") + " " + mp.get("OPRELACIONAL") + " ";
                        if (mp.get("TIPO").equals("NUMERO") || mp.get("TIPO").equals("DECIMAL")) {
                            retorno += " " + mp.get("DATO") + " ";
                            consultades += " " + mp.get("DATO") + " ";
                        } else {
                            retorno += " '" + mp.get("DATO") + "' ";
                            consultades += " '" + mp.get("DATO") + "' ";
                        }
                    }
                }
                retorno += " ]\",\n";
                consultades += " ]\n";
                break;
            case SOLO_CAMPOS:
                retorno += t3 + "\"CONSULTA\" : \"SELECT TO FORM -> " + analizando.getForm() + " [";
                consultades += "SELECT TO FORM -> " + analizando.getForm() + "[";
                for (int i = 0; i < analizando.getCampos().size(); i++) {
                    retorno += analizando.getCampos().get(i);
                    consultades += analizando.getCampos().get(i);
                    if ((i + 1) != analizando.getCampos().size()) {
                        retorno += ",";
                        consultades += ",";
                    }
                }
                retorno += "]\",\n";
                consultades += " ]\n";
                break;
            case SOLO_RESTRICCIONES:
                retorno += t3 + "\"CONSULTA\" : \"SELECT TO FORM -> " + analizando.getForm() + " [] ";
                consultades += "SELECT TO FORM -> " + analizando.getForm() + "[] WHERE [ ";
                retorno += "WHERE [ ";
                for (int i = 0; i < analizando.getRestricciones().size(); i++) {
                    Map<String, String> mp = analizando.getRestricciones().get(i);
                    if (mp.containsKey("OPLOGICO")) {
                        retorno += mp.get("OPLOGICO") + " ";
                        consultades += mp.get("OPLOGICO") + " ";
                    } else {
                        if (mp.containsKey("NOT")) {
                            retorno += "NOT ";
                            consultades += "NOT ";
                        }
                        retorno += mp.get("CAMPO") + " " + mp.get("OPRELACIONAL") + " ";
                        consultades += mp.get("CAMPO") + " " + mp.get("OPRELACIONAL") + " ";
                        if (mp.get("TIPO").equals("NUMERO") || mp.get("TIPO").equals("DECIMAL")) {
                            retorno += " " + mp.get("DATO") + " ";
                            consultades += " " + mp.get("DATO") + " ";
                        } else {
                            retorno += " '" + mp.get("DATO") + "' ";
                            consultades += " '" + mp.get("DATO") + "' ";
                        }
                    }
                }
                retorno += " ]\",\n";
                consultades += " ]\n";
                break;
            default:
                break;
        }
        if (!res.isEmpty()) {
            ArrayList<String> camposec = new ArrayList<>();
            ArrayList<String> datosec = new ArrayList<>();
            retorno += t3 + "\"RESULTADOS\":[\n";
            for (int j = 0; j < res.size(); j++) {
                Registro tep = res.get(j);
                if (opcion == TODOS_LOS_CAMPOS) {
                    tep = extraerTodosCampos(tep, co);
                } else if (opcion == SOLO_CAMPOS) {
                    tep = extraerCampos(analizando.getCampos(), tep, co);
                }
                if (j == 0) {
                    camposec.add("ID_REGISTRO");
                    for (int k = 0; k < tep.getValores().size(); k++) {
                        camposec.add(tep.getValores().get(k).getNombrec());
                    }
                }
                retorno += t3 + "{\n" + t3 + "\t\"REGISTRO\":\"" + tep.getNoregistro() + "\",\n";
                datosec.add(tep.getNoregistro());
                for (int k = 0; k < tep.getValores().size(); k++) {
                    retorno += t3 + "\t\"" + tep.getValores().get(k).getNombrec() + "\":\"" + tep.getValores().get(k).getDato() + "\"";
                    if (!tep.getValores().get(k).getDato().isEmpty()){
                        datosec.add(tep.getValores().get(k).getDato());
                    } else {
                        datosec.add(" ");
                    }
                    if ((k + 1) != tep.getValores().size()) {
                        retorno += ",\n";
                    } else {
                        retorno += "\n";
                    }
                }
                retorno += t3 + "}";
                if ((j + 1) != res.size()) {
                    retorno += ",\n";
                }
            }
            for (String s:camposec){
                consultades += s+" <///>";
            }
            consultades += "\n";
            for (String s:datosec){
                consultades += s+" <///>";
            }
            consultades += "\t";
            retorno += "\n" + t3 + "]\n";
        } else {
            consultades += "NO HAY NINGUN REGISTRO\nQUE COINCIDA CON LO SOLICITADO\t";
            retorno += t3 + "\"RESULTADOS\":\"NO SE HALLARON RESULTADOS\"\n";
        }
        return retorno;
    }

    public ArrayList<Registro> concuerda(int form, ArrayList<String> campos, Map<String, String> restricciones, ArrayList<Componente> co) {
        ArrayList<Registro> retorno = new ArrayList<>();
        Formulario actual = datosDB.get(form);
        ArrayList<Registro> ingresados = actual.getRegistros();
        boolean traeCampos = !campos.isEmpty();
        String campo = restricciones.get("CAMPO");
        String opre = restricciones.get("OPRELACIONAL");
        if (restricciones.containsKey("NOT")) {
            switch (opre) {
                case "=":
                    opre = "!=";
                    break;
                case "<":
                    opre = ">=";
                    break;
                case ">":
                    opre = "<=";
                    break;
                case "<=":
                    opre = ">";
                    break;
                case ">=":
                    opre = "<";
                    break;
                case "!=":
                    opre = "=";
                    break;
                default:
                    break;
            }
        }
        String tipo = restricciones.get("TIPO");
        String dato = restricciones.get("DATO");

        if (tipo.equals("NUMERO") || tipo.equals("DECIMAL")) {
            Double datoC = Double.parseDouble(dato);
            for (int i = 0; i < ingresados.size(); i++) {
                Registro tp = ingresados.get(i);
                ArrayList<Ingreso> in = tp.getValores();
                for (int j = 0; j < in.size(); j++) {
                    Ingreso it = in.get(j);
                    if (it.getIdc().equals(campo) || it.getNombrec().equals(campo)) {
                        if (verificarNumero(it.getDato())) {
                            Double ingreso = Double.parseDouble(it.getDato());
                            boolean corresponde = false;
                            switch (opre) {
                                case "=":
                                    if (Objects.equals(ingreso, datoC)) {
                                        corresponde = true;
                                    }
                                    break;
                                case "<":
                                    if (ingreso < datoC) {
                                        corresponde = true;
                                    }
                                    break;
                                case ">":
                                    if (ingreso > datoC) {
                                        corresponde = true;
                                    }
                                    break;
                                case "<=":
                                    if (ingreso <= datoC) {
                                        corresponde = true;
                                    }
                                    break;
                                case ">=":
                                    if (ingreso >= datoC) {
                                        corresponde = true;
                                    }
                                    break;
                                case "!=":
                                    if (ingreso > datoC || ingreso < datoC) {
                                        corresponde = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            if (corresponde) {
                                if (traeCampos) {
                                    retorno.add(extraerCampos(campos, tp, co));
                                } else {
                                    retorno.add(extraerTodosCampos(tp, co));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < ingresados.size(); i++) {
                Registro tp = ingresados.get(i);
                ArrayList<Ingreso> in = tp.getValores();
                for (int j = 0; j < in.size(); j++) {
                    Ingreso it = in.get(j);
                    if (it.getIdc().equals(campo) || it.getNombrec().equals(campo)) {
                        boolean corresponde = false;
                        switch (opre) {
                            case "=":
                                if (it.getDato().equals(dato)) {
                                    corresponde = true;
                                }
                                break;
                            case "!=":
                                if (!it.getDato().equals(dato)) {
                                    corresponde = true;
                                }
                                break;
                            default:
                                break;
                        }
                        if (corresponde) {
                            if (traeCampos) {
                                retorno.add(extraerCampos(campos, tp, co));
                            } else {
                                retorno.add(extraerTodosCampos(tp, co));
                            }
                        }
                    }
                }
            }
        }

        return retorno;
    }

    public Registro extraerTodosCampos(Registro tp, ArrayList<Componente> co) {
        Registro retorno = new Registro();
        retorno.setNoregistro(tp.getNoregistro());

        for (int i = 0; i < co.size(); i++) {
            Componente c = co.get(i);
            boolean encontrado = false;
            for (int k = 0; k < tp.getValores().size(); k++) {
                Ingreso in = tp.getValores().get(k);
                if (in.getIdc().equals(c.getId()) || in.getNombrec().equals(c.getNombre_campo())) {
                    retorno.getValores().add(in);
                    encontrado = true;
                    break;
                }
            }
            if (!c.getClase().equals("BOTON") && !c.getClase().equals("IMAGEN") && !c.getClase().equals("FICHERO")) {
                if (!encontrado) {
                    Ingreso it = new Ingreso();
                    it.setIdc(c.getId());
                    it.setNombrec(c.getNombre_campo());
                    retorno.getValores().add(it);
                }
            }
        }
        return retorno;
    }

    /**
     *
     * @param campos Los campos que solicita el usuario
     * @param tp El registro de donde pide los datos
     * @param co El conjunto de componentes, para retornar un conjunto bien
     * ordenado
     * @return El registro ordenado
     */
    public Registro extraerCampos(ArrayList<String> campos, Registro tp, ArrayList<Componente> co) {
        Registro retorno = new Registro();
        retorno.setNoregistro(tp.getNoregistro());

        for (int i = 0; i < co.size(); i++) {
            for (int j = 0; j < campos.size(); j++) {
                if (co.get(i).getId().equals(campos.get(j)) || co.get(i).getNombre_campo().equals(campos.get(j))) {
                    String actual = campos.get(j);
                    boolean encontrado = false;
                    for (int k = 0; k < tp.getValores().size(); k++) {
                        Ingreso in = tp.getValores().get(k);
                        if (in.getIdc().equals(actual) || in.getNombrec().equals(actual)) {
                            retorno.getValores().add(in);
                            encontrado = true;
                        }
                    }
                    if (!encontrado) {
                        Ingreso it = new Ingreso();
                        it.setIdc(co.get(i).getId());
                        it.setNombrec(co.get(i).getNombre_campo());
                        retorno.getValores().add(it);
                    }
                }
            }
        }
        return retorno;
    }

    public boolean verificarNumero(String dato) {
        return dato.matches("[+-]?\\d*(\\.\\d+)?");
    }

    public String dePrueba(String texto, String usuario) throws FileNotFoundException, IOException {
        parser par = new parser(new Lexer(new StringReader(texto)));
        this.usuarioActual = usuario;
        usuariosDB = listado_usuarios();
        formsDB = listado_formularios();
        String retorno = "<!ini_respuestas>\n";
        retorno += dePrueba2(texto, usuario);
        try {
            par.parse();
            ArrayList<Solicitud> halla = par.lista_solicitudes;
            ArrayList<Consulta> halla2 = par.lista_consultas;
            if (!halla2.isEmpty()) {
                for (int i = 0; i < halla2.size(); i++) {
                    Consulta c = halla2.get(i);
                    System.out.println("Consulta numero: " + halla2.get(i).getNoconsulta() + " del Formulario: " + halla2.get(i).getForm() + " sacara " + halla2.get(i).getCampos().size());
                    for (int j = 0; j < c.getPuentes().size(); j++) {
                        System.out.println("Puente " + j + ": " + c.getPuentes().get(j));
                    }
                    for (int j = 0; j < c.getRestricciones().size(); j++) {
                        System.out.println("Restricciones " + j + ": " + c.getRestricciones().get(j));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error por: " + ex.toString());
        }
        retorno += "<!fin_solicitudes>";
        return retorno;
    }

    public String dePrueba2(String texto, String usuario) throws FileNotFoundException, IOException {
        Lexer lexico = new Lexer(new StringReader(texto));
        this.usuarioActual = usuario;
        usuariosDB = listado_usuarios();
        formsDB = listado_formularios();
        String retorno = "<!ini_respuestas>\n";
        while (true) {
            Symbol simbolito = lexico.next_token();
            if (simbolito.value == null) {
                break;
            }
            System.out.println("valor token:" + simbolito.value);
        }
        retorno += "<!fin_solicitudes>";
        return retorno;
    }

    public String analizarSolicitudes(String texto) throws FileNotFoundException {
        parser par = new parser(new Lexer(new StringReader(texto)));
        usuariosDB = listado_usuarios();
        formsDB = listado_formularios();
        String retorno = "";
        try {
            par.parse();
            ArrayList<Solicitud> halla = par.lista_solicitudes;
            if (halla.size() > 0) {
                if (halla.get(0).getTipo().equalsIgnoreCase("LOGIN")) {
                    System.out.println("entra");
                    int conteo = 0;
                    for (int i = 0; i < halla.size(); i++) {
                        if (halla.get(i).getTipo().equalsIgnoreCase("LOGIN")) {
                            conteo++;
                        }
                    }
                    if (conteo > 1) {
                        retorno += "Mandas la solicitud más de un login, solo se permite que hagas una";
                    } else {
                        Map<String, String> usuario = halla.get(0).getCuantas().get(0);
                        boolean existe = false;
                        for (int i = 0; i < usuariosDB.size(); i++) {
                            if (usuariosDB.get(i).getUsuario().equals(usuario.get("USUARIO"))
                                    && usuariosDB.get(i).getPassword().equals(usuario.get("CONTRA"))) {
                                existe = true;
                                break;
                            }
                        }
                        if (existe) {
                            setUsuarioActual(usuario.get("USUARIO"));
                            retorno += analizarSolicitudes(texto, getUsuarioActual());
                        } else {
                            retorno += "Las credenciales que ingresaste no corresponden a ningún usuario";
                        }
                    }
                }
            } else {
                retorno += "Sin mandar nada";
            }
        } catch (Exception ex) {
            System.out.println("Error por en metodo analizar: " + ex.toString());
        }
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

    public String crearUsuario2(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"CREAR_USUARIO\">\n      {\"CREDENCIALES_USUARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < crearU.getCuantas().size(); j++) {
            Map<String, String> mapeado = crearU.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("USUARIO")) {
                    if (idsUs.isEmpty()) {
                        idsUs.add(mapeado.get("USUARIO"));
                        if (mapeado.containsKey("USUARIO") && mapeado.containsKey("CONTRA")) {
                            if (mapeado.containsKey("FECHA_CREACION")) {
                                retorno += creandoUsuario(mapeado.get("USUARIO"), mapeado.get("CONTRA"), mapeado.get("FECHA_MODIFICACION"));
                            } else {
                                retorno += creandoUsuario(mapeado.get("USUARIO"), mapeado.get("CONTRA"), fechaActual());
                            }
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Falta la contraseña para poder crear el usuario\"\n      }";
                        }
                    } else {
                        if (idsUs.contains(mapeado.get("USUARIO"))) {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"El usuario que se intenta crear ya fue solicitado previamente en esta misma solicitud\"\n      }";
                        } else {
                            idsUs.add(mapeado.get("USUARIO"));
                            if (mapeado.containsKey("USUARIO") && mapeado.containsKey("CONTRA")) {
                                if (mapeado.containsKey("FECHA_CREACION")) {
                                    retorno += creandoUsuario(mapeado.get("USUARIO"), mapeado.get("CONTRA"), mapeado.get("FECHA_MODIFICACION"));
                                } else {
                                    retorno += creandoUsuario(mapeado.get("USUARIO"), mapeado.get("CONTRA"), fechaActual());
                                }
                            } else {
                                retorno += "         \"ESTADO\":\"ERROR\",\n";
                                retorno += "         \"DESCRIPCION_ERROR\":\"Falta la contraseña para poder crear el usuario\"\n      }";
                            }
                        }
                    }
                } else {
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el parametro más importante (USUARIO)\"\n      }";
                }
                if ((j + 1) != crearU.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String creandoUsuario(String usuario, String contra, String fecha_creacion) {
        String retorno = "";
        int posicion = -1;
        for (int i = 0; i < usuariosDB.size(); i++) {
            if (usuariosDB.get(i).getUsuario().equals(usuario)) {
                posicion = i;
            }
        }
        if (posicion == -1) {
            Usuario temp = new Usuario(usuario, contra, fecha_creacion);
            usuariosDB.add(temp);
            retorno += "         \"ESTADO\":\"USUARIO MODIFICADO\"\n      }";
        } else {
            retorno += "         \"ESTADO\":\"ERROR\",\n";
            retorno += "         \"DESCRIPCION_ERROR\":\"El usuario que tratas de crear ya existe en la base de datos\"\n      }";
        }
        return retorno;
    }

    public String crearFormulario(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"NUEVO_FORMULARIO\">\n      {\"PARAMETROS_FORMULARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < crearU.getCuantas().size(); j++) {
            Map<String, String> mapeado = crearU.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")) {
                    if (idsUs.isEmpty()) {
                        idsUs.add(mapeado.get("ID"));
                        if (mapeado.containsKey("ID") && mapeado.containsKey("TEMA") && mapeado.containsKey("TITULO") && mapeado.containsKey("NOMBRE")) {
                            if (!mapeado.containsKey("FECHA")) {
                                mapeado.put("FECHA", fechaActual());
                            }
                            if (!mapeado.containsKey("USUARIO_CREACION")) {
                                mapeado.put("USUARIO_CREACION", usuarioActual);
                            }
                            retorno += creandoFormulario(mapeado);
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Faltan parametros obligatorios (TITULO, TEMA, NOMBRE)\"\n      }";
                        }
                    } else {
                        if (idsUs.contains(mapeado.get("ID"))) {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"El ID del formulario que se intenta ingresar ya existe\"\n      }";
                        } else {
                            idsUs.add(mapeado.get("ID"));
                            if (mapeado.containsKey("ID") && mapeado.containsKey("TEMA") && mapeado.containsKey("TITULO") && mapeado.containsKey("NOMBRE")) {
                                if (!mapeado.containsKey("FECHA_CREACION")) {
                                    mapeado.put("FECHA_CREACION", fechaActual());
                                }
                                if (!mapeado.containsKey("USUARIO_CREACION")) {
                                    mapeado.put("USUARIO_CREACION", usuarioActual);
                                }
                                retorno += creandoFormulario(mapeado);
                            } else {
                                retorno += "         \"ESTADO\":\"ERROR\",\n";
                                retorno += "         \"DESCRIPCION_ERROR\":\"Faltan parametros obligatorios (TITULO, TEMA, NOMBRE)\"\n      }";
                            }
                        }
                    }
                } else {
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No se puede crear el formulario sin alguno de los siguientes parametros (ID,TITULO,NOMBRE,TEMA)\"\n      }";
                }
                if ((j + 1) != crearU.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String creandoFormulario(Map<String, String> mapeado) {
        String retorno = "";
        int posicion = -1;
        for (int i = 0; i < formsDB.size(); i++) {
            if (formsDB.get(i).getId().equals(mapeado.get("ID"))) {
                posicion = i;
            }
        }
        if (posicion == -1) {
            Formulario temp = new Formulario();
            temp.setId(mapeado.get("ID"));
            temp.setTitulo(mapeado.get("TITULO"));
            temp.setTema(mapeado.get("TEMA"));
            temp.setNombre(mapeado.get("NOMBRE"));
            temp.setUsuario(mapeado.get("USUARIO_CREACION"));
            temp.setFecha(mapeado.get("FECHA"));
            formsDB.add(temp);
            retorno += "         \"ESTADO\":\"FORMULARIO INGRESADO\"\n      }";
        } else {
            retorno += "         \"ESTADO\":\"ERROR\",\n";
            retorno += "         \"DESCRIPCION_ERROR\":\"El formulario que intentas crear ya existe\"\n      }";
        }
        return retorno;
    }

    public String agregarComponente(Solicitud modificarForm) {
        String retorno = "   <!ini_respuesta:\"AGREGAR_COMPONENTE\">\n      {\"PARAMETROS_COMPONENTE\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < modificarForm.getCuantas().size(); j++) {
            Map<String, String> mapeado = modificarForm.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")
                        && mapeado.containsKey("FORMULARIO")
                        && mapeado.containsKey("CLASE")
                        && mapeado.containsKey("TEXTO_VISIBLE")) {
                    int posicion = -1;
                    for (int i = 0; i < formsDB.size(); i++) {
                        if (formsDB.get(i).getId().equals(mapeado.get("FORMULARIO"))) {
                            posicion = i;
                            break;
                        }
                    }
                    if (posicion != -1) {
                        ArrayList<Componente> componentes = formsDB.get(posicion).getComponentes();
                        int posicion_componente = -1;
                        for (int i = 0; i < componentes.size(); i++) {
                            if (componentes.get(i).getId().equals(mapeado.get("ID"))) {
                                posicion_componente = i;
                                break;
                            }
                        }
                        if (posicion_componente == -1) {
                            retorno += agregandoComponente(mapeado, posicion);
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Ya existe un componente con el id que se envio\"\n      }";
                        }
                    } else {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"No se puede agregar un componente a un formulario que no existe\"\n      }";
                    }
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No se puede crear el formulario sin alguno de los siguientes parametros (ID,NOMBRE_CAMPO,FORMULARIO,CLASE)\"\n      }";
                }
                if ((j + 1) != modificarForm.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String agregandoComponente(Map<String, String> mapeado, int posicion) {
        String retorno = "";
        Componente nuevo = new Componente();
        nuevo.setClase(mapeado.get("CLASE"));
        nuevo.setIndice(formsDB.get(posicion).getComponentes().size() + 1);
        nuevo.setId(mapeado.get("ID"));
        nuevo.setFormulario(mapeado.get("FORMULARIO"));
        nuevo.setTexto_visible(mapeado.get("TEXTO_VISIBLE"));
        switch (mapeado.get("CLASE")) {
            case "BOTON":
                formsDB.get(posicion).getComponentes().add(nuevo);
                retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                break;
            case "IMAGEN":
                if (mapeado.containsKey("URL")) {
                    nuevo.setUrl(mapeado.get("URL"));
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el url\"\n      }";
                }
                break;
            case "CAMPO_TEXTO":
                if (mapeado.containsKey("NOMBRE_CAMPO")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            case "AREA_TEXTO":
                if (mapeado.containsKey("NOMBRE_CAMPO")
                        && mapeado.containsKey("FILAS")
                        && mapeado.containsKey("COLUMNAS")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    nuevo.setFilas(Integer.parseInt(mapeado.get("FILAS")));
                    nuevo.setColumnas(Integer.parseInt(mapeado.get("COLUMNAS")));
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            case "CHECKBOX":
                if (mapeado.containsKey("NOMBRE_CAMPO")
                        && mapeado.containsKey("OPCIONES")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    ArrayList<String> lista_opciones = new ArrayList<>();
                    String opciones = mapeado.get("OPCIONES");
                    String partes[] = opciones.split("\n");
                    lista_opciones.addAll(Arrays.asList(partes));
                    nuevo.setOpciones(lista_opciones);
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "RADIO":
                if (mapeado.containsKey("NOMBRE_CAMPO")
                        && mapeado.containsKey("OPCIONES")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    ArrayList<String> lista_opciones = new ArrayList<>();
                    String opciones = mapeado.get("OPCIONES");
                    String partes[] = opciones.split("\n");
                    lista_opciones.addAll(Arrays.asList(partes));
                    nuevo.setOpciones(lista_opciones);
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "COMBO":
                if (mapeado.containsKey("NOMBRE_CAMPO")
                        && mapeado.containsKey("OPCIONES")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    ArrayList<String> lista_opciones = new ArrayList<>();
                    String opciones = mapeado.get("OPCIONES");
                    String partes[] = opciones.split("\n");
                    lista_opciones.addAll(Arrays.asList(partes));
                    nuevo.setOpciones(lista_opciones);
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "FICHERO":
                if (mapeado.containsKey("NOMBRE_CAMPO")) {
                    nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
                    if (mapeado.containsKey("ALINEACION")) {
                        nuevo.setAlineacion(mapeado.get("ALINEACION"));
                    }
                    if (mapeado.containsKey("REQUERIDO")) {
                        nuevo.setRequerido(mapeado.get("REQUERIDO"));
                    }
                    formsDB.get(posicion).getComponentes().add(nuevo);
                    retorno += "         \"ESTADO\":\"COMPONENTE INGRESADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            default:
                break;
        }
        return retorno;
    }

    public String modificarComponente(Solicitud modificarForm) {
        String retorno = "   <!ini_respuesta:\"AGREGAR_COMPONENTE\">\n      {\"PARAMETROS_COMPONENTE\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < modificarForm.getCuantas().size(); j++) {
            Map<String, String> mapeado = modificarForm.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")
                        && mapeado.containsKey("FORMULARIO")) {
                    int posicion = -1;
                    for (int i = 0; i < formsDB.size(); i++) {
                        if (formsDB.get(i).getId().equals(mapeado.get("FORMULARIO"))) {
                            posicion = i;
                            break;
                        }
                    }
                    if (posicion != -1) {
                        ArrayList<Componente> componentes = formsDB.get(posicion).getComponentes();
                        int posicion_componente = -1;
                        for (int i = 0; i < componentes.size(); i++) {
                            if (componentes.get(i).getId().equals(mapeado.get("ID"))) {
                                posicion_componente = i;
                                break;
                            }
                        }
                        if (posicion_componente != -1) {
                            if (mapeado.size() == 2) {
                                retorno += "         \"ESTADO\":\"SIN MODIFICAR\",\n";
                                retorno += "         \"MOTIVO\":\"No se mando ningun parametro para modificar\"\n      }";
                            } else {
                                retorno += modificandoComponente(mapeado, posicion, posicion_componente);
                            }
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Ya existe un componente con el id que se envio\"\n      }";
                        }
                    } else {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"No se puede agregar un componente a un formulario que no existe\"\n      }";
                    }
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No se puede modificar el componente si no se especifica ninguno de los siguientes (ID,FORMULARIO)\"\n      }";
                }
                if ((j + 1) != modificarForm.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificandoComponente(Map<String, String> mapeado, int posicion, int posicion_componente) {
        String retorno = "";
        Componente nuevo = formsDB.get(posicion).getComponentes().get(posicion_componente);
        nuevo.setId(mapeado.get("ID"));
        nuevo.setFormulario(mapeado.get("FORMULARIO"));
        if (mapeado.containsKey("CLASE")) {
            nuevo.setClase(mapeado.get("CLASE"));
        }
        if (mapeado.containsKey("TEXTO_VISIBLE")) {
            nuevo.setTexto_visible(mapeado.get("TEXTO_VISIBLE"));
        }
        if (mapeado.containsKey("INDICE")) {
            nuevo.setIndice(Integer.parseInt(mapeado.get("INDICE")));
        }
        if (mapeado.containsKey("NOMBRE_CAMPO")) {
            nuevo.setNombre_campo(mapeado.get("NOMBRE_CAMPO"));
        }
        if (mapeado.containsKey("ALINEACION")) {
            nuevo.setAlineacion(mapeado.get("ALINEACION"));
        }
        if (mapeado.containsKey("REQUERIDO")) {
            nuevo.setRequerido(mapeado.get("REQUERIDO"));
        }
        if (mapeado.containsKey("OPCIONES")) {
            ArrayList<String> lista_opciones = new ArrayList<>();
            String opciones = mapeado.get("OPCIONES");
            String partes[] = opciones.split("\n");
            lista_opciones.addAll(Arrays.asList(partes));
            nuevo.setOpciones(lista_opciones);
        }
        if (mapeado.containsKey("FILAS")) {
            nuevo.setFilas(Integer.parseInt(mapeado.get("FILAS")));
        }
        if (mapeado.containsKey("COLUMNAS")) {
            nuevo.setColumnas(Integer.parseInt(mapeado.get("COLUMNAS")));
        }
        if (mapeado.containsKey("URL")) {
            nuevo.setUrl(mapeado.get("URL"));
        }
        retorno += modificarElComponente(nuevo, posicion, posicion_componente);
        return retorno;
    }

    public String modificarElComponente(Componente mapeado, int pos, int pos_componente) {
        String retorno = "";
        Componente nuevo = new Componente();
        nuevo.setId(mapeado.getId());
        nuevo.setFormulario(mapeado.getFormulario());
        nuevo.setTexto_visible(mapeado.getTexto_visible());
        nuevo.setClase(mapeado.getClase());
        switch (mapeado.getClase()) {
            case "BOTON":
                formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                break;
            case "IMAGEN":
                if (!mapeado.getUrl().isEmpty()) {
                    nuevo.setUrl(mapeado.getUrl());
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el url\"\n      }";
                }
                break;
            case "CAMPO_TEXTO":
                if (!mapeado.getNombre_campo().isEmpty()) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            case "AREA_TEXTO":
                if (!mapeado.getNombre_campo().isEmpty()
                        && (mapeado.getFilas() >= 0)
                        && (mapeado.getColumnas() >= 0)) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    nuevo.setFilas(mapeado.getFilas());
                    nuevo.setColumnas(mapeado.getColumnas());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            case "CHECKBOX":
                if (!mapeado.getNombre_campo().isEmpty()
                        && !mapeado.getOpciones().isEmpty()) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    nuevo.setOpciones(mapeado.getOpciones());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "RADIO":
                if (!mapeado.getNombre_campo().isEmpty()
                        && !mapeado.getOpciones().isEmpty()) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    nuevo.setOpciones(mapeado.getOpciones());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "COMBO":
                if (!mapeado.getNombre_campo().isEmpty()
                        && !mapeado.getOpciones().isEmpty()) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    nuevo.setOpciones(mapeado.getOpciones());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta alguno de los siguientes parametros (NOMBRE_CAMPO, OPCIONES)\"\n      }";
                }
                break;
            case "FICHERO":
                if (!mapeado.getNombre_campo().isEmpty()) {
                    nuevo.setNombre_campo(mapeado.getNombre_campo());
                    if (!mapeado.getAlineacion().isEmpty()) {
                        nuevo.setAlineacion(mapeado.getAlineacion());
                    }
                    if (!mapeado.getRequerido().isEmpty()) {
                        nuevo.setRequerido(mapeado.getRequerido());
                    }
                    formsDB.get(pos).getComponentes().set(pos_componente, nuevo);
                    retorno += cambiarIndice(pos, pos_componente, mapeado.getIndice());
                    retorno += "         \"ESTADO\":\"COMPONENTE MODIFICADO\"\n      }";
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el nombre del campo\"\n      }";
                }
                break;
            default:
                break;
        }
        return retorno;
    }

    public String cambiarIndice(int pos, int pos_comp, int indice) {
        ArrayList<Componente> temporal = new ArrayList<>();
        String retorno = "";
        if (indice != -1) {
            if (indice > 0) {
                if (indice <= formsDB.get(pos).getComponentes().size()) {
                    indice = indice - 1;
                    if (indice != pos_comp) {
                        if (indice > pos_comp) {
                            ArrayList<Componente> temporal_antes = new ArrayList<>();
                            ArrayList<Componente> temporal_despues = new ArrayList<>();
                            for (int i = 0; i < pos_comp; i++) {
                                temporal_antes.add(formsDB.get(pos).getComponentes().get(i));
                            }
                            for (int i = (indice + 1); i < formsDB.get(pos).getComponentes().size(); i++) {
                                temporal_despues.add(formsDB.get(pos).getComponentes().get(i));
                            }
                            for (int i = (pos_comp + 1); i < (indice + 1); i++) {
                                temporal_antes.add(formsDB.get(pos).getComponentes().get(i));
                            }
                            temporal_antes.add(formsDB.get(pos).getComponentes().get(pos_comp));
                            temporal_antes.addAll(temporal_despues);
                            formsDB.get(pos).setComponentes(temporal_antes);
                        } else {
                            int conteo = 0;
                            for (int i = 0; i < (formsDB.get(pos).getComponentes().size() + 1); i++) {
                                if (indice == i) {
                                    temporal.add(formsDB.get(pos).getComponentes().get(pos_comp));
                                } else {
                                    if (conteo != pos_comp) {
                                        temporal.add(formsDB.get(pos).getComponentes().get(conteo));
                                    }
                                    conteo++;
                                }
                            }
                            formsDB.get(pos).setComponentes(temporal);
                        }
                        retorno += "         \"NOTA\":\"Fue cambiado el indice del componente de " + pos_comp + " a " + indice + "\",\n";
                    } else {
                        retorno += "         \"NOTA\":\"No se modifico el indice, dado que mandaste el mismo que tenia\",\n";
                    }
                } else {
                    retorno += "         \"NOTA\":\"El indice al que intentas mover el componente no existe\",\n";
                }
            } else {
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"El indice debe ser mayor que 0\"\n      }";
            }
        }
        return retorno;
    }

    public String eliminarComponente(Solicitud eliminarComp) {
        String retorno = "   <!ini_respuesta:\"ELIMINAR_COMPONENTE\">\n      {\"PARAMETROS_COMPONENTE\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < eliminarComp.getCuantas().size(); j++) {
            Map<String, String> mapeado = eliminarComp.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")
                        && mapeado.containsKey("FORMULARIO")) {
                    int posicion = -1;
                    for (int i = 0; i < formsDB.size(); i++) {
                        if (formsDB.get(i).getId().equals(mapeado.get("FORMULARIO"))) {
                            posicion = i;
                            break;
                        }
                    }
                    if (posicion != -1) {
                        ArrayList<Componente> componentes = formsDB.get(posicion).getComponentes();
                        int posicion_componente = -1;
                        for (int i = 0; i < componentes.size(); i++) {
                            if (componentes.get(i).getId().equals(mapeado.get("ID"))) {
                                posicion_componente = i;
                                break;
                            }
                        }
                        if (posicion_componente != -1) {
                            formsDB.get(posicion).getComponentes().remove(posicion_componente);
                            retorno += "         \"ESTADO\":\"COMPONENTE ELIMINADO\"\n      }";
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"No existe el componente que tratas de eliminar\"\n      }";
                        }
                    } else {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"No existe el formulario que contiene el componente que tratas de eliminar\"\n      }";
                    }
                } else {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No se puede eliminar un componente sin alguno de los siguientes parametros ID,FORMULARIO\"\n      }";
                }
                if ((j + 1) != eliminarComp.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificarFormulario(Solicitud modificarForm) {
        String retorno = "   <!ini_respuesta:\"MODIFICAR_FORMULARIO\">\n      {\"PARAMETROS_FORMULARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < modificarForm.getCuantas().size(); j++) {
            Map<String, String> mapeado = modificarForm.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")) {
                    if (idsUs.isEmpty()) {
                        idsUs.add(mapeado.get("ID"));
                        if (mapeado.containsKey("ID")
                                && (mapeado.containsKey("TEMA") || mapeado.containsKey("TITULO") || mapeado.containsKey("NOMBRE"))) {
                            retorno += modificandoFormulario(mapeado);
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Faltan algun parametro para modificar (TITULO, TEMA, NOMBRE)\"\n      }";
                        }
                    } else {
                        if (idsUs.contains(mapeado.get("ID"))) {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"El ID del formulario que se intenta ingresar ya existe\"\n      }";
                        } else {
                            idsUs.add(mapeado.get("ID"));
                            if (mapeado.containsKey("ID")
                                    && (mapeado.containsKey("TEMA") || mapeado.containsKey("TITULO") || mapeado.containsKey("NOMBRE"))) {
                                retorno += modificandoFormulario(mapeado);
                            } else {
                                retorno += "         \"ESTADO\":\"ERROR\",\n";
                                retorno += "         \"DESCRIPCION_ERROR\":\"Faltan algun parametro para modificar (TITULO, TEMA, NOMBRE)\"\n      }";
                            }
                        }
                    }
                } else {
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"No se puede crear el formulario sin alguno de los siguientes parametros (ID,TITULO,NOMBRE,TEMA)\"\n      }";
                }
                if ((j + 1) != modificarForm.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }

        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificandoFormulario(Map<String, String> mapeado) {
        String retorno = "";
        String modificados = "";
        int posicion = -1;
        for (int i = 0; i < formsDB.size(); i++) {
            if (formsDB.get(i).getId().equals(mapeado.get("ID"))) {
                posicion = i;
            }
        }
        if (posicion != -1) {
            if (mapeado.containsKey("TITULO")) {
                formsDB.get(posicion).setTitulo(mapeado.get("TITULO"));
                modificados += "TITULO-";
            }
            if (mapeado.containsKey("TEMA")) {
                formsDB.get(posicion).setTema(mapeado.get("TEMA"));
                modificados += "TEMA-";
            }
            if (mapeado.containsKey("NOMBRE")) {
                formsDB.get(posicion).setNombre(mapeado.get("NOMBRE"));
                modificados += "NOMBRE-";
            }
            retorno += "         \"ESTADO\":\"FORMULARIO MODIFICADO\",\n";
            retorno += "         \"NOTA\":\"Los siguientes parametros del formulario " + mapeado.get("ID") + " fueron modificados " + modificados + "\"\n      }";
        } else {
            retorno += "         \"ESTADO\":\"ERROR\",\n";
            retorno += "         \"DESCRIPCION_ERROR\":\"No existe el formulario que intentas modificar\"\n      }";
        }
        return retorno;
    }

    public String modificarUsuario(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"MODIFICAR_USUARIO\">\n      {\"CREDENCIALES_USUARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < crearU.getCuantas().size(); j++) {
            Map<String, String> mapeado = crearU.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("USUARIO_ANTIGUO")) {
                    if (idsUs.isEmpty()) {
                        idsUs.add(mapeado.get("USUARIO_ANTIGUO"));
                        if (mapeado.containsKey("USUARIO_NUEVO") && mapeado.containsKey("CONTRA_NUEVA")) {
                            if (mapeado.containsKey("FECHA_MODIFICACION")) {
                                retorno += modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"), mapeado.get("USUARIO_NUEVO"), mapeado.get("CONTRA_NUEVA"), mapeado.get("FECHA_MODIFICACION"));
                            } else {
                                retorno += modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"), mapeado.get("USUARIO_NUEVO"), mapeado.get("CONTRA_NUEVA"), fechaActual());
                            }
                        } else {
                            crearU.getCuantas().get(j).put("ERROR", "FALTAN");
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"Faltan alguno de los siguientes parametros obligatorios USUARIO_NUEVO, NUEVO_PASSWORD\"\n      }";
                        }
                    } else {
                        if (idsUs.contains(mapeado.get("USUARIO_ANTIGUO"))) {
                            crearU.getCuantas().get(j).put("ERROR", "REPETIDO");
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"El usuario que se intenta modificar ya fue solicitado previamente en esta misma solicitud\"\n      }";
                        } else {
                            idsUs.add(mapeado.get("USUARIO_ANTIGUO"));
                            if (mapeado.containsKey("USUARIO_NUEVO") && mapeado.containsKey("CONTRA_NUEVA")) {
                                if (mapeado.containsKey("FECHA_MODIFICACION")) {
                                    retorno += modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"), mapeado.get("USUARIO_NUEVO"), mapeado.get("CONTRA_NUEVA"), mapeado.get("FECHA_MODIFICACION"));
                                } else {
                                    retorno += modificandoUsuario(mapeado.get("USUARIO_ANTIGUO"), mapeado.get("USUARIO_NUEVO"), mapeado.get("CONTRA_NUEVA"), fechaActual());
                                }
                            } else {
                                crearU.getCuantas().get(j).put("ERROR", "FALTAN");
                                retorno += "         \"ESTADO\":\"ERROR\",\n";
                                retorno += "         \"DESCRIPCION_ERROR\":\"Faltan alguno de los siguientes parametros obligatorios USUARIO_NUEVO, NUEVO_PASSWORD\"\n      }";
                            }
                        }
                    }
                } else {
                    crearU.getCuantas().get(j).put("ERROR", "FALTA");
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el parametro más importante (USUARIO_ANTIGUO)\"\n      }";
                }
                if ((j + 1) != crearU.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }

        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String modificandoUsuario(String usuario_antiguo, String usuario_nuevo, String contra_nueva, String fecha_modificacion) {
        String retorno = "";
        if (!usuario_antiguo.equals(usuario_nuevo)) {
            int posicion = -1;
            int posicion2 = -1;
            for (int i = 0; i < usuariosDB.size(); i++) {
                if (usuariosDB.get(i).getUsuario().equals(usuario_antiguo)) {
                    posicion = i;
                }
                if (usuariosDB.get(i).getUsuario().equals(usuario_nuevo)) {
                    posicion2 = i;
                }
            }
            if (posicion != -1 && posicion2 == -1) {
                usuariosDB.get(posicion).setUsuario(usuario_nuevo);
                usuariosDB.get(posicion).setPassword(contra_nueva);
                usuariosDB.get(posicion).setFecha_mod(fecha_modificacion);
                retorno += "         \"ESTADO\":\"USUARIO MODIFICADO\"\n      }";
            } else {
                if (posicion == -1) {
                    retorno += "         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"El usuario que tratas de modificar no existe en la base de datos\"\n      }";
                } else {
                    if (posicion2 != -1) {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"El nuevo usuario ya existe en la base de datos\"\n      }";
                    }
                }
            }
        } else {
            int posicion = -1;
            for (int i = 0; i < usuariosDB.size(); i++) {
                if (usuariosDB.get(i).getUsuario().equals(usuario_antiguo)) {
                    posicion = i;
                }
            }
            if (posicion != -1) {
                usuariosDB.get(posicion).setUsuario(usuario_nuevo);
                usuariosDB.get(posicion).setPassword(contra_nueva);
                usuariosDB.get(posicion).setFecha_mod(fecha_modificacion);
                retorno += "         \"ESTADO\":\"USUARIO MODIFICADO\"\n      }";
            } else {
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"El usuario que tratas de modificar no existe en la base de datos\"\n      }";
            }

        }
        return retorno;
    }

    public String eliminarFormulario(Solicitud eliminarForm) {
        String retorno = "   <!ini_respuesta:\"ELIMINAR_FORMULARIO\">\n      {\"PARAMETROS_FORMULARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < eliminarForm.getCuantas().size(); j++) {
            Map<String, String> mapeado = eliminarForm.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("ID")) {
                    if (idsUs.contains(mapeado.get("ID"))) {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"Solicitud de eliminacion repetida\"\n      }";
                    } else {
                        idsUs.add(mapeado.get("ID"));
                        int posicion = -1;
                        for (int i = 0; i < formsDB.size(); i++) {
                            if (formsDB.get(i).getId().equals(mapeado.get("ID"))) {
                                posicion = i;
                            }
                        }
                        if (posicion != -1) {
                            formsDB.remove(posicion);
                            retorno += "         \"ESTADO\":\"FORMULARIO ELIMINADO\"\n      }";
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"No existe el formulario que intentas eliminar\"\n      }";
                        }
                    }
                } else {
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el parametro más importante (ID)\"\n      }";
                }
                if ((j + 1) != eliminarForm.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
        return retorno;
    }

    public String eliminarUsuario(Solicitud crearU) {
        String retorno = "   <!ini_respuesta:\"ELIMINAR_USUARIO\">\n      {\"CREDENCIALES_USUARIO\":[\n";
        ArrayList<String> idsUs = new ArrayList<>();
        for (int j = 0; j < crearU.getCuantas().size(); j++) {
            Map<String, String> mapeado = crearU.getCuantas().get(j);
            if (!mapeado.containsKey("ERROR")) {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviados(mapeado);
                if (mapeado.containsKey("USUARIO")) {
                    if (idsUs.contains(mapeado.get("USUARIO"))) {
                        retorno += "         \"ESTADO\":\"ERROR\",\n";
                        retorno += "         \"DESCRIPCION_ERROR\":\"Solicitud de eliminacion repetida\"\n      }";
                    } else {
                        int posicion = -1;
                        for (int i = 0; i < usuariosDB.size(); i++) {
                            if (usuariosDB.get(i).getUsuario().equals(mapeado.get("USUARIO"))) {
                                posicion = i;
                            }
                        }
                        if (posicion != -1) {
                            usuariosDB.remove(posicion);
                            retorno += "         \"ESTADO\":\"USUARIO ELIMINADO\"\n      }";
                        } else {
                            retorno += "         \"ESTADO\":\"ERROR\",\n";
                            retorno += "         \"DESCRIPCION_ERROR\":\"No existe el usuario que se intenta eliminar\"\n      }";
                        }
                    }
                } else {
                    retorno += "      {\n         \"ESTADO\":\"ERROR\",\n";
                    retorno += "         \"DESCRIPCION_ERROR\":\"Falta el parametro más importante (USUARIO)\"\n      }";
                }
                if ((j + 1) != crearU.getCuantas().size()) {
                    retorno += ",\n";
                } else {
                    retorno += "\n";
                }
            } else {
                retorno += "      {\n";
                retorno += obtenerParametrosEnviadosConRepetidos(mapeado);
                retorno += "         \"ESTADO\":\"ERROR\",\n";
                retorno += "         \"DESCRIPCION_ERROR\":\"Existen parametros repetidos en la solicitud\"\n      }\n";
            }
        }
        retorno += "         ]\n      }\n   <fin_respuesta!>\n";
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
            switch (temp.getFecha()) {
                case "FALTA":
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"ERROR\":\"Hacen falta parametros importantes(USUARIO,PASSWORD)\"\n         }";
                    break;
                case "REPETIDO":
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"DESCRIPCION_ERROR\":\"Este usuario ya fue solicitado para su creacion en esta misma solicitud\"\n         }";
                    break;
                case "EXISTE":
                    retorno += cT + "\"ESTADO\":\"ERROR\",\n";
                    retorno += cT + "\"DESCRIPCION_ERROR\":\"Este usuario ya existe en la base de datos\"\n         }";
                    break;
                default:
                    retorno += cT + "\"ESTADO\":\"USUARIO CREADO\"\n         }";
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
                        posibles += "\t\t\t\"INDICE\":\"" + (conteo + 1) + "\",\n";
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

    public String obtenerParametrosEnviados(Map<String, String> mapeado) {
        String ingresados = "         \"PARAMETROS_ENVIADOS\":\n                 {\n";
        ArrayList<String> llaves = new ArrayList<>();
        ArrayList<String> valores = new ArrayList<>();
        for (Map.Entry<String, String> entry : mapeado.entrySet()) {
            llaves.add(entry.getKey());
            valores.add(entry.getValue());
        }
        for (int i = (llaves.size() - 1); i >= 0; i--) {
            ingresados += "            \t\"" + llaves.get(i) + "\": \"" + valores.get(i) + "\"";
            if (i == 0) {
                ingresados += "\n";
            } else {
                ingresados += ",\n";
            }
        }
        ingresados += "                 },\n";
        return ingresados;
    }

    public String obtenerParametrosEnviadosConRepetidos(Map<String, String> mapeado) {
        String ingresados = "         \"PARAMETROS_ENVIADOS\":\n                 {\n";
        ArrayList<String> llaves = new ArrayList<>();
        ArrayList<String> valores = new ArrayList<>();
        for (Map.Entry<String, String> entry : mapeado.entrySet()) {
            if (!entry.getKey().equals("ERROR")) {
                String partes[];
                if (entry.getKey().equals("OPCIONES")) {
                    partes = entry.getValue().split("\t");
                } else {
                    partes = entry.getValue().split("\n");
                }
                if (partes.length > 1) {
                    for (int i = 0; i < partes.length; i++) {
                        if (entry.getKey().equals("OPCIONES")) {
                            llaves.add(entry.getKey() + "-REPETIDO");
                            valores.add(partes[i].replace('\n', '|'));
                        } else {
                            llaves.add(entry.getKey() + "-REPETIDO");
                            valores.add(partes[i]);
                        }
                    }
                } else {
                    if (entry.getKey().equals("OPCIONES")) {
                        llaves.add(entry.getKey());
                        valores.add(entry.getValue().replace('\n', '|'));
                    } else {
                        llaves.add(entry.getKey());
                        valores.add(entry.getValue());
                    }
                }
            }
        }
        for (int i = (llaves.size() - 1); i >= 0; i--) {
            ingresados += "            \t\"" + llaves.get(i) + "\": \"" + valores.get(i) + "\"";
            if (i == 0) {
                ingresados += "\n";
            } else {
                ingresados += ",\n";
            }
        }
        ingresados += "                 },\n";
        return ingresados;
    }

    public String fechaActual() {
        java.util.Date fecha = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // Aqui usamos la instancia formatter para darle el formato a la fecha. Es importante ver que el resultado es un string.
        return formatter.format(fecha);
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(String usuarioActual) {
        this.usuarioActual = usuarioActual;
    }
}
