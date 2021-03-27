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
    private String id;
    private String nombre;
    private String form;
    private String registro;
    private ArrayList<String> registros;

    public Registro(String id, String nombre, String form, String registro, ArrayList<String> registros) {
        this.id = id;
        this.nombre = nombre;
        this.form = form;
        this.registro = registro;
        this.registros = registros;
    }
    
    

    public ArrayList<String> getRegistros() {
        return registros;
    }

    public void setRegistros(ArrayList<String> registros) {
        this.registros = registros;
    }

    

    public Registro() {
        id = "";
        nombre = "";
        form = "";
        registro = "";
        registros = new ArrayList<>();
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

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }
    
}
