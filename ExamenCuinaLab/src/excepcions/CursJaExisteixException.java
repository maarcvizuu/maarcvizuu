package excepcions;

public class CursJaExisteixException extends Exception {
    public CursJaExisteixException(String codi) {
        super("Ja existeix un curs amb el codi " + codi + ".");
    }
}
