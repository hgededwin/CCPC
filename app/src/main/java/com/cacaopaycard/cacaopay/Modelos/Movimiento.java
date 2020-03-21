package com.cacaopaycard.cacaopay.Modelos;

import android.graphics.Bitmap;

public class Movimiento {

    private Bitmap imagen;
    private String descripcionTransferencia;
    private String fecha;
    private Fecha fechaObject;
    private String montoTransferencia;
    private boolean esComercio;
    private boolean esTransferenciaRecibida;
    private boolean esTransferenciaAut;

    public Movimiento(String descripcion, String fecha, String cantidad,boolean esTransferenciaAut, boolean esTransferenciaRecibida){
        this.fecha = fecha;
        this.descripcionTransferencia = descripcion;
        this.montoTransferencia = cantidad;
        this.esTransferenciaRecibida = esTransferenciaRecibida;
        this.esTransferenciaAut = esTransferenciaAut;

    }

    public Movimiento(String descripcion, String fecha, String cantidad, boolean esTransferenciaRecibida){
        this.fecha = fecha;
        this.descripcionTransferencia = descripcion;
        this.montoTransferencia = cantidad;
        this.esTransferenciaRecibida = esTransferenciaRecibida;

    }

    public Movimiento(String descripcion, Fecha fecha, String cantidad, boolean esTransferenciaRecibida){
        this.fechaObject = fecha;
        this.descripcionTransferencia = descripcion;
        this.montoTransferencia = cantidad;
        this.esTransferenciaRecibida = esTransferenciaRecibida;

    }

    public String getDescripcionTransferencia() {
        return descripcionTransferencia;
    }

    public void setDescripcionTransferencia(String descripcionTransferencia) {
        this.descripcionTransferencia = descripcionTransferencia;
    }

    public Fecha getFecha() {
        return fechaObject;
    }

    public void setFecha(Fecha fecha) {
        this.fechaObject = fecha;
    }

    public String getMontoTransferencia() {
        return montoTransferencia;
    }

    public void setMontoTransferencia(String montoTransferencia) {
        this.montoTransferencia = montoTransferencia;
    }


    public boolean getEsTransferenciaRecibida() {
        return esTransferenciaRecibida;
    }

    public void setEsTransferenciaHecha(boolean esTransferenciaRecibida) {
        this.esTransferenciaRecibida = esTransferenciaRecibida;
    }

    public boolean getEsTransferenciaAut() {
        return esTransferenciaAut;
    }

    public void setEsTransferenciaAut(boolean esTransferenciaAut) {
        this.esTransferenciaAut = esTransferenciaAut;
    }

    public boolean esComercio() {
        return esComercio;
    }

    public void setEsComercio(boolean esComercio) {

        this.esComercio = esComercio;
    }
}
