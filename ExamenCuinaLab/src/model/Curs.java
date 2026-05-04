package model;

import java.util.ArrayList;

public abstract class Curs {

    protected String codi;
    protected String nom;
    protected int placesMaximes;
    protected double preu;
    protected int sessions;
    protected ArrayList<String> alumnesInscrits;

    public Curs(String codi, String nom, int placesMaximes, double preu, int sessions) {
        this.codi = codi.toUpperCase();
        this.nom = nom;
        this.placesMaximes = placesMaximes;
        this.preu = preu;
        this.sessions = sessions;
        this.alumnesInscrits = new ArrayList<>();
    }

    public String getCodi() { return codi; }
    public String getNom() { return nom; }
    public int getPlacesMaximes() { return placesMaximes; }
    public double getPreu() { return preu; }
    public int getSessions() { return sessions; }
    public int getPlacesOcupades() { return alumnesInscrits.size(); }
    public int getPlacesLliures() { return placesMaximes - alumnesInscrits.size(); }

    public boolean tePlace() {
        return alumnesInscrits.size() < placesMaximes;
    }

    public boolean estaInscrit(String nif) {
        return alumnesInscrits.contains(nif.toUpperCase());
    }

    public void inscriureAlumne(String nif) {
        alumnesInscrits.add(nif.toUpperCase());
    }

    public abstract String getTipus();

    // Format per Info Alumne: codi - nom (Tipus) - preu€ - info especifica
    public abstract String getInfoAlumne();

    // Format per Veure Escola: codi - nom (Tipus) - preu€ - info | Places: occ/total
    public abstract String getInfoEscola();
}
