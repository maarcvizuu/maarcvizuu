package com.mycompany.cuinalab.model;

import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.enums.Plataforma;
import com.mycompany.cuinalab.model.enums.TipusCurs;

/**
 * Curs online de l'escola CuinaLab.
 *
 * Hereta de {@link Curs} i afegeix les propietats específiques
 * dels cursos online: si inclou material i la plataforma.
 *
 * @author marcvizuu
 */
public class CursOnline extends Curs {

    private Plataforma plataforma;

    public CursOnline(String codi, String nom, int placesMaximes, double preu, int sessions,
                      Plataforma plataforma) throws CuinaLabException {
        super(codi, nom, placesMaximes, preu, sessions);
        this.plataforma = plataforma;
    }

    public Plataforma getPlataforma() {
        return plataforma;
    }

    @Override
    public TipusCurs getTipus() {
        return TipusCurs.ONLINE;
    }

    /**
     * Inscriu un alumne al curs. Sobreescriu el comportament per validar
     * que l'alumne sigui major d'edat (els menors no poden fer cursos online).
     */
    @Override
    public void inscriureAlumne(Alumne a) throws CuinaLabException {
        if (a.esMenorEdat()) {
            throw new CuinaLabException("L'alumne és menor de 18 anys. No es pot inscriure a cursos online.");
        }
        super.inscriureAlumne(a);
    }

    @Override
    public String getInfoCursAlumne() {
        return codi + " - " + nom + " (Online) - " + preu + "€ - " + sessions + " sessions";
    }
}
