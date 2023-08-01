package dev.denux.dtp.internal.reader.helper;

import dev.denux.dtp.exception.TomlReadException;
import dev.denux.dtp.util.ArrayUtil;
import dev.denux.dtp.util.Constant;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

//TODO docs

/**
 * A helper class that helps to prepare the value for the parser.
 */
public class ParseHelper {

	private ParseHelper() {}

	/**
	 * Removes the quotes from the String.
	 * @param str the String to remove the quotes from
	 * @return the String without the quotes
	 */
	@Nonnull
	public static String removeQuotes(@Nonnull String str) {
		List<Character> charList = new ArrayList<>();
		//returning if it's an empty string
		if (str.length() == 2) return "";

		boolean escaped = false;
		char indicator = str.charAt(0);
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			char prev = str.charAt(i - 1);
			if (c == indicator && prev != '\\') {
				escaped = true;
				break;
			}
			charList.add(c);
		}
		if (!escaped) throw new TomlReadException("The end of the String is not defined: " + str);
		return ArrayUtil.charListToString(charList);
	}

	/**
	 * Removes the multiline quotes from the String.
	 * @param str the String to remove the multiline quotes from
	 * @return the String without the multiline quotes
	 */
	@Nonnull
	public static String removeMultilineQuotes(@Nonnull String str) {
		List<Character> charList = new ArrayList<>();
		//returning if it's an empty string
		if (str.length() == 6) return "";

		boolean escaped = false;
		char indicator = str.charAt(0);
		for (int i = 2; i < str.length(); i++) {
			char c = str.charAt(i);
			char c2 = str.charAt(i - 1);
			char c3 = str.charAt(i - 2);
			if (ArrayUtil.charListToString(List.of(c, c2, c3))
					.equals(String.format("%c%c%c", indicator, indicator, indicator)) && (c2 != '\\' || c != '\\') && i > 3) {
				charList.remove(charList.size() - 1);
				charList.remove(charList.size() - 2);
				escaped = true;
				break;
			}
			charList.add(c);
		}
		if (!escaped) throw new TomlReadException("The end of the String is not defined: " + str);
		return removeQuotes(ArrayUtil.charListToString(charList));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	@Nonnull
	public static String parseHex(@Nonnull String str) {
		return String.valueOf(Long.parseLong(str.substring(2), 16));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	@Nonnull
	public static String parseOctal(@Nonnull String str) {
		return String.valueOf(Long.parseLong(str.substring(2), 8));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	@Nonnull
	public static String parseBinary(@Nonnull String str) {
		return String.valueOf(Long.parseLong(str.substring(2), 2));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	public static long parseInteger(@Nonnull String str) {
		return Long.parseLong(str.replace("_", ""));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	public static double parseFloat(@Nonnull String str) {
		return Double.parseDouble(str.replace("_", ""));
	}

	/**
	 * Parses the String to the correct type.
	 * @param str the String to parse
	 * @return the parsed String
	 */
	@Nonnull
	public static String parseTable(@Nonnull String str) {
		List<Character> charList = new ArrayList<>();
		char[] chars = str.toCharArray();
		//starting at one because we don't want to have the "[" inside the name
		boolean endDefined = false;
		for (int i = 1; i < chars.length; i++) {
			char c = chars[i];
			if (i == 1 && Constant.STRING_INDICATORS.contains(c)) {
				for (char ch : chars) {
					charList.add(ch);
				}
				charList.remove(0);
				return removeQuotes(ArrayUtil.charListToString(charList));
			}
			if (c == ']') {
				endDefined = true;
				break;
			}
			charList.add(c);
		}
		if (!endDefined) {
			throw new TomlReadException("Toml table is not properly defined: " + ArrayUtil.charListToString(charList));
		}
		return ArrayUtil.charListToString(charList);
	}
}
