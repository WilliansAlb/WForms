/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POJOS;

import java.util.ArrayList;

/**
 *
 * @author willi
 */
public class Componente {
    private String id;
    private String nombre_campo;
    private String formulario;
    private String clase;
    private int indice;
    private String texto_visible;
    private String alineacion;
    private String requerido;
    private ArrayList<String> opciones;
    private int filas;
    private int columnas;
    private String url;

    public Componente() {
        id = "";
        nombre_campo = "";
        formulario = "";
        clase = "";
        indice = -1;
        texto_visible = "";
        alineacion = "";
        requerido = "";
        opciones = new ArrayList<>();
        filas = -1;
        columnas = -1;
        url = "";
    }

    public Componente(String id, String nombre_campo, String formulario, String clase, int indice, String texto_visible, String alineacion, String requerido, ArrayList<String> opciones, int filas, int columnas, String url) {
        this.id = id;
        this.nombre_campo = nombre_campo;
        this.formulario = formulario;
        this.clase = clase;
        this.indice = indice;
        this.texto_visible = texto_visible;
        this.alineacion = alineacion;
        this.requerido = requerido;
        this.opciones = opciones;
        this.filas = filas;
        this.columnas = columnas;
        this.url = url;
    }

    public ArrayList<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(ArrayList<String> opciones) {
        this.opciones = opciones;
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_campo() {
        return nombre_campo;
    }

    public void setNombre_campo(String nombre_campo) {
        this.nombre_campo = nombre_campo;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getTexto_visible() {
        return texto_visible;
    }

    public void setTexto_visible(String texto_visible) {
        this.texto_visible = texto_visible;
    }

    public String getAlineacion() {
        return alineacion;
    }

    public void setAlineacion(String alineacion) {
        this.alineacion = alineacion;
    }

    public String getRequerido() {
        return requerido;
    }

    public void setRequerido(String requerido) {
        this.requerido = requerido;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
