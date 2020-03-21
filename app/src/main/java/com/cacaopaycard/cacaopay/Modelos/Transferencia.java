package com.cacaopaycard.cacaopay.Modelos;

import java.io.Serializable;

public class Transferencia implements Serializable {
    private String fecha, hora, nombredestino, cuentaDestino, monto, numeroRastreo, cuentaOrigen;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombredestino() {
        return nombredestino;
    }

    public void setNombredestino(String nombredestino) {
        this.nombredestino = nombredestino;
    }

    public String getCuentaDestino() {
        if (cuentaDestino.length() == 18){
            return "**** " + cuentaDestino.substring(14);
        } else{
            return "**** " + cuentaDestino.substring(12);
        }

    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getNumeroRastreo() {
        return numeroRastreo;
    }

    public void setNumeroRastreo(String numeroRastreo) {
        this.numeroRastreo = numeroRastreo;
    }

    public String getCuentaOrigen() {
        return "**** " + cuentaOrigen.substring(12);
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }
}

