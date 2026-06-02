package com.example.Nansy_desktop;

public class CommandExecutor {
    
    public static void handleCommand(String command) {
        if (command.contains("shutdown")) {
            try {
                Runtime.getRuntime().exec(new String[]{"shutdown", "-h", "now"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}