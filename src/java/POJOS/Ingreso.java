/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POJOS;

/**
 *
 * @author willi
 */
public class Ingreso {
    private String idc;
    private String nombrec;
    private String dato;
    private String tipo;
    
    public Ingreso (){
        idc = "";
        nombrec = "";
        dato = "";
        tipo = "";
    }

    public Ingreso(String idc, String nombrec, String dato) {
        this.idc = idc;
        this.nombrec = nombrec;
        this.dato = dato;
    }
    

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getNombrec() {
        return nombrec;
    }

    public void setNombrec(String nombrec) {
        this.nombrec = nombrec;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
}
