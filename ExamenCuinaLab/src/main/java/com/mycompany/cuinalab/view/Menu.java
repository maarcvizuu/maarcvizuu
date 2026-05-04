package com.mycompany.cuinalab.view;

import com.mycompany.cuinalab.controller.GestorEscola;
import com.mycompany.cuinalab.exception.CuinaLabException;
import com.mycompany.cuinalab.model.enums.DiaSetmana;
import com.mycompany.cuinalab.model.enums.Plataforma;
import com.mycompany.cuinalab.model.enums.TipusCurs;
import java.io.IOException;

/**
 * Vista i menú principal de l'aplicació CuinaLab.
 *
 * Mostra el menú, demana l'opció a l'usuari i invoca el gestor.
 * Captura les CuinaLabException dins del bucle perquè l'aplicació
 * no s'aturi si l'usuari fa una operació invàlida.
 *
 * @author marcvizuu
 */
public class Menu {

    private AskData ask;
    private GestorEscola gestor;

    /**
     * Punt d'arrencada de la vista.
     *
     * @throws IOException si hi ha algun problema demanant dades (no hauria de donar-se)
     */
    public void start() throws IOException {
        gestor = new GestorEscola();
        ask = new AskData();
        boolean sortir = false;
        do {
            try {
                mostrarMenu();
                int opcio = ask.askInt("Indica què vols fer: ");
                switch (opcio) {
                    case 1:
                        registrarCurs();
                        break;
                    case 2:
                        registrarAlumne();
                        break;
                    case 3:
                        inscriureAlumne();
                        break;
                    case 4:
                        infoAlumne();
                        break;
                    case 5:
                        veureEscola();
                        break;
                    case 6:
                        sortir = true;
                        break;
                    default:
                        System.out.println("Opció incorrecta.");
                }
            } catch (CuinaLabException ex) {
                System.out.println(ex.getMessage());
            }
        } while (!sortir);
        System.out.println("Tancant CuinaLab... Bon profit! Fins aviat!");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("*** CuinaLab - Escola de Cuina ***");
        System.out.println("1. Registrar curs");
        System.out.println("2. Registrar alumne");
        System.out.println("3. Inscriure alumne a curs");
        System.out.println("4. Info alumne");
        System.out.println("5. Veure escola");
        System.out.println("6. Sortir");
    }

    // ─── OPCIÓ 1: REGISTRAR CURS ───────────────────────────────────────────────

    private void registrarCurs() throws IOException, CuinaLabException {
        System.out.println("*** Registrar Curs ***");
        String codi = demanarCodiCurs();
        String nom = ask.askString("Nom: ");
        int places = ask.askInt("Capacitat: ", "Les places han d'estar entre 5 i 20.", 5, 20);
        double preu = ask.askDouble("Preu: ", "El preu ha de ser un nombre positiu.", 0.01);
        TipusCurs tipus = demanarTipusCurs();
        int sessions = ask.askInt("Sessions: ", "Les sessions han d'estar entre 1 i 12.", 1, 12);

        if (tipus == TipusCurs.PRESENCIAL) {
            String aula = ask.askString("Aula: ");
            boolean inclouMaterial = ask.askBoolean(
                    "Inclou material? (s/n): ",
                    "Has d'introduir 's' o 'n'.",
                    "s", "n");
            DiaSetmana dia = demanarDiaSetmana();
            gestor.registrarCursPresencial(codi, nom, places, preu, sessions, aula, dia, inclouMaterial);
        } else {
            Plataforma plataforma = demanarPlataforma();
            gestor.registrarCursOnline(codi, nom, places, preu, sessions, plataforma);
        }
        System.out.println("Curs registrat.");
    }

    /**
     * Demana un codi de curs vàlid: exactament 5 caràcters, 3 lletres + 2 dígits.
     */
    private String demanarCodiCurs() throws IOException {
        String codi;
        boolean valid;
        do {
            codi = ask.askString("Codi del curs: ").toUpperCase();
            valid = formatCodiValid(codi);
            if (!valid) {
                System.out.println("El codi ha de tenir 5 caràcters: 3 lletres + 2 dígits.");
            }
        } while (!valid);
        return codi;
    }

    private boolean formatCodiValid(String codi) {
        if (codi.length() != 5) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!Character.isLetter(codi.charAt(i))) {
                return false;
            }
        }
        for (int i = 3; i < 5; i++) {
            if (!Character.isDigit(codi.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private TipusCurs demanarTipusCurs() throws IOException {
        TipusCurs t = null;
        boolean error;
        do {
            String resp = ask.askString("Tipus (PRESENCIAL/ONLINE): ").toUpperCase();
            try {
                t = TipusCurs.valueOf(resp);
                error = false;
            } catch (IllegalArgumentException ex) {
                System.out.println("Tipus incorrecte. Ha de ser PRESENCIAL o ONLINE.");
                error = true;
            }
        } while (error);
        return t;
    }

    private DiaSetmana demanarDiaSetmana() throws IOException {
        DiaSetmana d = null;
        boolean error;
        do {
            String resp = ask.askString("Dia de la setmana (DILLUNS/DIMARTS/DIMECRES/DIJOUS/DIVENDRES): ").toUpperCase();
            try {
                d = DiaSetmana.valueOf(resp);
                error = false;
            } catch (IllegalArgumentException ex) {
                System.out.println("Dia incorrecte.");
                error = true;
            }
        } while (error);
        return d;
    }

    private Plataforma demanarPlataforma() throws IOException {
        Plataforma p = null;
        boolean error;
        do {
            String resp = ask.askString("Plataforma (ZOOM/MEET/TEAMS): ").toUpperCase();
            try {
                p = Plataforma.valueOf(resp);
                error = false;
            } catch (IllegalArgumentException ex) {
                System.out.println("Plataforma incorrecta.");
                error = true;
            }
        } while (error);
        return p;
    }

    // ─── OPCIÓ 2: REGISTRAR ALUMNE ─────────────────────────────────────────────

    private void registrarAlumne() throws IOException, CuinaLabException {
        System.out.println("*** Registrar Alumne ***");
        String dni = ask.askStringLength(
                "DNI de l'alumne: ", 9,
                "El DNI ha de tenir exactament 9 caràcters.").toUpperCase();
        String nom = ask.askString("Nom: ");
        String cognoms = ask.askString("Cognoms: ");
        int edat = ask.askInt("Edat: ", "L'edat ha d'estar entre 16 i 99.", 16, 99);
        gestor.registrarAlumne(dni, nom, cognoms, edat);
        System.out.println("Alumne registrat.");
    }

    // ─── OPCIÓ 3: INSCRIURE ALUMNE A CURS ──────────────────────────────────────

    private void inscriureAlumne() throws IOException, CuinaLabException {
        System.out.println("*** Inscriure alumne a curs ***");
        String dni = ask.askString("DNI de l'alumne: ").toUpperCase();
        String codi = ask.askString("Codi del curs: ").toUpperCase();
        gestor.inscriureAlumne(dni, codi);
        System.out.println("Inscripció realitzada!");
    }

    // ─── OPCIÓ 4: INFO ALUMNE ──────────────────────────────────────────────────

    private void infoAlumne() throws IOException, CuinaLabException {
        System.out.println("*** Info Alumne ***");
        String dni = ask.askString("DNI de l'alumne: ").toUpperCase();
        System.out.println(gestor.infoAlumne(dni));
    }

    // ─── OPCIÓ 5: VEURE ESCOLA ─────────────────────────────────────────────────

    private void veureEscola() {
        System.out.println(gestor.infoEscola());
    }
}
