package dev.denux.dtp.internal.reader.helper;

import dev.denux.dtp.exception.TomlReadException;
import dev.denux.dtp.util.Constant;

import javax.annotation.Nonnull;

/**
 * Represents a parsable type and defines how the {@link dev.denux.dtp.internal.parser.TomlParser} should parse the value.
 */
public enum ParseType {
	STRING,
	MULTILINE_STR,
	BOOLEAN,
	HEX,
	OCTAL,
	BINARY,
	INTEGER,
	FLOAT,
	POS_INF,
	NEG_INF,
	NAN,
	RFC3339,
	RFC3339_TIME,
	ARRAY,
	MULTILINE_ARRAY,
	MAP,
	MULTILINE_MAP;

	/**
	 * Gets the {@link ParseType} of the given string.
	 * @param str The {@link String}.
	 * @return The {@link ParseType}.
	 * @throws TomlReadException If the type could not be determined.
	 */
	@Nonnull
	public static ParseType getType(@Nonnull String str) throws TomlReadException {
		if (str.isEmpty()) throw new TomlReadException("The String is empty");
		//strings
		if (isString(str)) return STRING;
		if (isMultilineString(str)) return MULTILINE_STR;
		//boolean
		if (str.equals("true") || str.equals("false")) return BOOLEAN;
		//numbers
		if (isInteger(str)) return INTEGER;
		if (isFloat(str)) return FLOAT;
		if (str.equals("inf") || str.equals("+inf")) return POS_INF;
		if (str.equals("-inf")) return NEG_INF;
		if (str.equals("nan") || str.equals("+nan") || str.equals("-nan")) return NAN;
		//hex, octal, binary
		if (str.startsWith("0x")) return HEX;
		if (str.startsWith("0o")) return OCTAL;
		if (str.startsWith("0b")) return BINARY;
		//arrays
		if (isArray(str)) return ARRAY;
		if (isMultilineArray(str)) return MULTILINE_ARRAY;
		//maps
		if (isMap(str)) return MAP;
		if (isMultilineMap(str)) return MULTILINE_MAP;
		//date and time -> last because we are using regexes to check if it is a valid date or time
		if (Constant.RFC3339_REGEX.matcher(str).matches()) return RFC3339;
		if (Constant.RFC3339_TIME_REGEX.matcher(str).matches()) return RFC3339_TIME;
		//an exception is thrown if the type could not be determined
		throw new TomlReadException("Unknown type: " + str);
	}

	/**
	 * Checks if the given string is an integer.
	 * @param str The {@link String}.
	 * @return {@code true} if the string is an integer, otherwise {@code false}.
	 */
	private static boolean isInteger(@Nonnull String str) {
		try {
			Long.parseLong(str.replace("_", ""));
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	/**
	 * Checks if the given string is a float.
	 * @param str The {@link String}.
	 * @return {@code true} if the string is a float, otherwise {@code false}.
	 */
	private static boolean isFloat(@Nonnull String str) {
		try {
			Double.parseDouble(str.replace("_", ""));
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	/**
	 * Checks if the given string is an array.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is an array, otherwise {@code false}.
	 */
	private static boolean isArray(@Nonnull String value) {
		return isChar(value, '[');
	}

	/**
	 * Checks if the given string is a multiline array.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a multiline array, otherwise {@code false}.
	 */
	private static boolean isMultilineArray(@Nonnull String value) {
		return isMultilineChar(value, '[');
	}

	/**
	 * Checks if the given string is a map.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a map, otherwise {@code false}.
	 */
	private static boolean isMap(@Nonnull String value) {
		return isChar(value, '{');
	}

	/**
	 * Checks if the given string is a multiline map.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a multiline map, otherwise {@code false}.
	 */
	private static boolean isMultilineMap(@Nonnull String value) {
		return isMultilineChar(value, '{');
	}

	/**
	 * Checks if the given string starts and ends with the given char.
	 * @param value The {@link String}.
	 * @param c The char.
	 * @return {@code true} if the string starts and ends with the given char, otherwise {@code false}.
	 */
	private static boolean isChar(@Nonnull String value, char c) {
		if (value.isEmpty())
			return false;
		return value.charAt(0) == c && value.charAt(value.length() - 1) == c && !isMultilineChar(value, c);
	}

	/**
	 * Checks if the given string is a multiline string.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a multiline string, otherwise {@code false}.
	 */
	public static boolean isTable(@Nonnull String value) {
		char[] chars = value.toCharArray();
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

	/**
	 * Checks if the given string starts and ends with the given char but is not a multiline string.
	 * @param value The {@link String}.
	 * @param c The char.
	 * @return {@code true} if the string starts and ends with the given char but is not a multiline string,
	 * otherwise {@code false}.
	 */
	private static boolean isMultilineChar(@Nonnull String value, char c) {
		if (value.isEmpty())
			return false;
		else if (value.charAt(0) == c) {
			return value.charAt(value.length() - 1) != c;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the given string is a multiline string.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a multiline string, otherwise {@code false}.
	 */
	public static boolean isMultilineString(@Nonnull String value) {
		char[] chars = value.toCharArray();
		if (chars.length < 3)
			return false;
		if (chars[0] != chars[1] || chars[1] != chars[2])
			return false;
		boolean found = Constant.STRING_INDICATORS.contains(value.charAt(0));
		return found;
	}

	/**
	 * Checks if the given string is a toml-string.
	 * @param value The {@link String}.
	 * @return {@code true} if the string is a string, otherwise {@code false}.
	 */
	public static boolean isString(@Nonnull String value) {
		//represents empty strings
		if (value.equals("\"\"\"\"")) return true;
		if (value.isEmpty()) return false;
		return Constant.STRING_INDICATORS.contains(value.charAt(0)) && !isMultilineString(value);
	}
}
