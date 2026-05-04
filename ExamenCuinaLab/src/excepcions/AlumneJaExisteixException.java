package excepcions;

public class AlumneJaExisteixException extends Exception {
    public AlumneJaExisteixException(String nif) {
        super("Ja existeix un alumne amb el NIF " + nif + ".");
    }
}
