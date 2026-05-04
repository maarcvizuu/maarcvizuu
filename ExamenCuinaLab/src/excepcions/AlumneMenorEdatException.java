package excepcions;

public class AlumneMenorEdatException extends Exception {
    public AlumneMenorEdatException() {
        super("L'alumne és menor de 18 anys. No es pot inscriure a cursos online.");
    }
}
