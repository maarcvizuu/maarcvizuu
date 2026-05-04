package com.mycompany.cuinalab.model;

import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.enums.TipusCurs;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe abstracta que representa un curs de l'escola CuinaLab.
 *
 * És la superclasse de {@link CursPresencial} i {@link CursOnline}.
 * Conté els atributs i mètodes comuns a tots els cursos i defineix
 * mètodes abstractes que cada subclasse haurà d'implementar.
 *
 * @author marcvizuu
 */
public abstract class Curs {

    protected String codi;
    protected String nom;
    protected int placesMaximes;
    protected double preu;
    protected int sessions;
    protected Map<String, Alumne> inscrits;

    public Curs(String codi, String nom, int placesMaximes, double preu, int sessions) throws CuinaLabException {
        if (placesMaximes < 5 || placesMaximes > 20) {
            throw new CuinaLabException("Les places han d'estar entre 5 i 20.");
        }
        if (preu <= 0) {
            throw new CuinaLabException("El preu ha de ser un número positiu.");
        }
        if (sessions < 1 || sessions > 12) {
            throw new CuinaLabException("Les sessions han d'estar entre 1 i 12.");
        }
        this.codi = codi.toUpperCase();
        this.nom = nom;
        this.placesMaximes = placesMaximes;
        this.preu = preu;
        this.sessions = sessions;
        this.inscrits = new HashMap<>();
    }

    public String getCodi() {
        return codi;
    }

    public String getNom() {
        return nom;
    }

    public int getPlacesMaximes() {
        return placesMaximes;
    }

    public double getPreu() {
        return preu;
    }

    public int getSessions() {
        return sessions;
    }

    public int getPlacesOcupades() {
        return inscrits.size();
    }

    public int getPlacesLliures() {
        return placesMaximes - inscrits.size();
    }

    public boolean tePlace() {
        return inscrits.size() < placesMaximes;
    }

    public boolean estaInscrit(String dni) {
        return inscrits.containsKey(dni.toUpperCase());
    }

    public void inscriureAlumne(Alumne a) throws CuinaLabException {
        if (!tePlace()) {
            throw new CuinaLabException("El curs " + codi + " ja té totes les places ocupades.");
        }
        if (estaInscrit(a.getDni())) {
            throw new CuinaLabException("L'alumne ja està inscrit al curs.");
        }
        inscrits.put(a.getDni(), a);
    }

    /**
     * Tipus del curs (PRESENCIAL o ONLINE).
     * @return el tipus del curs
     */
    public abstract TipusCurs getTipus();

    /**
     * Línia d'informació del curs amb el format demanat per l'opció Info Alumne.
     * Exemple: "PAS01 - Pastisseria Francesa (Presencial) - 149.9€ - DIMARTS - 6 sessions"
     * @return la línia d'informació formatada
     */
    public abstract String getInfoCursAlumne();

    /**
     * Línia d'informació del curs amb el format demanat per l'opció Veure Escola.
     * Exemple: "PAS01 - ... | Places: 1/5"
     * @return la línia d'informació formatada amb places ocupades/totals
     */
    public String getInfoCursEscola() {
        return getInfoCursAlumne() + " | Places: " + getPlacesOcupades() + "/" + placesMaximes;
    }
}
