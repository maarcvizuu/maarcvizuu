import excepcions.*;
import gestio.Escola;
import model.Alumne;
import model.Curs;
import model.CursOnline;
import model.CursPresencial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static Escola escola = new Escola();

    public static void main(String[] args) {
        carregarAlumnes();

        int opcio = 0;
        while (opcio != 6) {
            mostrarMenu();
            opcio = llegirEnter("Indica què vols fer: ");
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
                    System.out.println("Tancant CuinaLab... Bon profit! Fins aviat!");
                    break;
                default:
                    System.out.println("Opció incorrecta");
                    break;
            }
        }
    }

    // ─── MENÚ ──────────────────────────────────────────────────────────────────

    private static void mostrarMenu() {
        System.out.println("\n*** CuinaLab - Escola de Cuina ***");
        System.out.println("1. Registrar curs");
        System.out.println("2. Registrar alumne");
        System.out.println("3. Inscriure alumne a curs");
        System.out.println("4. Info alumne");
        System.out.println("5. Veure escola");
        System.out.println("6. Sortir");
    }

    // ─── OPCIONS ───────────────────────────────────────────────────────────────

    private static void registrarCurs() {
        System.out.println("*** Registrar Curs ***");

        // Validar codi: exactament 5 caràcters, format 3 lletres + 2 dígits
        String codi = "";
        boolean codiValid = false;
        while (!codiValid) {
            System.out.print("Codi del curs: ");
            codi = sc.nextLine().toUpperCase();
            if (codi.length() != 5) {
                System.out.println("El codi del curs ha de tenir 5 caràcters.");
            } else if (!formatCodiValid(codi)) {
                System.out.println("El format del codi no és correcte (3 lletres + 2 números).");
            } else {
                codiValid = true;
            }
        }

        System.out.print("Nom: ");
        String nom = sc.nextLine();

        // Validar capacitat entre 5 i 20
        int capacitat = 0;
        boolean capacitatValida = false;
        while (!capacitatValida) {
            capacitat = llegirEnter("Capacitat: ");
            if (capacitat < 5 || capacitat > 20) {
                System.out.println("La capacitat ha de ser entre 5 i 20.");
            } else {
                capacitatValida = true;
            }
        }

        // Validar preu entre 5 i 20
        double preu = 0;
        boolean preuValid = false;
        while (!preuValid) {
            preu = llegirDouble("Preu: ");
            if (preu < 5 || preu > 20) {
                System.out.println("El preu ha de ser entre 5 i 20.");
            } else {
                preuValid = true;
            }
        }

        // Tipus: presencial (s) o online (n)
        String tipusResp = "";
        boolean tipusValid = false;
        while (!tipusValid) {
            System.out.print("Presencial o online?(s/n): ");
            tipusResp = sc.nextLine().toLowerCase();
            if (tipusResp.equals("s") || tipusResp.equals("n")) {
                tipusValid = true;
            } else {
                System.out.println("Opció incorrecta. Introdueix 's' per Presencial o 'n' per Online.");
            }
        }

        Curs nouCurs = null;

        if (tipusResp.equals("s")) {
            // PRESENCIAL
            System.out.print("Aula: ");
            String aula = sc.nextLine();

            String diaSemana = "";
            boolean diaValid = false;
            while (!diaValid) {
                System.out.print("Dia de la setmana (DILLUNS/DIMARTS/DIMECRES/DIJOUS/DIVENDRES): ");
                diaSemana = sc.nextLine().toUpperCase();
                if (diaSemana.equals("DILLUNS") || diaSemana.equals("DIMARTS")
                        || diaSemana.equals("DIMECRES") || diaSemana.equals("DIJOUS")
                        || diaSemana.equals("DIVENDRES")) {
                    diaValid = true;
                } else {
                    System.out.println("Dia no vàlid.");
                }
            }

            int sessions = 0;
            boolean sessionsValides = false;
            while (!sessionsValides) {
                sessions = llegirEnter("Nombre de sessions (1-12): ");
                if (sessions < 1 || sessions > 12) {
                    System.out.println("Les sessions han de ser entre 1 i 12.");
                } else {
                    sessionsValides = true;
                }
            }

            nouCurs = new CursPresencial(codi, nom, capacitat, preu, sessions, aula, diaSemana);

        } else {
            // ONLINE
            boolean material = false;
            boolean materialValid = false;
            while (!materialValid) {
                System.out.print("Inclou material? (s/n): ");
                String resp = sc.nextLine().toLowerCase();
                if (resp.equals("s")) {
                    material = true;
                    materialValid = true;
                } else if (resp.equals("n")) {
                    material = false;
                    materialValid = true;
                } else {
                    System.out.println("Opció incorrecta. Introdueix 's' o 'n'.");
                }
            }

            String plataforma = "";
            boolean plataformaValida = false;
            while (!plataformaValida) {
                System.out.print("Plataforma (ZOOM/MEET/TEAMS): ");
                plataforma = sc.nextLine().toUpperCase();
                if (plataforma.equals("ZOOM") || plataforma.equals("MEET") || plataforma.equals("TEAMS")) {
                    plataformaValida = true;
                } else {
                    System.out.println("Plataforma no vàlida.");
                }
            }

            int sessions = 0;
            boolean sessionsValides = false;
            while (!sessionsValides) {
                sessions = llegirEnter("Nombre de sessions (1-12): ");
                if (sessions < 1 || sessions > 12) {
                    System.out.println("Les sessions han de ser entre 1 i 12.");
                } else {
                    sessionsValides = true;
                }
            }

            nouCurs = new CursOnline(codi, nom, capacitat, preu, sessions, material, plataforma);
        }

        try {
            escola.registrarCurs(nouCurs);
            System.out.println("Curs registrat.");
        } catch (CursJaExisteixException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void registrarAlumne() {
        System.out.println("*** Registrar Alumne ***");

        // Validar NIF: exactament 9 caràcters
        String nif = "";
        boolean nifValid = false;
        while (!nifValid) {
            System.out.print("NIF de l'alumne: ");
            nif = sc.nextLine().toUpperCase();
            if (nif.length() != 9) {
                System.out.println("El NIF ha de tenir 9 caràcters.");
            } else {
                nifValid = true;
            }
        }

        System.out.print("Nom: ");
        String nom = sc.nextLine();

        System.out.print("Cognoms: ");
        String cognoms = sc.nextLine();

        // Validar edat entre 16 i 99
        int edat = 0;
        boolean edatValida = false;
        while (!edatValida) {
            edat = llegirEnter("Edat: ");
            if (edat < 16 || edat > 99) {
                System.out.println("L'edat ha d'estar entre 16 i 99.");
            } else {
                edatValida = true;
            }
        }

        try {
            escola.registrarAlumne(new Alumne(nif, nom, cognoms, edat));
            System.out.println("Alumne registrat.");
        } catch (AlumneJaExisteixException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inscriureAlumne() {
        System.out.println("*** Inscriure alumne a curs ***");
        System.out.print("NIF de l'alumne: ");
        String nif = sc.nextLine().toUpperCase();
        System.out.print("Codi del curs: ");
        String codi = sc.nextLine().toUpperCase();

        try {
            escola.inscriureAlumne(nif, codi);
            System.out.println("Inscripció realitzada!");
        } catch (AlumneNoExisteixException | CursNoExisteixException
                | AlumneJaInscritException | PlacesOcupadesException
                | AlumneMenorEdatException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void infoAlumne() {
        System.out.println("*** Info Alumne ***");
        System.out.print("NIF de l'alumne: ");
        String nif = sc.nextLine().toUpperCase();

        try {
            System.out.println(escola.getInfoAlumne(nif));
        } catch (AlumneNoExisteixException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void veureEscola() {
        System.out.println(escola.getInfoEscola());
    }

    // ─── FITXERS ───────────────────────────────────────────────────────────────

    private static void carregarAlumnes() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("students.txt"));
            String linia = br.readLine();
            while (linia != null) {
                String[] parts = linia.split(",");
                if (parts.length == 4) {
                    String nif = parts[0].trim();
                    String nom = parts[1].trim();
                    String cognoms = parts[2].trim();
                    int edat = Integer.parseInt(parts[3].trim());
                    Alumne alumne = new Alumne(nif, nom, cognoms, edat);
                    try {
                        escola.registrarAlumne(alumne);
                    } catch (AlumneJaExisteixException e) {
                        // alumne duplicat al fitxer, s'ignora
                    }
                }
                linia = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            // fitxer no existeix o error de lectura, continuem sense dades
        }
    }

    // ─── HELPERS D'ENTRADA ─────────────────────────────────────────────────────

    private static int llegirEnter(String prompt) {
        int valor = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            String entrada = sc.nextLine();
            try {
                valor = Integer.parseInt(entrada.trim());
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número enter.");
            }
        }
        return valor;
    }

    private static double llegirDouble(String prompt) {
        double valor = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            String entrada = sc.nextLine();
            try {
                valor = Double.parseDouble(entrada.trim().replace(",", "."));
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número decimal.");
            }
        }
        return valor;
    }

    // Comprova que el codi tingui format 3 lletres + 2 dígits
    private static boolean formatCodiValid(String codi) {
        boolean valid = true;
        int i = 0;
        while (i < 3 && valid) {
            if (!Character.isLetter(codi.charAt(i))) {
                valid = false;
            }
            i++;
        }
        while (i < 5 && valid) {
            if (!Character.isDigit(codi.charAt(i))) {
                valid = false;
            }
            i++;
        }
        return valid;
    }
}
