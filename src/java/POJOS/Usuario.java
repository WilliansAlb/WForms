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
public class Usuario {
    private String usuario;
    private String password;
    private String fecha;
    private String fecha_mod;
    
    public Usuario(){
        usuario = "";
        password = "";
        fecha = "";
        fecha_mod = "";
    }

    public Usuario(String usuario, String password, String fecha, String fecha_mod) {
        this.usuario = usuario;
        this.password = password;
        this.fecha = fecha;
        this.fecha_mod = fecha_mod;
    }
    
    public Usuario(String usuario, String password, String fecha) {
        this.usuario = usuario;
        this.password = password;
        this.fecha = fecha;
        this.fecha_mod = "";
    }

    public String getFecha_mod() {
        return fecha_mod;
    }

    public void setFecha_mod(String fecha_mod) {
        this.fecha_mod = fecha_mod;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    
}
