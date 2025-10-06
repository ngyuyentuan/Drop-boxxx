package com.dropbox.core.examples.android.model;

public class DropboxFile {
    private String name;
    private String pathLower;
    private long size;

    public DropboxFile(String name, String pathLower, long size) {
        this.name = name;
        this.pathLower = pathLower;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getPathLower() {
        return pathLower;
    }

    public long getSize() {
        return size;
    }
}
