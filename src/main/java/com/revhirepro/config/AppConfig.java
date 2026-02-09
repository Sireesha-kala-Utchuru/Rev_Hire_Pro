package com.revhirepro.config;

import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) throw new IllegalStateException("application.properties not found");
            PROPERTIES.load(inputStream);
        } catch (Exception ex) {
            System.err.println("Failed to load config: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private AppConfig() {}

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing config: " + key);
        return value.trim();
    }
}
