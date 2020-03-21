package com.cacaopaycard.cacaopay.Modelos;

public class Fecha {

    private String dia;
    private String mes;
    private String anio;

    public Fecha(String dia, String mes, String anio){
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;

    }

    public String getDate(){
        return dia + "/" + mes +"/" + anio;
    }


    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }
}
