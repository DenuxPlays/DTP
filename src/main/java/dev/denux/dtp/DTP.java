package dev.denux.dtp;

import dev.denux.dtp.exception.parse.TomlParseException;
import dev.denux.dtp.internal.reader.TomlReader;
import dev.denux.dtp.internal.parser.TomlParser;
import dev.denux.dtp.internal.writer.TomlWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class DTP {

    /**
     * Serializes the given toml to a new instance of the given class.
     *
     * @param toml     The {@link Reader} that reads the toml file.
     * @param clazzOfT The {@link Class} of the object that we will instantiate.
     * @param <T>      The object that will be returned.
     * @return the object or null if there was an exception while reading or serializing.
     */
    @Nullable
    public <T> T fromToml(@Nonnull Reader toml, @Nonnull Class<T> clazzOfT) {
        TomlReader tomlReader = new TomlReader(toml);
        return fromToml(tomlReader, clazzOfT);
    }

    /**
     * Serializes the given toml to a new instance of the given class.
     *
     * @param tomlString The string that represents a toml file.
     * @param clazzOfT   The {@link Class} of the object that we will instantiate.
     * @param <T>        The object that will be returned.
     * @return the object or null if there was an exception while reading or serializing.
     */
    public <T> T fromToml(@Nonnull String tomlString, @Nonnull Class<T> clazzOfT) {
        TomlReader tomlReader = new TomlReader(tomlString);
        return fromToml(tomlReader, clazzOfT);
    }

    /**
     * Serializes the given toml to a new instance of the given class.
     *
     * @param tomlReader the {@link TomlReader} that reads the toml file and formats it to make it easier for the {@link TomlParser}.
     * @param clazzOfT   The {@link Class} of the object that we will instantiate.
     * @param <T>        The object that will be returned.
     * @return the object or null if there was an exception while reading or serializing.
     */
    public <T> T fromToml(@Nonnull TomlReader tomlReader, @Nonnull Class<T> clazzOfT) {
        try {
            return new TomlParser<>(clazzOfT, tomlReader).parse();
        } catch (ReflectiveOperationException exception) {
            throw new TomlParseException("Could not parse the toml to the object.\n " +
                    "Do .getCause() for more information.", exception);
        }
    }

    /**
     * Deserializes the given {@link Object} to a string that represents a toml file.
     *
     * @param source the {@link Object} you want to deserialize.
     * @return the {@link String} or null if there was an exception while writing.
     */
    public String toToml(@Nonnull Object source) {
        return toToml(new TomlWriter(source));
    }

    /**
     * Deserializes the given {@link Object} to a string that represents a toml file.
     * @param writer the {@link TomlWriter} that writes an object to a {@link String}.
     * @return the {@link String} or null if there was an exception while writing.
     */
    public String toToml(@Nonnull TomlWriter writer) {
        return writer.writeToString();
    }

    /**
     * Deserializes the given {@link Object} and writes it to the given {@link File}.
     * @param source the {@link Object} you want to deserialize.
     * @param file the {@link File} you want to write to.
     * @param openOptions {@link OpenOption}s that describes how an existing file should be handled.
     * @throws IOException is thrown when the file could not be written.
     */
    public void writeTomlToFile(@Nonnull Object source, @Nonnull File file, @Nullable OpenOption... openOptions) throws IOException {
        writeTomlToFile(source, file.toPath(), openOptions);
    }

    /**
     * Deserializes the given {@link Object} and writes it to the given {@link Path}.
     * @param source the {@link Object} you want to deserialize.
     * @param path the {@link Path} you want to write the {@link File} to.
     * @param openOptions {@link OpenOption}s that describes how an existing file should be handled.
     * @throws IOException is thrown when the file could not be written.
     */
    public void writeTomlToFile(@Nonnull Object source, @Nonnull Path path, @Nullable OpenOption... openOptions) throws IOException {
        Files.write(path, toToml(source).getBytes(StandardCharsets.UTF_8), openOptions);
    }
}
