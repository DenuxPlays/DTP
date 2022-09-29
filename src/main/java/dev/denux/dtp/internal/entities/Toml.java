package dev.denux.dtp.internal.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class Toml {

    private final List<TomlTable> tomlTables;
    private final String tomlString;

    public Toml(String tomlString, Collection<TomlTable> tomlTables) {
        this.tomlTables = new ArrayList<>(tomlTables);
        this.tomlString = tomlString;
    }

    public List<TomlTable> getTomlTables() {
        return tomlTables;
    }

    public Optional<TomlTable> getTomlTable(String name) {
        return tomlTables.stream().filter(table -> table.getTableName().equals(name)).findFirst();
    }

    public TomlTable.Entry getTomlEntry(String tableName, String key) {
        Optional<TomlTable> tomlTable = getTomlTable(tableName);
        if (!tomlTable.isPresent()) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tomlTable.get().get(key);
    }

    public String getTomlAsString() {
        return tomlString;
    }
}
