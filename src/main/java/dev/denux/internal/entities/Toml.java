package dev.denux.internal.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Toml {

    private final Set<TomlTable> tomlTables;
    private final String tomlString;

    public Toml(String tomlString, Collection<TomlTable> tomlMaps) {
        this.tomlTables = new HashSet<>(tomlMaps);
        this.tomlString = tomlString;
    }

    public Set<TomlTable> getTomlTables() {
        return tomlTables;
    }

    public String getTomlAsString() {
        return tomlString;
    }
}
