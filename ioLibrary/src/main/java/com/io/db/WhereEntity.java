package com.io.db;

import java.util.ArrayList;
import java.util.List;

public class WhereEntity {
    public enum BetweenType {
        EQUALS("="), GREATER(">"), LESS("<"), NON("!="), GREATER_EQUALS(">="), LESS_EQUALS("<="), LIKE(" LIKE "), IN(" IN "), BETWEEN(" BETWEEN ");
        private String value;

        private BetweenType(String value) {
            this.value = value;
        }
    }

    private String key;
    private List<Object> values = new ArrayList<>();
    private BetweenType type;

    public WhereEntity(String key, Object value, BetweenType type) {
        this.key = key;
        values.add(value);
        this.type = type;
    }

    public WhereEntity add(Object value) {
        values.add(value);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(key);
        sb.append(type.value);
        if (type == BetweenType.IN) {
            sb.append("(");
            int size = values.size() - 1;
            for (int i = 0; i <= size; i++) {
                Object o = values.get(i);
                if (o instanceof String) {
                    sb.append("'").append(o).append("'");
                } else {
                    sb.append(o);
                }
                if (i < size)
                    sb.append(",");
            }
            sb.append(")");
        } else if (type == BetweenType.BETWEEN) {
            Object o1 = values.get(0);
            if (o1 instanceof String) {
                sb.append("'").append(o1).append("'");
            } else {
                sb.append(o1);
            }
            sb.append(" AND ");
            Object o2 = values.get(1);
            if (o2 instanceof String) {
                sb.append("'").append(o2).append("'");
            } else {
                sb.append(o2);
            }
        } else {
            Object o = values.get(0);
            if (o instanceof String) {
                sb.append("'").append(o).append("'");
            } else {
                sb.append(o);
            }
        }
        return sb.toString();
    }
}
