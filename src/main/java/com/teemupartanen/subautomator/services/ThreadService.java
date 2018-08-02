package com.teemupartanen.subautomator.services;

import com.teemupartanen.subautomator.beans.VideoFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class ThreadService {

    private static final Logger logger = LogManager.getLogger(ThreadService.class);
    private VideoFile[] filesToBeProcessed;
    private int filesToBeProcessedIndex = 0;
    private float completedProcessedCount = 0;
    private String subEditPath;
    private int usableThreads;

    public int getProgress() {
        int percentage = 0;
        if (filesToBeProcessed != null && filesToBeProcessed.length > 0) {
            percentage = Math.round(completedProcessedCount / filesToBeProcessed.length * 100);
        }
        logger.debug("Progress percentage: " + percentage);
        return percentage;
    }

    public ThreadService(HashSet<VideoFile> files) {
        filesToBeProcessed = files.toArray(new VideoFile[0]);
        subEditPath = PropertiesService.getSubtitleEditExecutableLocation();
        logger.debug("SubEdit location: " + subEditPath);
        usableThreads = Math.min(filesToBeProcessed.length, Runtime.getRuntime().availableProcessors());
        logger.debug("Usable cores: " + usableThreads);
    }

    public void processFiles() {
        for (int i = 1; i <= usableThreads; i++) {
            Thread fileProcessingThread = createFileProcessingThread();
            fileProcessingThread.start();
        }
    }

    private Thread createFileProcessingThread() {
        return new Thread(() -> {
            VideoFile nextFile = getNextVideoFile();
            while (nextFile != null) {
                Process extractSubtitleProcess = extractSubtitle(nextFile);
                while (extractSubtitleProcess != null && extractSubtitleProcess.isAlive()) {
                    try {
                        extractSubtitleProcess.waitFor();
                    } catch (InterruptedException e) {
                        logger.error(e);
                    } finally {
                        logger.debug("waiting...");
                    }
                }
                completedProcessedCount++;
                nextFile = getNextVideoFile();
            }
            logger.debug("Out of files");
        });
    }

    private synchronized VideoFile getNextVideoFile() {
        VideoFile resultVideoFile = null;
        if (filesToBeProcessedIndex < filesToBeProcessed.length) {
            resultVideoFile = filesToBeProcessed[filesToBeProcessedIndex];
            filesToBeProcessedIndex++;
        }
        return resultVideoFile;
    }

    private Process extractSubtitle(VideoFile file) {
        if (file.getFile().exists()) {
            try {
                ProcessBuilder builder = new ProcessBuilder(subEditPath, "/convert", file.getCanonicalPath(), "SubRip", "/removetextforhi", "/fixcommonerrors", "/overwrite");
                logger.info("Converting file: " + file.getName());
                return builder.start();
            } catch (Exception e) {
                logger.error(e);
                return null;
            }
        } else {
            logger.error("File " + file.getFile().getAbsolutePath() + " not found.");
            return null;
        }

    }

}
