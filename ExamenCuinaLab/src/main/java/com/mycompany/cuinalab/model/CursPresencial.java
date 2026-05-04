package com.mycompany.cuinalab.model;

import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.enums.DiaSetmana;
import com.mycompany.cuinalab.model.enums.TipusCurs;

/**
 * Curs presencial de l'escola CuinaLab.
 *
 * Hereta de {@link Curs} i afegeix les propietats específiques
 * dels cursos presencials: aula i dia de la setmana.
 *
 * @author marcvizuu
 */
public class CursPresencial extends Curs {

    private String aula;
    private DiaSetmana dia;
    private boolean inclouMaterial;

    public CursPresencial(String codi, String nom, int placesMaximes, double preu, int sessions,
                          String aula, DiaSetmana dia, boolean inclouMaterial) throws CuinaLabException {
        super(codi, nom, placesMaximes, preu, sessions);
        this.aula = aula;
        this.dia = dia;
        this.inclouMaterial = inclouMaterial;
    }

    public String getAula() {
        return aula;
    }

    public DiaSetmana getDia() {
        return dia;
    }

    public boolean isInclouMaterial() {
        return inclouMaterial;
    }

    @Override
    public TipusCurs getTipus() {
        return TipusCurs.PRESENCIAL;
    }

    @Override
    public String getInfoCursAlumne() {
        return codi + " - " + nom + " (Presencial) - " + preu + "€ - " + dia + " - " + sessions + " sessions";
    }
}
