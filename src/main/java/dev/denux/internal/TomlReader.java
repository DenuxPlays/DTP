package dev.denux.internal;

import dev.denux.internal.entities.Toml;
import dev.denux.internal.entities.TomlDataType;
import dev.denux.utils.Constant;
import dev.denux.utils.TomlTable;
import dev.denux.utils.TypesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

//TODO add javadocs
public class TomlReader {

    private final Toml toml;

    public TomlReader(Reader reader) {
        try {
            toml = read(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private synchronized Toml read(Reader tomlReader) throws IOException {
        BufferedReader reader = new BufferedReader(tomlReader);
        Set<TomlTable> tomlMaps = new HashSet<>();
        TomlTable tomlMap = new TomlTable(); //represents the current (master) table
        for (String line : reader.lines().collect(Collectors.toList())) {
            if (line.startsWith("#")) {
                //Cuz comment
                continue;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                tomlMaps.add(tomlMap);
                tomlMap = new TomlTable(line.trim().substring(1, line.length() - 1));
                continue;
            }
            addEntryToTomlMap(tomlMap, line);
        }
        tomlMaps.add(tomlMap);
        String tomlString = reader.lines().collect(Collectors.joining("\n"));
        tomlReader.close();
        reader.close();
        return new Toml(tomlString, tomlMaps);
    }

    private void addEntryToTomlMap(TomlTable map, String line) {
        String[] split = line.split("=");
        String key = split[0].trim();
        key = key.replace("\"", "");
        key = key.replace("'", "");
        String value = split[1].trim();
        Matcher matcher = Constant.STRING_REGEX.matcher(value);
        if (matcher.find()) {
            map.put(key, matcher.group(0), TomlDataType.STRING);
            return;
        }
        map.put(key, TypesUtil.convertType(value), TomlDataType.getDataType(value));
    }

    public Set<TomlTable> getTomlMaps() {
        return toml.getTomlMaps();
    }

    public Toml getToml() {
        return toml;
    }
}