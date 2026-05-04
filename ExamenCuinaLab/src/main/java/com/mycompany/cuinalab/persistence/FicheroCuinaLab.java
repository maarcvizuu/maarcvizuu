package com.mycompany.cuinalab.persistence;

import com.mycompany.cuinalab.model.Alumne;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la persistència en fitxers de l'aplicació CuinaLab.
 *
 * Segons l'enunciat només cal LLEGIR el fitxer "students.txt" del directori
 * actual quan arrenca l'aplicació. Si el fitxer no existeix l'aplicació
 * comença buida sense donar error.
 *
 * Format del fitxer students.txt (una línia per alumne):
 *   DNI,Nom,Cognoms,Edat
 *
 * @author marcvizuu
 */
public class FicheroCuinaLab {

    /**
     * Fitxer dels alumnes.
     */
    private File ficheroAlumnes;

    /**
     * Inicialitza la ruta del fitxer students.txt al directori actual.
     */
    public FicheroCuinaLab() {
        ficheroAlumnes = new File("students.txt");
    }

    /**
     * Llegeix els alumnes del fitxer students.txt.
     *
     * Si el fitxer no existeix retorna un mapa buit.
     * Les línies amb format incorrecte s'ignoren silenciosament per
     * evitar que un error en el fitxer aturi l'aplicació.
     *
     * @return mapa amb els alumnes carregats (clau: DNI en majúscules)
     * @throws IOException si hi ha algun error de lectura del fitxer
     */
    public Map<String, Alumne> readAlumnesFile() throws IOException {
        Map<String, Alumne> resultat = new HashMap<>();
        if (!ficheroAlumnes.exists()) {
            return resultat;
        }
        BufferedReader br = new BufferedReader(new FileReader(ficheroAlumnes));
        String linia;
        while ((linia = br.readLine()) != null) {
            String[] parts = linia.split(",");
            if (parts.length == 4) {
                try {
                    String dni = parts[0].trim().toUpperCase();
                    String nom = parts[1].trim();
                    String cognoms = parts[2].trim();
                    int edat = Integer.parseInt(parts[3].trim());
                    if (!resultat.containsKey(dni)) {
                        resultat.put(dni, new Alumne(dni, nom, cognoms, edat));
                    }
                } catch (NumberFormatException ex) {
                    // línia ignorada si l'edat no és vàlida
                }
            }
        }
        br.close();
        return resultat;
    }
}
