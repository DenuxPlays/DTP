package dev.denux.dtp;

import dev.denux.dtp.internal.reader.TomlReader;
import dev.denux.dtp.internal.parser.TomlParser;
import dev.denux.dtp.internal.writer.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

//TODO implement
//  - add javadocs
public class DTP {

    public <T> T fromToml(Reader toml, Class<T> clazzOfT) throws ReflectiveOperationException {
        return new TomlParser<>(clazzOfT).parse(new TomlReader(toml));
    }

    public String toToml(Object source) {
        return new TomlWriter(source).writeToString();
    }

    public void writeTomlToFile(Object source, File file, OpenOption... openOptions) throws IOException {
        writeTomlToFile(source, file.toPath(), openOptions);
    }

    public void writeTomlToFile(Object source, Path path, OpenOption... openOptions) throws IOException {
        Files.write(path, toToml(source).getBytes(StandardCharsets.UTF_8), openOptions);
    }
}
