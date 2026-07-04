package com.example.Nansy_desktop;

import java.util.Arrays;
import java.util.List;

public enum ActionCode {
    SHUTDOWN(
        "Выключение компьютера",
        Arrays.asList(
            new Param("delay", "задержка", "int", "5")
        )
    );

    private final String description;
    private final List<Param> parameters;

    ActionCode(String description, List<Param> parameters) {
        this.description = description;
        this.parameters = parameters;
    }

    public String getDescription() { return description; }
    public List<Param> getParameters() { return parameters; }
}