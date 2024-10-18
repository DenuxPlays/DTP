package dev.denux.dtp.internal.entities;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
public final class Toml {

    private final List<TomlTable> tomlTables;
    private final String tomlString;

    public Toml(@Nonnull String tomlString, @Nonnull Collection<TomlTable> tomlTables) {
        this.tomlTables = new ArrayList<>(tomlTables);
        this.tomlString = tomlString;
    }

    @Nonnull
    public Optional<TomlTable> getTomlTable(@Nonnull String name) {
        return tomlTables.stream().filter(table -> table.getTableName().equals(name)).findFirst();
    }

    public TomlTable.Entry getTomlEntry(String tableName, String key) {
        Optional<TomlTable> tomlTable = getTomlTable(tableName);
        if (tomlTable.isEmpty()) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tomlTable.get().get(key);
    }

    public String getTomlAsString() {
        return tomlString;
    }
}
