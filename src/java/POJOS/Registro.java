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
public class Registro {
    private String noregistro;
    private ArrayList<Ingreso> valores;
    private String id;
    private String nombre;
    private String form;
    private String nombref;
    private String registro;

    public Registro() {
        noregistro = "";
        valores = new ArrayList<>();
    }

    public Registro(String noregistro, ArrayList<Ingreso> valores) {
        this.noregistro = noregistro;
        this.valores = valores;
    }
    
    
    
    public String getNoregistro() {
        return noregistro;
    }

    public void setNoregistro(String noregistro) {
        this.noregistro = noregistro;
    }

    public ArrayList<Ingreso> getValores() {
        return valores;
    }

    public void setValores(ArrayList<Ingreso> valores) {
        this.valores = valores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getNombref() {
        return nombref;
    }

    public void setNombref(String nombref) {
        this.nombref = nombref;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

}
