package com.vitralux.domain;

public enum Rol {

    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    COMPRAS("Encargado de compras"),
    PRODUCCION("Encargado de producción"),
    INSTALADOR("Instalador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}