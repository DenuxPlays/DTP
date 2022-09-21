package dev.denux.dtp.exception.parse;

import dev.denux.dtp.exception.TomlException;

public class TomlParseException extends TomlException {

    public TomlParseException(String s) {
        super(s);
    }

    public TomlParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
