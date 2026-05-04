package excepcions;

public class AlumneJaInscritException extends Exception {
    public AlumneJaInscritException() {
        super("L'alumne ja està inscrit al curs.");
    }
}
