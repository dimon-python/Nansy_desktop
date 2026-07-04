package com.example.Nansy_desktop.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.example.Nansy_desktop.ActionCode;
import com.example.Nansy_desktop.Command;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.jsonwebtoken.io.IOException;

public class CommandManager {
    private static Map<Long, Command> commandIdList = new HashMap<>();
    private static Map<Long, String> commandNameList = new HashMap<>();
    private static Long idCounter = 0L;
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data";
    private static final String COMMANDS_FILE_PATH = DATA_DIR + "/commands.json";
    
    public static void handleCommand(Long commandId) {
        Command command = commandIdList.get(commandId);
        if (command == null) {
            System.out.println("❌ Команда не найдена");
            return;
        }

        ActionCode action;
        try {
            action = ActionCode.valueOf(command.getActionCode());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Неизвестный actionCode: " + command.getActionCode());
            return;
        }

        switch (action) {
            case SHUTDOWN:
                executeShutdown();
                break;
        }
    }

    private static void executeShutdown() {
        try {
            Runtime.getRuntime().exec(new String[]{"shutdown", "-h", "now"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createCommand(String name, String actionCode) {
        Command command = new Command(name, actionCode);
        idCounter += 1;
        Long id = idCounter;

        commandIdList.put(id, command);
        commandNameList.put(id, name);
    }

    public static void saveCommandsToFile() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Gson gson = new Gson();
            String commandJson = gson.toJson(commandIdList);
            
            try (FileWriter writer = new FileWriter(COMMANDS_FILE_PATH)) {
                writer.write(commandJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCommands() {
        StringBuilder content = new StringBuilder();

        try {
            FileReader reader = new FileReader(COMMANDS_FILE_PATH);
            
            int character;
            while ((character = reader.read()) != -1) {
                content.append((char) character);
            }
            reader.close();

            String json = content.toString();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<Long, Command>>(){}.getType();
            Map<Long, Command> commandMap = gson.fromJson(json, type);

            for (Map.Entry<Long, Command> entry : commandMap.entrySet()) {
                Long id = entry.getKey();
                Command command = entry.getValue();

                commandIdList.put(id, command);
                commandNameList.put(id, command.getName());

                if (id > idCounter) {
                    idCounter = id;
                }
            }

            System.out.println("Размер commandNameList: " + commandNameList.size());

            for (Map.Entry<Long, String> entry : commandNameList.entrySet()) {
                String id = entry.getKey().toString();
                String name = entry.getValue();
                System.out.println("name: " + name + " id: " + id);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}