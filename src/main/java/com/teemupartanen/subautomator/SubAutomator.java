package com.teemupartanen.subautomator;

import com.teemupartanen.subautomator.ui.SubAutomatorWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class SubAutomator {

    public final static double version = 0.12;
    private static final Logger logger = LogManager.getLogger(SubAutomator.class);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SubAutomatorWindow userInterface = new SubAutomatorWindow();
            userInterface.setVisible(true);
            logger.trace("SubAutomator started");
        });
    }

}
