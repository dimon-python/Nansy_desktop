package com.example.Nansy_desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties defaultProps = new Properties();
    private static Properties userProps = new Properties();
    private static File userConfigFile;

    static {
        try (InputStream input = ConfigManager.class.getResourceAsStream("/nansy.properties")) {
            if (input != null) {
                defaultProps.load(input);
            } else {
                System.err.println("nansy.properties не найден");
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки системной конфигурации");
        }

        userConfigFile = new File(System.getProperty("user.dir"), "/nansy.user.properties");
        if (userConfigFile.exists()) {
            try (FileInputStream input = new FileInputStream(userConfigFile)) {
                userProps.load(input);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки пользовательской конфигурации");
            }
        }
    }

    public static String getUserProperty(String key) {
        return userProps.getProperty(key);
    }

    public static String getSystemProperty(String key) {
        return defaultProps.getProperty(key);
    }

    public static void setUserProperty(String key, String value) {
        userProps.setProperty(key, value);
        try (FileOutputStream output = new FileOutputStream(userConfigFile)) {
            userProps.store(output, null);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользовательской конфигурации");
        }
    }
}
