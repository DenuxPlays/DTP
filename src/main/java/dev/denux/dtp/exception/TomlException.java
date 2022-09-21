package dev.denux.dtp.exception;

public class TomlException extends RuntimeException {

    public TomlException(String message) {
        super(message);
    }

    public TomlException(String message, Throwable cause) {
        super(message, cause);
    }
}
