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
public class Formulario {
    private String id;
    private String titulo;
    private String nombre;
    private String tema;
    private String usuario;
    private String fecha;
    private ArrayList<Componente> componentes;
    private ArrayList<Registro> registros;
    
    public Formulario() {
        id = "";
        titulo = "";
        nombre = "";
        tema = "";
        usuario = "";
        fecha = "";
        componentes = new ArrayList<>();
        registros = new ArrayList<>();
    }

    public Formulario(String id, String titulo, String nombre, String tema, String usuario, String fecha, ArrayList<Componente> componentes, ArrayList<Registro> registros) {
        this.id = id;
        this.titulo = titulo;
        this.nombre = nombre;
        this.tema = tema;
        this.usuario = usuario;
        this.fecha = fecha;
        this.componentes = componentes;
        this.registros = registros;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public ArrayList<Componente> getComponentes() {
        return componentes;
    }

    public void setComponentes(ArrayList<Componente> componentes) {
        this.componentes = componentes;
    }

    public ArrayList<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(ArrayList<Registro> registros) {
        this.registros = registros;
    }
}
