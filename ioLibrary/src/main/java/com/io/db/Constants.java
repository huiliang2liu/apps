package com.io.db;

public interface Constants {
    String CREATE_DB = "CREATE DATABASE %s";//创建数据库
    String DROP_DB = "DROP DATABASE %s";//删除数据库
    String CREATE_TABLE = "CREATE TABLE %s (%s)";//创建表格
    String DROP_TABLE = "DROP TABLE %s";//删除表格
    String ALTER_ADD = "ALTER TABLE %s ADD %s";//添加一列
    String ALTER_DROP = "ALTER TABLE %s DROP COLUMN %s";//删除某一列
    String ALTER_ALTER = "ALTER TABLE %s ALTER COLUMN %s";//更新某一列数据类型
    String INSERT1 = "INSERT INTO %s VALUES (%s)";//不指定插入的列
    String INSERT2 = "INSERT INTO %s (%s) VALUES (%s)";//指定插入的列
    String SELECT = "SELECT %s FROM %s";//查询所有数据
    String SELECT_ORDER = "SELECT %s FROM %s ORDER BY %s";//查询所有数据
    String SELECT_WHERE = "SELECT %s FROM %s WHERE %s";//查询所有数据
    String SELECT_WHERE_ORDER = "SELECT %s FROM %s WHERE %s ORDER BY %s";//查询所有数据
    String DELETE_WHERE = "DELETE FROM %s WHERE %s";//删除所有数据
    String UPDATE = "UPDATE %s SET %s WHERE %s";//更新数据
}
