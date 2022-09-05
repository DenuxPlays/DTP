package dev.denux;

import dev.denux.internal.parser.TomlParser;
import dev.denux.internal.reader.TomlReader;

import java.io.Reader;

//TODO implement
//  - add javadocs
public class DTP {

    public <T> T fromToml(Reader toml, Class<T> clazzOfT) throws ReflectiveOperationException {
        return new TomlParser<>(clazzOfT).parse(new TomlReader(toml));
    }

    public String toToml(Object source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
