package com.mycompany.cuinalab.controller;

import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.Alumne;
import com.mycompany.cuinalab.model.Curs;
import com.mycompany.cuinalab.persistence.FicheroCuinaLab;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lògica de l'aplicació CuinaLab.
 *
 * Gestiona els cursos i els alumnes de l'escola.
 * Llegeix els alumnes inicials del fitxer al constructor.
 *
 * @author marcvizuu
 */
public class GestorEscola {

    /**
     * Cursos registrats a l'aplicació, indexats pel codi del curs.
     */
    private Map<String, Curs> cursos;

    /**
     * Alumnes registrats a l'aplicació, indexats pel DNI.
     */
    private Map<String, Alumne> alumnes;

    /**
     * Fitxer per a la persistència de l'aplicació.
     */
    private FicheroCuinaLab fichero;

    /**
     * Inicialitza les estructures i carrega els alumnes del fitxer students.txt
     * si existeix. Si no existeix, comença l'aplicació buida sense donar error.
     */
    public GestorEscola() {
        cursos = new HashMap<>();
        alumnes = new HashMap<>();
        fichero = new FicheroCuinaLab();
        try {
            alumnes = fichero.readAlumnesFile();
        } catch (IOException ex) {
            // No hauria de donar-se: si hi ha error de lectura comencem buit.
            alumnes = new HashMap<>();
        }
    }

    // ─── CURSOS ────────────────────────────────────────────────────────────────

    /**
     * Registra un curs nou a l'escola.
     *
     * @param curs el curs a registrar (ja construït)
     * @throws CuinaLabException si ja existeix un curs amb el mateix codi
     */
    public void registrarCurs(Curs curs) throws CuinaLabException {
        if (cursos.containsKey(curs.getCodi())) {
            throw new CuinaLabException("Ja existeix un curs amb el codi indicat.");
        }
        cursos.put(curs.getCodi(), curs);
    }

    // ─── ALUMNES ───────────────────────────────────────────────────────────────

    /**
     * Registra un alumne nou a l'escola.
     *
     * @param alumne l'alumne a registrar
     * @throws CuinaLabException si ja existeix un alumne amb el mateix DNI
     */
    public void registrarAlumne(Alumne alumne) throws CuinaLabException {
        if (alumnes.containsKey(alumne.getDni())) {
            throw new CuinaLabException("Ja existeix un alumne amb el DNI indicat.");
        }
        alumnes.put(alumne.getDni(), alumne);
    }

    // ─── INSCRIPCIONS ──────────────────────────────────────────────────────────

    /**
     * Inscriu un alumne a un curs.
     *
     * @param dni dni de l'alumne
     * @param codiCurs codi del curs
     * @throws CuinaLabException si no existeix l'alumne, no existeix el curs,
     *         no hi ha places, l'alumne ja està inscrit o és menor i el curs és online
     */
    public void inscriureAlumne(String dni, String codiCurs) throws CuinaLabException {
        String dniUpper = dni.toUpperCase();
        String codiUpper = codiCurs.toUpperCase();
        if (!alumnes.containsKey(dniUpper)) {
            throw new CuinaLabException("No existeix cap alumne amb el DNI indicat.");
        }
        if (!cursos.containsKey(codiUpper)) {
            throw new CuinaLabException("No existeix cap curs amb el codi indicat.");
        }
        Alumne a = alumnes.get(dniUpper);
        Curs c = cursos.get(codiUpper);
        c.inscriureAlumne(a);
    }

    // ─── INFO ──────────────────────────────────────────────────────────────────

    /**
     * Retorna la informació d'un alumne i la llista de cursos en què està inscrit.
     *
     * @param dni dni de l'alumne
     * @return cadena amb la informació formatada
     * @throws CuinaLabException si no existeix l'alumne
     */
    public String infoAlumne(String dni) throws CuinaLabException {
        String dniUpper = dni.toUpperCase();
        if (!alumnes.containsKey(dniUpper)) {
            throw new CuinaLabException("No existeix cap alumne amb el DNI indicat.");
        }
        Alumne a = alumnes.get(dniUpper);
        String info = "";
        info += "NIF de l'alumne: " + a.getDni() + "\n";
        info += "Nom: " + a.getNom() + " " + a.getCognoms() + "\n";
        info += "Edat: " + a.getEdat() + "\n";

        List<Curs> cursosAlumne = new ArrayList<>();
        for (Curs c : cursos.values()) {
            if (c.estaInscrit(dniUpper)) {
                cursosAlumne.add(c);
            }
        }

        if (cursosAlumne.isEmpty()) {
            info += "L'alumne no està inscrit a cap curs.";
        } else {
            info += "Cursos inscrits:\n";
            Collections.sort(cursosAlumne, new Comparator<Curs>() {
                @Override
                public int compare(Curs c1, Curs c2) {
                    return c1.getCodi().compareTo(c2.getCodi());
                }
            });
            for (Curs c : cursosAlumne) {
                info += c.getInfoCursAlumne() + "\n";
            }
        }
        return info;
    }

    /**
     * Retorna la informació de tots els cursos de l'escola, ordenats per codi,
     * amb el nombre de places ocupades i totals i un resum final.
     *
     * @return cadena amb l'estat actual de l'escola
     */
    public String infoEscola() {
        if (cursos.isEmpty()) {
            return "*** ESTAT ESCOLA CUINALAB ***\nNo hi ha cursos registrats.";
        }
        List<Curs> ordenats = new ArrayList<>(cursos.values());
        Collections.sort(ordenats, new Comparator<Curs>() {
            @Override
            public int compare(Curs c1, Curs c2) {
                return c1.getCodi().compareTo(c2.getCodi());
            }
        });

        String info = "*** ESTAT ESCOLA CUINALAB ***\n";
        int totalOcupades = 0;
        int totalLliures = 0;
        for (Curs c : ordenats) {
            info += c.getInfoCursEscola() + "\n";
            totalOcupades += c.getPlacesOcupades();
            totalLliures += c.getPlacesLliures();
        }
        info += "Totals cursos: " + ordenats.size()
              + ". Places totals ocupades: " + totalOcupades
              + ". Places totals lliures: " + totalLliures + ".";
        return info;
    }
}
