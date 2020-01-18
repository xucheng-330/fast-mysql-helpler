package com.xucheng.fastmysql.api.config;

public class ColumnDefinition {

    private Class<?> type;
    private String name;
    private Integer length ;
    private Integer decimalLength;

    public ColumnDefinition(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public ColumnDefinition(Class<?> type, String name, Integer length) {
        this.type = type;
        this.name = name;
        this.length = length;
    }

    public ColumnDefinition(Class<?> type, String name, Integer length, Integer decimalLength) {
        this.type = type;
        this.name = name;
        this.length = length;
        this.decimalLength = decimalLength;
    }

    public Class<?> getType() {
        return type;
    }

    public ColumnDefinition setType(Class<?> type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public ColumnDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public ColumnDefinition setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Integer getDecimalLength() {
        return decimalLength;
    }

    public ColumnDefinition setDecimalLength(Integer decimalLength) {
        this.decimalLength = decimalLength;
        return this;
    }
}
