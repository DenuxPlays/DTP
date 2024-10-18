package dev.denux.dtp.internal.reader;

import dev.denux.dtp.util.Constant;

import javax.annotation.Nonnull;

//TODO docs
public class ArrayReader {

	public final StringBuilder builder = new StringBuilder();
	public final TomlReader reader;

	public ArrayReader(@Nonnull TomlReader reader) {
		this.reader = reader;
	}

	public void readArray(@Nonnull String value) {
		StringBuilder val = new StringBuilder();
		boolean needEscaping = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			char previousChar = i == 0 ? ' ' : value.charAt(i - 1);
			if (!needEscaping && c == '#')
				continue;
			if (Constant.STRING_INDICATORS.contains(c)) {
				if (previousChar == '\\') {
					val.append(c);
					continue;
				}
				if (!needEscaping) {
					needEscaping = true;
				}
				val.append(c);
				continue;
			}
			if (c == ',' && !needEscaping && previousChar != '\"') {
				val.append(c);
				continue;
			}
			if (c != ' ') {
				val.append(c);
			}
		}
		builder.append(val.toString().trim());
	}

	@Nonnull
	public String getFormattedString() {
		return builder.toString();
	}
}
