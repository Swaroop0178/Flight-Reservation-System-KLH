package JavaProject;

public class InvalidFareException extends Exception {
    private double rejectedValue;

    public InvalidFareException(String message) {
        super(message);
    }

    public InvalidFareException(String message, double rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }

    public double getRejectedValue() {
        return rejectedValue;
    }
}