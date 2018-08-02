package com.teemupartanen.subautomator.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

public class PropertiesService {

    private static final String defaultPath = "C:/Program Files/Subtitle Edit/SubtitleEdit.exe";
    private static Properties properties = null;
    private static final Logger logger = LogManager.getLogger(PropertiesService.class);

    public static String getSubtitleEditExecutableLocation() {
        if (properties == null) {
            loadProperties();
        }
        String subEditPath = properties.getProperty("subtitle.edit.path");
        if (subEditPath == null || !new File(subEditPath).exists()) {
            logger.error("SubEditPath in config file was not found, using default path.");
            subEditPath = defaultPath;
        }
        return subEditPath;
    }

    public static HashSet<String> getSupportedExtensions() {
        if (properties == null) {
            loadProperties();
        }
        HashSet<String> extensions = new HashSet<>(Arrays.asList(properties.getProperty("supported.file.extensions").split(" ")));
        if (extensions.size() == 0) {
            logger.error("Extension setting was not set, using defaults (mkv, srt)");
            extensions.add("mkv");
            extensions.add("srt");
        }
        logger.debug("Supported extensions size: " + extensions.size());
        return extensions;
    }

    private static void loadProperties() {
        properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
            logger.error("Failed to load config file", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }

    }

}
