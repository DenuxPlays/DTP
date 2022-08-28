package dev.denux.utils;

import dev.denux.exception.TomlMapException;
import dev.denux.internal.entities.TomlDataType;

import java.util.HashSet;
import java.util.Set;

public class TomlTable {

    private String tableName = "";
    private final Set<Entry> entries = new HashSet<>();

    public TomlTable() {}

    public TomlTable(String tableName) {
        this.tableName = tableName;
    }

    public void put(Entry entry) {
        if (get(entry.getKey()) == null) {
            throw new TomlMapException("Key already exists: " + entry.getKey());
        }
        entries.add(entry);
    }

    public void put(String key, Object value, TomlDataType type) {
        if (get(key) != null) {
            throw new TomlMapException("Key already exists: " + key);
        }
        entries.add(new Entry(key, value, type));
    }

    public void remove(String key) {
        entries.removeIf(entry -> entry.getKey().equals(key));
    }

    public Entry get(String key) {
        return entries.stream().filter(entry -> entry.getKey().equals(key)).findFirst().orElse(null);
    }

    public String getTableName() {
        return tableName;
    }

    public Set<Entry> getEntries() {
        return entries;
    }

    public static class Entry {
        private final String key;
        private final TomlDataType dataType;
        private Object value;

        public Entry(String key, Object value, TomlDataType dataType) {
            this.key = key;
            this.value = value;
            this.dataType = dataType;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public TomlDataType getDataType() {
            return dataType;
        }
    }
}
