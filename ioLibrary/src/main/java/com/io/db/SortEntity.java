package com.io.db;

public class SortEntity {
    public enum SortType {
        DESC("DESC"), ASC("ASC");
        private String value;

        private SortType(String value) {
            this.value = value;
        }
    }

    private String key;
    private SortType type = SortType.ASC;

    public SortEntity(String key) {
        this.key = key;
    }

    public SortEntity(String key, SortType type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(key);
        sb.append(" ").append(type.value);
        return sb.toString();
    }
}
