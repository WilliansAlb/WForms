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
public class Solicitud {
    private String tipo;
    private boolean tieneErrores;
    private ArrayList<Map<String,String>> cuantas;
    private String descripcion_error;
    
    public Solicitud(){
        tipo = "";
        tieneErrores = false;
        cuantas = new ArrayList<>();
        descripcion_error = "";
    }
    
    public Solicitud(String tipo, boolean tieneErrores, ArrayList<Map<String,String>> cuantas, String descripcion_error){
        this.tipo = tipo;
        this.tieneErrores = tieneErrores;
        this.cuantas = cuantas;
        this.descripcion_error = descripcion_error;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isTieneErrores() {
        return tieneErrores;
    }

    public void setTieneErrores(boolean tieneErrores) {
        this.tieneErrores = tieneErrores;
    }

    public ArrayList<Map<String, String>> getCuantas() {
        return cuantas;
    }

    public void setCuantas(ArrayList<Map<String, String>> cuantas) {
        this.cuantas = cuantas;
    }

    public String getDescripcion_error() {
        return descripcion_error;
    }

    public void setDescripcion_error(String descripcion_error) {
        this.descripcion_error = descripcion_error;
    }
    
}
