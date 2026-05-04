package excepcions;

public class PlacesOcupadesException extends Exception {
    public PlacesOcupadesException() {
        super("El curs no té places lliures.");
    }
}
