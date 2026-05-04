package com.mycompany.cuinalab.exception;

/**
 * Excepció pròpia de l'aplicació CuinaLab.
 *
 * S'utilitza per a tots els errors de lògica del domini:
 * curs ja existent, alumne no trobat, places ocupades, etc.
 *
 * @author marcvizuu
 */
public class CuinaLabException extends Exception {

    public CuinaLabException(String message) {
        super(message);
    }
}
