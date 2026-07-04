package com.example.Nansy_desktop;

public class Param {
    private String name;
    private String description;
    private String type;
    private String defaultValue;

    public Param(String name, String description, String type, String defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getDefaultValue() { return defaultValue; }
}
