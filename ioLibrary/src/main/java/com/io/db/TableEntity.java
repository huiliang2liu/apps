package com.io.db;

import java.util.ArrayList;
import java.util.List;

public class TableEntity {
    public enum KeyType {
        INTEGER("INTEGER"), DOUBLE("DECIMAL(%d,%d)"), STRING("VARCHAR(%d)");
        private String name;

        private KeyType(String name) {
            this.name = name;
        }

        public String value() {
            return name;
        }
    }

    public enum Type {
        NOT_NULL("NOT NULL"), UNIQUE("UNIQUE"), PRIMARY_KEY("PRIMARY KEY"), FOREIGN_KEY("FOREIGN KEY (%s) REFERENCES %s (%s)"), AUTO_INCREMENT("AUTOINCREMENT");
        private String name;

        private Type(String name) {
            this.name = name;
        }

        public String value() {
            return name;
        }
    }

    private String name;
    private KeyType type;
    private int length;
    private int rightLength;
    private String tableName;
    private String tableKey;
    private List<Type> types = new ArrayList<>();


    public TableEntity setName(String name) {
        this.name = name;
        return this;
    }


    public TableEntity setType(KeyType type) {
        if (type == KeyType.INTEGER) {
        } else if (type == KeyType.STRING) {
            length = 256;
        } else {
            length = 10;
            rightLength = 10;
        }
        this.type = type;
        return this;
    }


    public TableEntity setLength(int length) {
        this.length = length;
        return this;
    }


    public TableEntity setRightLength(int rightLength) {
        this.rightLength = rightLength;
        return this;
    }


    public TableEntity setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }


    public TableEntity setTableKey(String tableKey) {
        this.tableKey = tableKey;
        return this;
    }


    public TableEntity add(Type type) {
        this.types.add(type);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(name);
        sb.append(" ");
        if (type == KeyType.INTEGER) {
            sb.append(type.value()).append(" ");
        } else if (type == KeyType.DOUBLE) {
            sb.append(String.format(type.value(), length, rightLength)).append(" ");
        } else {
            sb.append(String.format(type.value(), length)).append(" ");
        }
        if (types != null && types.size() > 0) {
            int size = types.size();
            for (int i = 0; i < size; i++) {
                Type type = types.get(i);
                if (type == Type.FOREIGN_KEY) {
                    sb.append(String.format(type.value(), name, tableName, tableKey));
                } else {
                    sb.append(type.value());
                }
                if (i != size - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
    //    private
}
