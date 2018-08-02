package com.teemupartanen.subautomator.services;

import com.teemupartanen.subautomator.beans.VideoFile;

import java.io.File;
import java.util.HashSet;

public class EventService {

    private static ThreadService threadService;
    private static HashSet<VideoFile> droppedFiles;

    public static int getProgress() {
        int percentage = 100;
        if (threadService != null) {
            percentage = threadService.getProgress();
        }
        return percentage;
    }

    public static void filesDroppedEvent(File[] files) {
        droppedFiles = convertFilesToVideoFiless(files);
    }

    public static String getDroppedFilesAsString() {
        StringBuilder fileNames = new StringBuilder();
        if (droppedFiles != null) {
            for (VideoFile file : droppedFiles) {
                if (fileNames.length() != 0) {
                    fileNames.append(System.getProperty("line.separator"));
                }
                fileNames.append(file.getCanonicalPath());
            }
        }
        return fileNames.toString();
    }

    private static HashSet<VideoFile> convertFilesToVideoFiless(File[] files) {
        HashSet<VideoFile> videoFiles = new HashSet<>();
        HashSet<String> supportedExtensions = PropertiesService.getSupportedExtensions();
        for (File file : files) {
            if (file.exists()) {
                if (file.isDirectory() && file.listFiles() != null) {
                    videoFiles.addAll(convertFilesToVideoFiless(file.listFiles()));
                } else {
                    VideoFile videoFile = new VideoFile(file);
                    if (supportedExtensions.contains(videoFile.getExtension())) {
                        videoFiles.add(videoFile);
                    }
                }
            }
        }
        return videoFiles;
    }

    public static void eventGenerate() {
        if (droppedFiles != null && droppedFiles.size() > 0) {
            threadService = new ThreadService(droppedFiles);
            threadService.processFiles();
        }
    }

}
