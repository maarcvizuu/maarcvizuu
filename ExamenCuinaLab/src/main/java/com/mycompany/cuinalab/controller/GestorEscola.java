package com.mycompany.cuinalab.controller;

import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.Alumne;
import com.mycompany.cuinalab.model.Curs;
import com.mycompany.cuinalab.model.CursOnline;
import com.mycompany.cuinalab.model.CursPresencial;
import com.mycompany.cuinalab.model.enums.DiaSetmana;
import com.mycompany.cuinalab.model.enums.Plataforma;
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
     * Inicialitza fitxer i llegeix els alumnes del fitxer students.txt.
     *
     * Si el fitxer no existeix o hi ha algun error de lectura, l'aplicació
     * comença buida sense donar error.
     */
    public GestorEscola() {
        fichero = new FicheroCuinaLab();
        cursos = new HashMap<>();
        try {
            // Llegim alumnes del fitxer students.txt
            alumnes = fichero.readAlumnesFile();
        } catch (IOException ex) {
            // No hauria de donar-se. Si hi ha error inicialitzem alumnes buits.
            alumnes = new HashMap<>();
        }
    }

    // ─── CURSOS ────────────────────────────────────────────────────────────────

    /**
     * Registra un curs presencial nou a l'escola.
     *
     * Comprova que no existeixi cap curs amb el mateix codi i, si tot és correcte,
     * crea la instància de CursPresencial i la guarda al registre.
     *
     * @throws CuinaLabException si ja existeix un curs amb el codi o si les dades no són vàlides
     */
    public void registrarCursPresencial(String codi, String nom, int placesMaximes, double preu,
                                        int sessions, String aula, DiaSetmana dia,
                                        boolean inclouMaterial) throws CuinaLabException {
        String codiUpper = codi.toUpperCase();
        if (cursos.containsKey(codiUpper)) {
            throw new CuinaLabException("Ja existeix un curs amb el codi indicat.");
        }
        Curs nou = new CursPresencial(codiUpper, nom, placesMaximes, preu, sessions, aula, dia, inclouMaterial);
        cursos.put(nou.getCodi(), nou);
    }

    /**
     * Registra un curs online nou a l'escola.
     *
     * @throws CuinaLabException si ja existeix un curs amb el codi o si les dades no són vàlides
     */
    public void registrarCursOnline(String codi, String nom, int placesMaximes, double preu,
                                    int sessions, Plataforma plataforma) throws CuinaLabException {
        String codiUpper = codi.toUpperCase();
        if (cursos.containsKey(codiUpper)) {
            throw new CuinaLabException("Ja existeix un curs amb el codi indicat.");
        }
        Curs nou = new CursOnline(codiUpper, nom, placesMaximes, preu, sessions, plataforma);
        cursos.put(nou.getCodi(), nou);
    }

    // ─── ALUMNES ───────────────────────────────────────────────────────────────

    /**
     * Registra un alumne nou a l'escola.
     *
     * @throws CuinaLabException si ja existeix un alumne amb el mateix DNI
     */
    public void registrarAlumne(String dni, String nom, String cognoms, int edat) throws CuinaLabException {
        String dniUpper = dni.toUpperCase();
        if (alumnes.containsKey(dniUpper)) {
            throw new CuinaLabException("Ja existeix un alumne amb el DNI indicat.");
        }
        alumnes.put(dniUpper, new Alumne(dniUpper, nom, cognoms, edat));
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
