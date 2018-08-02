package com.teemupartanen.subautomator.beans;

import java.io.File;
import java.io.IOException;

public class VideoFile {
    private File file;
    private String name;
    private String extension;

    public VideoFile(File file) {
        this.file = file;
        this.name = file.getName().substring(0, Integer.max(0, file.getName().lastIndexOf('.')));
        this.extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension != null ? extension : "";
    }

    public String getCanonicalPath() {
        String path = "";
        if (file != null) {
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    @Override
    public String toString() {
        return "VideoFile [file=" + file + ", name=" + name + "]";
    }

}
