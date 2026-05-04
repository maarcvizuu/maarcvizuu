package gestio;

import model.Alumne;
import model.Curs;
import model.CursOnline;
import excepcions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Escola {

    private ArrayList<Curs> cursos;
    private ArrayList<Alumne> alumnes;

    public Escola() {
        this.cursos = new ArrayList<>();
        this.alumnes = new ArrayList<>();
    }

    // ─── CURSOS ────────────────────────────────────────────────────────────────

    public void registrarCurs(Curs curs) throws CursJaExisteixException {
        boolean existeix = false;
        int i = 0;
        while (i < cursos.size() && !existeix) {
            if (cursos.get(i).getCodi().equalsIgnoreCase(curs.getCodi())) {
                existeix = true;
            }
            i++;
        }
        if (existeix) {
            throw new CursJaExisteixException(curs.getCodi());
        }
        cursos.add(curs);
    }

    public Curs buscarCurs(String codi) throws CursNoExisteixException {
        Curs trobat = null;
        int i = 0;
        while (i < cursos.size() && trobat == null) {
            if (cursos.get(i).getCodi().equalsIgnoreCase(codi)) {
                trobat = cursos.get(i);
            }
            i++;
        }
        if (trobat == null) {
            throw new CursNoExisteixException(codi.toUpperCase());
        }
        return trobat;
    }

    // ─── ALUMNES ───────────────────────────────────────────────────────────────

    public void registrarAlumne(Alumne alumne) throws AlumneJaExisteixException {
        boolean existeix = false;
        int i = 0;
        while (i < alumnes.size() && !existeix) {
            if (alumnes.get(i).getNif().equalsIgnoreCase(alumne.getNif())) {
                existeix = true;
            }
            i++;
        }
        if (existeix) {
            throw new AlumneJaExisteixException(alumne.getNif());
        }
        alumnes.add(alumne);
    }

    public Alumne buscarAlumne(String nif) throws AlumneNoExisteixException {
        Alumne trobat = null;
        int i = 0;
        while (i < alumnes.size() && trobat == null) {
            if (alumnes.get(i).getNif().equalsIgnoreCase(nif)) {
                trobat = alumnes.get(i);
            }
            i++;
        }
        if (trobat == null) {
            throw new AlumneNoExisteixException(nif.toUpperCase());
        }
        return trobat;
    }

    // ─── INSCRIPCIÓ ────────────────────────────────────────────────────────────

    public void inscriureAlumne(String nif, String codiCurs)
            throws AlumneNoExisteixException, CursNoExisteixException,
                   AlumneJaInscritException, PlacesOcupadesException,
                   AlumneMenorEdatException {

        Alumne alumne = buscarAlumne(nif);
        Curs curs = buscarCurs(codiCurs);

        if (curs.estaInscrit(nif)) {
            throw new AlumneJaInscritException();
        }
        if (!curs.tePlace()) {
            throw new PlacesOcupadesException();
        }
        if (alumne.esMenorEdat() && curs instanceof CursOnline) {
            throw new AlumneMenorEdatException();
        }
        curs.inscriureAlumne(nif);
    }

    // ─── INFO ALUMNE ───────────────────────────────────────────────────────────

    public String getInfoAlumne(String nif) throws AlumneNoExisteixException {
        Alumne alumne = buscarAlumne(nif);
        StringBuilder sb = new StringBuilder();
        sb.append("NIF: ").append(alumne.getNif()).append("\n");
        sb.append("Nom: ").append(alumne.getNomComplet()).append("\n");
        sb.append("Edat: ").append(alumne.getEdat()).append("\n");
        sb.append("--- Informació de l'alumne ---\n");

        ArrayList<Curs> cursosAlumne = getCursosAlumne(nif);
        if (cursosAlumne.isEmpty()) {
            sb.append("L'alumne no està inscrit a cap curs.");
        } else {
            int i = 0;
            while (i < cursosAlumne.size()) {
                sb.append(cursosAlumne.get(i).getInfoAlumne());
                if (i < cursosAlumne.size() - 1) {
                    sb.append("\n");
                }
                i++;
            }
        }
        return sb.toString();
    }

    private ArrayList<Curs> getCursosAlumne(String nif) {
        ArrayList<Curs> resultat = new ArrayList<>();
        int i = 0;
        while (i < cursos.size()) {
            if (cursos.get(i).estaInscrit(nif)) {
                resultat.add(cursos.get(i));
            }
            i++;
        }
        return resultat;
    }

    // ─── VEURE ESCOLA ─────────────────────────────────────────────────────────

    public String getInfoEscola() {
        ArrayList<Curs> ordenats = new ArrayList<>(cursos);
        Collections.sort(ordenats, new Comparator<Curs>() {
            public int compare(Curs c1, Curs c2) {
                return c1.getCodi().compareTo(c2.getCodi());
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("*** ESTAT ESCOLA CUINALAB ***\n");

        int totalOcupades = 0;
        int totalLliures = 0;

        int i = 0;
        while (i < ordenats.size()) {
            Curs c = ordenats.get(i);
            sb.append(c.getInfoEscola()).append("\n");
            totalOcupades += c.getPlacesOcupades();
            totalLliures += c.getPlacesLliures();
            i++;
        }

        sb.append("---\n");
        sb.append("Total cursos: ").append(ordenats.size())
          .append(". Places totals ocupades: ").append(totalOcupades)
          .append(". Places totals lliures: ").append(totalLliures).append(".");

        return sb.toString();
    }
}
