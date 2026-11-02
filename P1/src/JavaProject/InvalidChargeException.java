package JavaProject;

public class InvalidChargeException extends Exception {
    public InvalidChargeException(String message) {
        super(message);
    }

    public InvalidChargeException(String message, Throwable cause) {
        super(message, cause);
    }
}