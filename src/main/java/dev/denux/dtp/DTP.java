package dev.denux.dtp;

import dev.denux.dtp.internal.reader.TomlReader;
import dev.denux.dtp.internal.parser.TomlParser;

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
