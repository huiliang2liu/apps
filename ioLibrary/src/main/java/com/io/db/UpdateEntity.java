package com.io.db;

public class UpdateEntity {
    private String key;
    private Object value;

    public UpdateEntity(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(key);
        sb.append("=");
        if (value instanceof String)
            sb.append("'").append(value).append("'");
        else
            sb.append(value);
        return sb.toString();
    }
}
