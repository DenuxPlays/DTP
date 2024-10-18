package dev.denux.dtp.util;

import lombok.Getter;
import lombok.Setter;


/**
 * A Pair of two elements.
 *
 * @param <K> The first value.
 * @param <V> The second value.
 */
@Getter
@Setter
public class Pair<K, V> {
	private K key;
	private V value;

	/**
	 * Creates a new {@link Pair} of to {@link Object}s.
	 *
	 * @param key the first {@link Object}.
	 * @param value the second {@link Object}.
	 */
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
}
