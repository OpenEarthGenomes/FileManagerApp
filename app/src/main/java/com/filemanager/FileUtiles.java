package com.filemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.util.Log;
import androidx.documentfile.provider.DocumentFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileUtils {
    
    public static final String PATH_INTERNAL_STORAGE = "internal";
    public static final String PATH_EXTERNAL_STORAGE = "external";
    public static final String PATH_RECENT_FILES = "recent";
    
    private static final String TAG = "FileUtils";
    
    public static List<FileItem> getFilesInDirectory(String path) {
        List<FileItem> fileList = new ArrayList<>();
        
        try {
            File directory = new File(path);
            
            if (!directory.exists() || !directory.isDirectory()) {
                Log.e(TAG, "Invalid directory: " + path);
                return fileList;
            }
            
            File[] files = directory.listFiles();
            if (files == null) return fileList;
            
            // First add directories
            for (File file : files) {
                if (file.isDirectory() && !file.isHidden()) {
                    fileList.add(createFileItem(file));
                }
            }
            
            // Then add files
            for (File file : files) {
                if (!file.isDirectory() && !file.isHidden()) {
                    fileList.add(createFileItem(file));
                }
            }
            
            // Add hidden files if enabled
            for (File file : files) {
                if (file.isHidden()) {
                    fileList.add(createFileItem(file));
                }
            }
            
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception accessing directory: " + path, e);
        } catch (Exception e) {
            Log.e(TAG, "Error reading directory: " + path, e);
        }
        
        return fileList;
    }
    
    public static FileItem createFileItem(File file) {
        FileItem item = new FileItem();
        item.setName(file.getName());
        item.setPath(file.getAbsolutePath());
        item.setDirectory(file.isDirectory());
        item.setHidden(file.isHidden());
        item.setSize(file.length());
        item.setLastModified(file.lastModified());
        item.setExtension(getFileExtension(file.getName()));
        item.setReadable(file.canRead());
        item.setWritable(file.canWrite());
        item.setExecutable(file.canExecute());
        
        return item;
    }
    
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }
        
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) 
               + " " + units[digitGroups];
    }
    
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public static boolean copyFile(File source, File destination) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        
        try {
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }
            
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file: " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (sourceChannel != null) sourceChannel.close();
                if (destChannel != null) destChannel.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing channels", e);
            }
        }
    }
    
    public static boolean moveFile(File source, File destination) {
        if (copyFile(source, destination)) {
            return deleteFile(source);
        }
        return false;
    }
    
    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteFile(child);
                }
            }
        }
        return file.delete();
    }
    
    public static boolean renameFile(File oldFile, String newName) {
        File newFile = new File(oldFile.getParent(), newName);
        return oldFile.renameTo(newFile);
    }
    
    public static long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }
    
    public static String getStorageInfo(String path) {
        try {
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            
            long totalSize = totalBlocks * blockSize;
            long freeSize = availableBlocks * blockSize;
            long usedSize = totalSize - freeSize;
            
            return String.format(Locale.getDefault(),
                "Total: %s\nUsed: %s\nFree: %s",
                formatFileSize(totalSize),
                formatFileSize(usedSize),
                formatFileSize(freeSize));
        } catch (Exception e) {
            return "Storage info unavailable";
        }
    }
    
    public static List<String> getExternalStoragePaths(Context context) {
        List<String> paths = new ArrayList<>();
        
        // Internal storage
        paths.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        
        // SD card and other external storage
        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        for (File file : externalFilesDirs) {
            if (file != null) {
                String path = file.getAbsolutePath();
                // Extract the root path
                int index = path.indexOf("/Android/data/");
                if (index > 0) {
                    paths.add(path.substring(0, index));
                }
            }
        }
        
        // Remove duplicates
        return new ArrayList<>(new java.util.HashSet<>(paths));
    }
    
    public static boolean isSdCardPath(String path) {
        return path.toLowerCase(Locale.getDefault()).contains("sd") ||
               path.toLowerCase(Locale.getDefault()).contains("external");
    }
}
