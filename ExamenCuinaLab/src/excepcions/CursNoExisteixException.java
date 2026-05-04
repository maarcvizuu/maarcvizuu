package excepcions;

public class CursNoExisteixException extends Exception {
    public CursNoExisteixException(String codi) {
        super("No existeix cap curs amb el codi " + codi + ".");
    }
}
