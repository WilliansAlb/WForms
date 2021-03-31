/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POJOS;

import java.util.ArrayList;
import java_cup.runtime.Symbol;

/**
 *
 * @author willi
 */
public class Errores {
    private ArrayList<String> esperados;
    private Symbol encontrado;
    private String nombre;
    private String tipo;

    public Errores() {
    }

    public Errores(ArrayList<String> esperados, Symbol encontrado, String nombre, String tipo) {
        this.esperados = esperados;
        this.encontrado = encontrado;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Errores(ArrayList<String> esperados, Symbol encontrado) {
        this.esperados = esperados;
        this.encontrado = encontrado;
    }

    public ArrayList<String> getEsperados() {
        return esperados;
    }

    public void setEsperados(ArrayList<String> esperados) {
        this.esperados = esperados;
    }

    public Symbol getEncontrado() {
        return encontrado;
    }

    public void setEncontrado(Symbol encontrado) {
        this.encontrado = encontrado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
    
}
