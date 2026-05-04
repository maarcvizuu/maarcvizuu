package model;

public class Alumne {

    private String nif;
    private String nom;
    private String cognoms;
    private int edat;

    public Alumne(String nif, String nom, String cognoms, int edat) {
        this.nif = nif.toUpperCase();
        this.nom = nom;
        this.cognoms = cognoms;
        this.edat = edat;
    }

    public String getNif() { return nif; }
    public String getNom() { return nom; }
    public String getCognoms() { return cognoms; }
    public int getEdat() { return edat; }

    public boolean esMenorEdat() {
        return edat < 18;
    }

    public String getNomComplet() {
        return nom + " " + cognoms;
    }
}
