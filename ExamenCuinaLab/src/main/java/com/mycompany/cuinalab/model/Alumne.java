package com.mycompany.cuinalab.model;

/**
 * Entitat Alumne de l'escola CuinaLab.
 *
 * @author marcvizuu
 */
public class Alumne {

    private String dni;
    private String nom;
    private String cognoms;
    private int edat;

    public Alumne(String dni, String nom, String cognoms, int edat) {
        this.dni = dni.toUpperCase();
        this.nom = nom;
        this.cognoms = cognoms;
        this.edat = edat;
    }

    public String getDni() {
        return dni;
    }

    public String getNom() {
        return nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public int getEdat() {
        return edat;
    }

    public boolean esMenorEdat() {
        return edat < 18;
    }
}
