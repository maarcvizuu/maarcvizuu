package model;

public class CursPresencial extends Curs {

    private String aula;
    private String diaSemana;

    public CursPresencial(String codi, String nom, int placesMaximes, double preu, int sessions,
                          String aula, String diaSemana) {
        super(codi, nom, placesMaximes, preu, sessions);
        this.aula = aula;
        this.diaSemana = diaSemana.toUpperCase();
    }

    public String getAula() { return aula; }
    public String getDiaSemana() { return diaSemana; }

    @Override
    public String getTipus() {
        return "Presencial";
    }

    @Override
    public String getInfoAlumne() {
        return codi + " - " + nom + " (Presencial) - " + preu + "€ - " + diaSemana + " - " + sessions + " sessions";
    }

    @Override
    public String getInfoEscola() {
        return codi + " - " + nom + " (Presencial) - " + preu + "€ - " + diaSemana + " - " + sessions
                + " sessions | Places: " + getPlacesOcupades() + "/" + placesMaximes;
    }
}
