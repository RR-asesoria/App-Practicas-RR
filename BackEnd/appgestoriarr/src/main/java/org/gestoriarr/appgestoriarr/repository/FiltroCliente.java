package org.gestoriarr.appgestoriarr.repository;

public class FiltroCliente {

    private Object valorIgual;
    private String valorParcial;
    private Object valorMin;
    private Object valorMax;

    public FiltroCliente() {}

    public Object getValorIgual() { return valorIgual; }
    public void setValorIgual(Object valorIgual) { this.valorIgual = valorIgual; }
    public String getValorParcial() { return valorParcial; }
    public void setValorParcial(String valorParcial) { this.valorParcial = valorParcial; }
    public Object getValorMin() { return valorMin; }
    public void setValorMin(Object valorMin) { this.valorMin = valorMin; }
    public Object getValorMax() { return valorMax; }
    public void setValorMax(Object valorMax) { this.valorMax = valorMax; }
}