package JavaProject;

public class InvalidExchangeRateException extends RuntimeException {
    public InvalidExchangeRateException(String message) {
        super(message);
    }

    public InvalidExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}