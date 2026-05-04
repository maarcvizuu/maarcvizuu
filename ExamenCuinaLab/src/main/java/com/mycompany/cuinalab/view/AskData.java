package com.mycompany.cuinalab.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Classe utilitària per demanar dades a l'usuari per consola amb validació.
 *
 * Tots els mètodes tornen a demanar el valor fins que sigui correcte.
 *
 * @author marcvizuu
 */
public class AskData {

    private BufferedReader br;

    public AskData() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    // ─── INT ───────────────────────────────────────────────────────────────────

    public int askInt(String message) throws IOException {
        int n = 0;
        boolean error;
        do {
            try {
                System.out.print(message);
                n = Integer.parseInt(br.readLine().trim());
                error = false;
            } catch (NumberFormatException ex) {
                error = true;
                System.out.println("Has d'introduir un nombre enter.");
            }
        } while (error);
        return n;
    }

    public int askInt(String message, String errorMessage, int min, int max) throws IOException {
        int n;
        do {
            n = askInt(message);
            if (n < min || n > max) {
                System.out.println(errorMessage);
            }
        } while (n < min || n > max);
        return n;
    }

    // ─── DOUBLE ────────────────────────────────────────────────────────────────

    public double askDouble(String message) throws IOException {
        double n = 0;
        boolean error;
        do {
            try {
                System.out.print(message);
                n = Double.parseDouble(br.readLine().trim().replace(",", "."));
                error = false;
            } catch (NumberFormatException ex) {
                error = true;
                System.out.println("Has d'introduir un nombre (pot ser amb decimals).");
            }
        } while (error);
        return n;
    }

    public double askDoublePositive(String message, String errorMessage) throws IOException {
        double n;
        do {
            n = askDouble(message);
            if (n <= 0) {
                System.out.println(errorMessage);
            }
        } while (n <= 0);
        return n;
    }

    // ─── STRING ────────────────────────────────────────────────────────────────

    public String askString(String message) throws IOException {
        String resp;
        do {
            System.out.print(message);
            resp = br.readLine();
            if (resp == null || resp.isBlank()) {
                System.out.println("No es pot deixar en blanc.");
            }
        } while (resp == null || resp.isBlank());
        return resp;
    }

    /**
     * Demana una cadena que ha de tenir la longitud exacta indicada.
     */
    public String askStringLength(String message, int length, String errorMessage) throws IOException {
        String resp;
        do {
            resp = askString(message);
            if (resp.length() != length) {
                System.out.println(errorMessage);
            }
        } while (resp.length() != length);
        return resp;
    }

    // ─── BOOLEAN ───────────────────────────────────────────────────────────────

    public boolean askBoolean(String message, String errorMessage, String optionTrue, String optionFalse) throws IOException {
        String resp;
        do {
            resp = askString(message);
            if (!resp.equalsIgnoreCase(optionTrue) && !resp.equalsIgnoreCase(optionFalse)) {
                System.out.println(errorMessage);
            }
        } while (!resp.equalsIgnoreCase(optionTrue) && !resp.equalsIgnoreCase(optionFalse));
        return resp.equalsIgnoreCase(optionTrue);
    }
}
