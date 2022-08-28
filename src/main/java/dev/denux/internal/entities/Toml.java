package dev.denux.internal.entities;

import dev.denux.utils.TomlTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Toml {

    private final Set<TomlTable> tomlMaps;
    private final String tomlString;

    public Toml(String tomlString, Collection<TomlTable> tomlMaps) {
        this.tomlMaps = new HashSet<>(tomlMaps);
        this.tomlString = tomlString;
    }

    public Set<TomlTable> getTomlMaps() {
        return tomlMaps;
    }

    public String getTomlAsString() {
        return tomlString;
    }
}
