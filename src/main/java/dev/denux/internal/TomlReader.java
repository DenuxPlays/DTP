package dev.denux.internal;

import dev.denux.exception.TomlReadException;
import dev.denux.internal.entities.Toml;
import dev.denux.internal.entities.TomlDataType;
import dev.denux.utils.ArrayUtil;
import dev.denux.utils.Constant;
import dev.denux.utils.RFC3339Util;
import dev.denux.internal.entities.TomlTable;
import dev.denux.utils.TypesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

//TODO add javadocs
public class TomlReader {

    private final Toml toml;

    public TomlReader(Reader reader) {
        try {
            toml = read(new BufferedReader(reader));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private synchronized Toml read(BufferedReader tomlReader) throws IOException {
        Set<TomlTable> tomlTables = new HashSet<>();
        TomlTable tomlTable = new TomlTable(); //represents the current (master) table

        MultilineReader multilineReader = null;
        for (String line : tomlReader.lines().collect(Collectors.toList())) {
            if (line.trim().startsWith("#")) {
                continue;
            }
            if (line.trim().isEmpty()) {
                continue;
            }
            if (isTable(line)) {
                tomlTables.add(tomlTable);
                tomlTable = new TomlTable(handleTable(line.trim().toCharArray()));
                continue;
            }

            String[] split = parseLine(line);
            String key = split[0];
            String value = split[1];
            //string and multiline string stuff
            char stringIndicator = multilineReader == null ? value.charAt(0) : multilineReader.stringIndicator;
            if (isString(value) && multilineReader == null) {
                tomlTable.put(key, handleString(value.toCharArray(), stringIndicator), TomlDataType.STRING);
                continue;
            }
            if (isMultilineString(value) || multilineReader != null) {
                if (multilineReader == null) {
                    multilineReader = new MultilineReader(stringIndicator, key);
                } else {
                    value = line;
                }
                multilineReader.readOneLine(value.toCharArray());
                if (multilineReader.multilineEnd) {
                    tomlTable.put(multilineReader.getKey(), multilineReader.getValue(), TomlDataType.STRING);
                    multilineReader = null;
                }
                continue;
            }
            addEntryToTomlTable(tomlTable, key, value);
        }
        tomlTables.add(tomlTable);
        String tomlString = tomlReader.lines().collect(Collectors.joining("\n"));
        tomlReader.close();
        return new Toml(tomlString, tomlTables);
    }

    private void addEntryToTomlTable(TomlTable table, String key, String value) {
        Matcher matcher = Constant.RFC3339_REGEX.matcher(value);
        if (matcher.matches()) {
            table.put(key, RFC3339Util.parseDateTime(value).toString(), TomlDataType.DATETIME);
            return;
        }
        matcher = Constant.RFC3339_TIME_REGEX.matcher(value);
        if (matcher.matches()) {
            table.put(key, RFC3339Util.parseTime(value).toString(), TomlDataType.TIME);
            return;
        }
        if (value.startsWith("0x")) {
            table.put(key, Long.parseLong(value.substring(2), 16), TomlDataType.NUMBER);
            return;
        }
        if (value.startsWith("0o")) {
            table.put(key, Long.parseLong(value.substring(2), 16), TomlDataType.NUMBER);
            return;
        }
        if (value.startsWith("0b")) {
            table.put(key, Long.parseLong(value.substring(2), 2), TomlDataType.NUMBER);
            return;
        }
        table.put(key, TypesUtil.convertType(value), TomlDataType.getDataType(value));
    }

    private String handleString(char[] chars, char stringIndicator) {
        List<Character> charList = new ArrayList<>();
        //looping through each char of the string
        //starting at 1 because we don't want to have the string indicator inside the actual string value
        boolean endDefined = false;
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = chars[i-1];
            //checking if it is the last (non escaped) string indicator -> checking if the string is at the end
            if (c == stringIndicator && previousChar != '\\') {
                endDefined = true;
                break;
            }
            charList.add(c);
        }
        if (!endDefined) {
            throw new TomlReadException("The end of the string is not defined: " + new String(ArrayUtil.listToCharArray(charList)));
        }
        return new String(ArrayUtil.listToCharArray(charList));
    }

    private String handleTable(char[] chars) {
        List<Character> charList = new ArrayList<>();
        //starting at one because we don't want to have the "[" inside the name
        boolean endDefined = false;
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            if (i == 1 && Constant.STRING_INDICATORS.contains(c)) {
                for (char ch : chars) {
                    charList.add(ch);
                }
                charList.remove(0);
                System.out.println("String key | table");
                return handleString(ArrayUtil.listToCharArray(charList), c);
            }
            if (c == ']') {
                endDefined = true;
                break;
            }
            charList.add(c);
        }
        if (!endDefined) {
            throw new TomlReadException("Toml table is not properly defined: " + new String(ArrayUtil.listToCharArray(charList)));
        }
        return new String(ArrayUtil.listToCharArray(charList));
    }

    private String[] parseLine(String line) {
        String[] split = line.split("=");
        String key = split[0].trim();
        key = key.replace("\"", "");
        key = key.replace("'", "");
        String value = Arrays.stream(split).skip(1).collect(Collectors.joining()).trim();
        return new String[]{key, value};
    }

    private boolean isString(String value) {
        if (value.length() == 0) return false;
        return Constant.STRING_INDICATORS.contains(value.charAt(0)) && !isMultilineString(value);
    }

    private boolean isMultilineString(String value) {
        char[] chars = value.toCharArray();
        if (chars.length < 3) return false;
        if (chars[0] != chars[1] && chars[1] != chars[2]) {
            return false;
        }
        return Constant.STRING_INDICATORS.contains(chars[0]);
    }

    private boolean isTable(String line) {
        char[] chars = line.toCharArray();
        if (chars[0] != '[') {
            return false;
        }
        for (char c : chars) {
            if (c == ']') {
                return true;
            }
        }
        return false;
    }

    private static class MultilineReader {
        private boolean multilineEnd = false;
        private int globalIndex = 0;
        private final StringBuilder value = new StringBuilder();
        private final String key;
        private final char stringIndicator;

        private MultilineReader(char stringIndicator, String key) {
            this.stringIndicator = stringIndicator;
            this.key = key;
        }

        private void readOneLine(char[] chars) {
            List<Character> charList = new ArrayList<>();
            int i;
            for (i = 0; i < chars.length; i++) {
                char c = chars[i];
                char previousChar = i == 0 ? ' ' : chars[i - 1];
                //checking if it could be the end of the multiline string
                if (c == stringIndicator && previousChar != '\\' && globalIndex > 3) {
                    try {
                        char c2 = chars[i + 1];
                        char c3 = chars[i + 2];
                        System.out.println(c2 + " " + c3);
                        //actually checking if it is the end or not
                        if (c2 == stringIndicator && c3 == stringIndicator) {
                            multilineEnd = true;
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
                if (globalIndex > 2) {
                    charList.add(c);
                }
                globalIndex++;
            }
            value.append(new String(ArrayUtil.listToCharArray(charList)));
            if (!multilineEnd) {
                value.append("\n");
            }
        }

        private String getValue() {
            return value.toString();
        }

        private String getKey() {
            return key;
        }
    }

    public Set<TomlTable> getTomlMaps() {
        return toml.getTomlTables();
    }

    public Toml getToml() {
        return toml;
    }
}