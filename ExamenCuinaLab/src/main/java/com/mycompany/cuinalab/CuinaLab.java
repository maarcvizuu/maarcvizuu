package com.mycompany.cuinalab;

import com.mycompany.cuinalab.view.Menu;
import java.io.IOException;

/**
 * Aplicació de gestió de l'escola de cuina CuinaLab.
 *
 * Implementa el sistema de gestió de cursos i alumnes amb POO
 * (herència, excepcions pròpies, fitxers i separació de capes).
 *
 * @author marcvizuu
 */
public class CuinaLab {

    // NUNCA THROW NINGUNA EXCEPTION EN EL MAIN
    // NUNCA CATCH EXCEPTION GENÉRICO
    public static void main(String[] args) {
        Menu m = new Menu();
        try {
            m.start();
        } catch (IOException ex) {
            System.out.println("ERROR INESPERAT: " + ex.getMessage());
        }
    }
}
