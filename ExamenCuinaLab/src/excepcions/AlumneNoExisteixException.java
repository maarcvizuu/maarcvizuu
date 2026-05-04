package excepcions;

public class AlumneNoExisteixException extends Exception {
    public AlumneNoExisteixException(String nif) {
        super("No existeix cap alumne amb el NIF " + nif + ".");
    }
}
