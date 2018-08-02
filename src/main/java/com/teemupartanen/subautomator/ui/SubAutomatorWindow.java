package com.teemupartanen.subautomator.ui;

import com.teemupartanen.subautomator.events.FileDropEvent;
import com.teemupartanen.subautomator.SubAutomator;
import com.teemupartanen.subautomator.ui.locale.Words;
import com.teemupartanen.subautomator.services.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.Thread.sleep;

public class SubAutomatorWindow extends JFrame {

    private static final long serialVersionUID = 2584646286792595161L;
    private static final Logger logger = LogManager.getLogger(SubAutomatorWindow.class);

    public SubAutomatorWindow() {
        createUI();
    }

    private void createUI() {
        JButton quitButton = createQuitButton();
        logger.trace("QuitButton initialized");

        JButton generateButton = createGenerateButton();
        logger.trace("GenerateButton initialized");

        JTextArea uriTextArea = createUriTextField();
        logger.trace("UriTextField initialized");

        createLayout(quitButton, generateButton, uriTextArea);

        setTitle(Words.APP_NAME + " " + Words.VERSION + " " + SubAutomator.version);
        setSize(600, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.trace("Closing SubAutomator");
                System.exit(0);
            }
        });
    }

    private JTextArea createUriTextField() {
        JTextArea textField = new JTextArea();
        textField.setEditable(false);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        textField.setDragEnabled(true);
        textField.setDropTarget(new FileDropEvent());
        return textField;
    }

    private JButton createGenerateButton() {
        JButton button = new JButton(Words.BUTTON_GENERATE_READY);
        button.addActionListener(e -> {
            logger.trace("GenerateButton pressed");
            EventService.eventGenerate();
            monitorProgress(button);
        });
        return button;
    }

    private JButton createQuitButton() {
        JButton button = new JButton(Words.BUTTON_QUIT);
        button.addActionListener(e -> {
            logger.trace("QuitButton pressed - exiting.");
            System.exit(0);
        });
        return button;
    }

    private void createLayout(JButton quitButton, JButton generateButton, JTextArea uriTextArea) {
        Container pane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(pane);
        pane.setLayout(groupLayout);

        groupLayout.setAutoCreateContainerGaps(true);

        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addComponent(generateButton)
                .addComponent(uriTextArea)
                .addComponent(quitButton)
        );

        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addComponent(generateButton)
                .addComponent(uriTextArea)
                .addComponent(quitButton)
        );

        logger.trace("Layout initialized");
    }

    private void monitorProgress(JButton button) {
        button.setEnabled(false);
        new Thread(() -> {
            int percentage = 0;
            while (percentage < 100) {
                percentage = EventService.getProgress();
                if (percentage == 100) {
                    button.setText(Words.BUTTON_GENERATE_READY);
                    button.setEnabled(true);
                } else {
                    button.setText(Words.BUTTON_GENERATE_PROCESSING + " " + percentage + "%");
                }
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }).start();
    }

}
