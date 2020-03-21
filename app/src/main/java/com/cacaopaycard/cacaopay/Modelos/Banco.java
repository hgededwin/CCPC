package com.cacaopaycard.cacaopay.Modelos;

public class Banco {

    private String nombreBanco;
    private int claveBanco;


    public Banco(String nombre, int clave){
        this.nombreBanco = nombre;
        this.claveBanco = clave;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public int getClaveBanco() {
        return claveBanco;
    }

    public void setClaveBanco(int claveBanco) {
        this.claveBanco = claveBanco;
    }

    @Override
    public String toString() {
        return nombreBanco;
    }
}
