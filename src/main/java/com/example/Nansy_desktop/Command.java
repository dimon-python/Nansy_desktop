package com.example.Nansy_desktop;

import java.util.HashMap;
import java.util.Map;

public class Command {
    private Long id;
    private String name;
    private String actionCode;
    private Map<String, String> params = new HashMap<>();
    
    public Command(String name, String actionCode) {
        this.name = name;
        this.actionCode = actionCode;
    }
    
    public String getActionCode() { return actionCode; }
    public String getName() { return name; }
    public Long getId() { return id; }
    public Map<String, String> getParams() { return params; }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}