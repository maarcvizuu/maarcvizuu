package model;

public class CursOnline extends Curs {

    private boolean inclouMaterial;
    private String plataforma;

    public CursOnline(String codi, String nom, int placesMaximes, double preu, int sessions,
                      boolean inclouMaterial, String plataforma) {
        super(codi, nom, placesMaximes, preu, sessions);
        this.inclouMaterial = inclouMaterial;
        this.plataforma = plataforma.toUpperCase();
    }

    public boolean isInclouMaterial() { return inclouMaterial; }
    public String getPlataforma() { return plataforma; }

    @Override
    public String getTipus() {
        return "Online";
    }

    @Override
    public String getInfoAlumne() {
        return codi + " - " + nom + " (Online) - " + preu + "€ - " + sessions + " sessions";
    }

    @Override
    public String getInfoEscola() {
        return codi + " - " + nom + " (Online) - " + preu + "€ - " + sessions
                + " sessions | Places: " + getPlacesOcupades() + "/" + placesMaximes;
    }
}
