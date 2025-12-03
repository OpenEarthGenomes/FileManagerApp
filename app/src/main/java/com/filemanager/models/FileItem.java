package com.filemanager.models;

public class FileItem {
    private String name;
    private String path;
    private String extension;
    private boolean isDirectory;
    private boolean isHidden;
    private long size;
    private long lastModified;
    private boolean readable;
    private boolean writable;
    private boolean executable;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    
    public boolean isDirectory() { return isDirectory; }
    public void setDirectory(boolean directory) { isDirectory = directory; }
    
    public boolean isHidden() { return isHidden; }
    public void setHidden(boolean hidden) { isHidden = hidden; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    
    public boolean isReadable() { return readable; }
    public void setReadable(boolean readable) { this.readable = readable; }
    
    public boolean isWritable() { return writable; }
    public void setWritable(boolean writable) { this.writable = writable; }
    
    public boolean isExecutable() { return executable; }
    public void setExecutable(boolean executable) { this.executable = executable; }
    
    @Override
    public String toString() {
        return "FileItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", extension='" + extension + '\'' +
                ", isDirectory=" + isDirectory +
                ", isHidden=" + isHidden +
                ", size=" + size +
                '}';
    }
}
