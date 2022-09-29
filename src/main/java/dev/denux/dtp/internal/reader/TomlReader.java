package dev.denux.dtp.internal.reader;

import dev.denux.dtp.internal.entities.Toml;
import dev.denux.dtp.internal.entities.TomlDataType;
import dev.denux.dtp.internal.entities.TomlTable;
import dev.denux.dtp.utils.RFC3339Util;
import dev.denux.dtp.exception.TomlReadException;
import dev.denux.dtp.utils.ArrayUtil;
import dev.denux.dtp.utils.Constant;
import dev.denux.dtp.utils.TypesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

//TODO add javadocs
public class TomlReader {

    private final Toml toml;
    private final Map<Integer, ArrayReader> arrayReaderMap = new HashMap<>();

    public TomlReader(Reader reader) {
        try {
            toml = read(stringToList(readToString(reader)));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public TomlReader(String tomlString) {
        try {
            this.toml = read(stringToList(tomlString));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private synchronized String readToString(Reader reader) throws IOException {
        BufferedReader tomlReader = new BufferedReader(reader);
        String lines = tomlReader.lines().collect(Collectors.joining("\n"));
        reader.close();
        tomlReader.close();
        return lines;
    }

    private List<String> stringToList(String lines) {
        return Arrays.asList(lines.split("\n"));
    }

    private Toml read(List<String> lines) throws IOException {
        Set<TomlTable> tomlTables = new HashSet<>();
        TomlTable tomlTable = new TomlTable(); //represents the current (master) table

        MultilineReader multilineReader = null;
        ArrayReader arrayReader = null;
        String multilineArrayKey = "";
        for (String line : lines) {
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
            char stringIndicator;
            if (arrayReader != null) {
                stringIndicator = ' ';
            } else if (multilineReader == null) {
                stringIndicator = value.charAt(0);
            } else {
                stringIndicator = multilineReader.stringIndicator;
            }
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
            if (isArray(value)) {
                ArrayReader reader = new ArrayReader();
                reader.readArray(value);
                int size = arrayReaderMap.size();
                arrayReaderMap.put(size, reader);
                tomlTable.put(key, size, TomlDataType.ARRAY);
                continue;
            }
            if (isMultilineArray(value) || arrayReader != null) {
                if (arrayReader == null) {
                    arrayReader = new ArrayReader();
                    multilineArrayKey = key;
                } else {
                    value = line;
                }
                arrayReader.readArray(value);
                if (ArrayReader.endOfMultilineArray(line)) {
                    int size = arrayReaderMap.size();
                    arrayReaderMap.put(size, arrayReader);
                    tomlTable.put(multilineArrayKey, size, TomlDataType.ARRAY);
                    arrayReader = null;
                }
                continue;
            }
            addEntryToTomlTable(tomlTable, key, value);
        }
        tomlTables.add(tomlTable);
        String tomlString = String.join("\n", lines);
        return new Toml(tomlString, tomlTables);
    }

    private String[] parseLine(String line) {
        String[] split = line.split("=");
        String key = split[0].trim();
        key = key.replace("\"", "");
        key = key.replace("'", "");
        String value = Arrays.stream(split).skip(1).collect(Collectors.joining("=")).trim();

        //remove comment
        boolean mustBeEscaped = false;
        boolean escaped = false;
        char escapeCharacter = ' ';
        char[] chars = value.toCharArray();
        List<Character> charList = new ArrayList<>();
        int i = 0;
        if (isMultilineString(value)) {
            i = 3;
        }
        for (; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = i == 0 ? ' ' : chars[i - 1];
            if (i == 0) {
                if (Constant.STRING_INDICATORS.contains(c)) {
                    escapeCharacter = c;
                    mustBeEscaped = true;
                    continue;
                }
            }
            if (mustBeEscaped && c == escapeCharacter && previousChar != '\\') {
                escaped = true;
                break;
            }
            if (!mustBeEscaped && c == '#') {
                //ending the loop cuz a comment is there
                break;
            }
            charList.add(c);
        }
        if (mustBeEscaped && !escaped) {
            throw new TomlReadException("Invalid toml file. Line: " + line);
        }
        if (!charList.isEmpty()) {
            value = new String(ArrayUtil.listToCharArray(charList)).trim();
        }
        if (mustBeEscaped) {
            value = escapeCharacter + value + escapeCharacter;
        }
        return new String[]{key, value};
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
            table.put(key, String.valueOf(Long.parseLong(value.substring(2), 16)), TomlDataType.NUMBER);
            return;
        }
        if (value.startsWith("0o")) {
            table.put(key, String.valueOf(Long.parseLong(value.substring(2), 8)), TomlDataType.NUMBER);
            return;
        }
        if (value.startsWith("0b")) {
            table.put(key, String.valueOf(Long.parseLong(value.substring(2), 2)), TomlDataType.NUMBER);
            return;
        }
        table.put(key, TypesUtil.convertType(value), TomlDataType.getDataType(value));
    }

    private String handleString(char[] chars, char stringIndicator) {
        List<Character> charList = new ArrayList<>();
        //looping through each char of the string
        //starting at 1 because we don't want to have the string indicator inside the actual string value
        boolean endDefined = false;
        if (chars.length == 2) {
            return "";
        }
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

    private boolean isString(String value) {
        //represents empty strings
        if (value.equals("\"\"\"\"")) return true;
        if (value.length() == 0) return false;
        return Constant.STRING_INDICATORS.contains(value.charAt(0)) && !isMultilineString(value);
    }

    private boolean isMultilineString(String value) {
        char[] chars = value.toCharArray();
        if (chars.length < 3) return false;
        if (chars[0] != chars[1] || chars[1] != chars[2]) {
            return false;
        }
        return Constant.STRING_INDICATORS.contains(value.charAt(0));
    }

    private boolean isArray(String value) {
        if (value.length() == 0) return false;
        return value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']' && !isMultilineArray(value);
    }

    private boolean isMultilineArray(String value) {
        if (value.length() == 0) {
            return false;
        } else if (value.charAt(0) == '[') {
            return value.charAt(value.length() - 1) != ']';
        } else {
            return false;
        }
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
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                char previousChar = i == 0 ? ' ' : chars[i - 1];
                //checking if it could be the end of the multiline string
                if (c == stringIndicator && previousChar != '\\' && globalIndex > 3) {
                    try {
                        char c2 = chars[i + 1];
                        char c3 = chars[i + 2];
                        //actually checking if it is the end or not
                        if (c2 == stringIndicator && c3 == stringIndicator) {
                            multilineEnd = true;
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
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

    public List<TomlTable> getTomlMaps() {
        return toml.getTomlTables();
    }

    public Toml getToml() {
        return toml;
    }

    public ArrayReader getArrayReaderByMapKey(int mapKey) {
        return arrayReaderMap.get(mapKey);
    }
}