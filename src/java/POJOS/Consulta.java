/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POJOS;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author willi
 */
public class Consulta {
    private String noconsulta;
    private String form;
    private ArrayList<String> campos;
    private ArrayList<String> puentes;
    private ArrayList<Map<String,String>> restricciones;
    
    public Consulta(){
        noconsulta = "";
        form = "";
        campos = new ArrayList<>();
        puentes = new ArrayList<>();
        restricciones = new ArrayList<>();
    }

    public String getNoconsulta() {
        return noconsulta;
    }

    public void setNoconsulta(String noconsulta) {
        this.noconsulta = noconsulta;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public ArrayList<String> getCampos() {
        return campos;
    }

    public void setCampos(ArrayList<String> campos) {
        this.campos = campos;
    }

    public ArrayList<String> getPuentes() {
        return puentes;
    }

    public void setPuentes(ArrayList<String> puentes) {
        this.puentes = puentes;
    }

    public ArrayList<Map<String, String>> getRestricciones() {
        return restricciones;
    }

    public void setRestricciones(ArrayList<Map<String, String>> restricciones) {
        this.restricciones = restricciones;
    }
    
}
