package dev.denux.dtp.internal.reader;

import dev.denux.dtp.exception.TomlReadException;
import dev.denux.dtp.internal.entities.Toml;
import dev.denux.dtp.internal.entities.TomlDataType;
import dev.denux.dtp.internal.entities.TomlTable;
import dev.denux.dtp.internal.reader.helper.ParseHelper;
import dev.denux.dtp.internal.reader.helper.ParseType;
import dev.denux.dtp.util.ArrayUtil;
import dev.denux.dtp.util.Constant;
import dev.denux.dtp.util.Pair;
import dev.denux.dtp.util.RFC3339Util;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO docs
@Getter
public class TomlReader {

	private final Toml toml;
	private final List<String> lines;
	private final AtomicInteger i = new AtomicInteger();

	public TomlReader(@Nonnull Reader reader) {
		this(new BufferedReader(reader).lines());
	}

	public TomlReader(@Nonnull Stream<String> str) {
		lines = str.collect(Collectors.toList());
		toml = read(String.join("\n", lines));
	}

	@Nonnull
	private Toml read(@Nonnull String str) {
		TomlTable currentTable = new TomlTable(); //represents the current or root table
		Set<TomlTable> tables = new HashSet<>();

		while (i.get() < lines.size()) {
			String line = lines.get(i.getAndIncrement());
			line = line.trim();
			if (line.startsWith("#")) continue; //ignore comments
			Pair<String, String> pair = splitLine(line);
			line = filterComments(pair.getValue());
			pair.setValue(line);
			if (line.isEmpty()) continue; //ignore empty lines
			if (ParseType.isTable(pair.getValue())) {
				tables.add(currentTable);
				currentTable = new TomlTable(ParseHelper.parseTable(line));
				continue;
			}
			ParseType type = ParseType.getType(pair.getValue());
			switch (type) {
				case STRING :
					currentTable.put(pair.getKey(), ParseHelper.removeQuotes(pair.getValue()), TomlDataType.STRING);
					break;
				case MULTILINE_STR:
					currentTable.put(pair.getKey(), readMultilineString(pair.getValue(), i.get()), TomlDataType.STRING);
					break;
				case BOOLEAN :
					currentTable.put(pair.getKey(), Boolean.valueOf(pair.getValue()), TomlDataType.BOOLEAN);
					break;
				case HEX :
					currentTable.put(pair.getKey(), ParseHelper.parseHex(pair.getValue()), TomlDataType.NUMBER);
					break;
				case OCTAL :
					currentTable.put(pair.getKey(), ParseHelper.parseOctal(pair.getValue()), TomlDataType.NUMBER);
					break;
				case BINARY :
					currentTable.put(pair.getKey(), ParseHelper.parseBinary(pair.getValue()), TomlDataType.NUMBER);
					break;
				case INTEGER:
					currentTable.put(pair.getKey(), ParseHelper.parseInteger(pair.getValue()), TomlDataType.NUMBER);
					break;
				case FLOAT:
					currentTable.put(pair.getKey(), ParseHelper.parseFloat(pair.getValue()), TomlDataType.NUMBER);
					break;
				case POS_INF:
					currentTable.put(pair.getKey(), Double.POSITIVE_INFINITY, TomlDataType.INFINITE_POSITIVE);
					break;
				case NEG_INF:
					currentTable.put(pair.getKey(), Double.NEGATIVE_INFINITY, TomlDataType.INFINITE_NEGATIVE);
					break;
				case NAN :
					currentTable.put(pair.getKey(), Double.NaN, TomlDataType.NAN);
					break;
				case RFC3339:
					currentTable.put(pair.getKey(), RFC3339Util.parseDateTime(pair.getValue()), TomlDataType.DATETIME);
					break;
				case RFC3339_TIME:
					currentTable.put(pair.getKey(), RFC3339Util.parseTime(pair.getValue()), TomlDataType.TIME);
					break;
				case ARRAY:
					throw new UnsupportedOperationException("Arrays are not supported yet!");
				case MULTILINE_ARRAY:
					throw new UnsupportedOperationException("Multiline arrays are not supported yet!");
				case MAP:
					throw new UnsupportedOperationException("Maps are not supported yet!");
				case MULTILINE_MAP:
					throw new UnsupportedOperationException("Multiline maps are not supported yet!");
				default: throw new TomlReadException("Unknown type: " + type);
			}
		}
		tables.add(currentTable);
		return new Toml(str, tables);
	}

	@Nonnull
	private String readMultilineString(@Nonnull String str, int currentIndex) {
		char escapeSequence = str.charAt(0);
		String endSequence = String.format("%c%c%c", escapeSequence, escapeSequence, escapeSequence);
		String finalStr = str;
		while (i.get() + 1 < lines.size()) {
			String line = lines.get(i.getAndIncrement());
			finalStr = String.join("\n", finalStr, line);
			if (line.endsWith(endSequence)&& i.get() > currentIndex) {
				break;
			}
		}
		return ParseHelper.removeMultilineQuotes(finalStr) + "\n";
	}

	@Nonnull
	private Pair<String, String> splitLine(@Nonnull String str) {
		String[] split = str.split("=");
		String key = split[0].trim();
		key = key.replace("\"", "");
		key = key.replace("'", "");
		String value = Arrays.stream(split).skip(1).collect(Collectors.joining("=")).trim();
		//allows parsing of tables
		if (value.isEmpty()) value = key;
		return new Pair<>(key, value);
	}

	@Nonnull
	private String filterComments(@Nonnull String line) {
		//excludes an edge case when a value is only the start of a multiline string without containing any usefull
		//content
		if (line.equals("\"\"\"")) return line;
		List<Character> charList = new ArrayList<>();
		boolean mustBeEscaped = false;
		boolean escaped = false;
		String escapeSequence = " ";
		int i = 0;
		if (ParseType.isMultilineString(line)) {
			escapeSequence  = String.format("%c%c%c", line.charAt(0), line.charAt(0), line.charAt(0));
			mustBeEscaped = true;
			i = 3;
		}
		for (; i < line.length(); i++) {
			char c = line.charAt(i);
			char previousChar = i == 0 ? ' ' : line.charAt(i - 1);
			if (i == 0 && Constant.STRING_INDICATORS.contains(c)) {
				escapeSequence = String.valueOf(c);
				mustBeEscaped = true;
				continue;
			}
			if (mustBeEscaped && escapeSequence.equals(String.valueOf(c)) && previousChar != '\\') {
				escaped = true;
				break;
			}
			if (!mustBeEscaped && c == '#')
				//ending the loop cuz a comment is there
				break;
			charList.add(c);
		}
		if (mustBeEscaped && !escaped && !ParseType.isMultilineString(line)) {
			throw new TomlReadException("Invalid toml file. Line: " + line);
		}
		if (!charList.isEmpty()) {
			line = ArrayUtil.charListToString(charList);
		}
		if (mustBeEscaped) {
			line = escapeSequence + line + escapeSequence;
		}
		return line.trim();
	}

	@Deprecated
	@Nullable
	public ArrayReader getArrayReaderByMapKey(int key) {
		throw new UnsupportedOperationException("Arrays are not supported yet!");
	}
}
