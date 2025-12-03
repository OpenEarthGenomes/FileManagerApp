package com.filemanager;

import com.filemanager.models.FileItem;

public class FileTypeDetector {
    
    // Icon resources
    public static final int ICON_FOLDER = R.drawable.ic_folder;
    public static final int ICON_FILE = R.drawable.ic_file;
    public static final int ICON_IMAGE = R.drawable.ic_image;
    public static final int ICON_DOCUMENT = R.drawable.ic_document;
    public static final int ICON_AUDIO = R.drawable.ic_audio;
    public static final int ICON_VIDEO = R.drawable.ic_video;
    public static final int ICON_ARCHIVE = R.drawable.ic_archive;
    public static final int ICON_PDF = R.drawable.ic_pdf;
    public static final int ICON_CODE = R.drawable.ic_code;
    public static final int ICON_APK = R.drawable.ic_apk;
    
    // Color resources
    public static final int COLOR_FOLDER = R.color.folder_color;
    public static final int COLOR_IMAGE = R.color.image_color;
    public static final int COLOR_DOCUMENT = R.color.document_color;
    public static final int COLOR_AUDIO = R.color.audio_color;
    public static final int COLOR_VIDEO = R.color.video_color;
    public static final int COLOR_ARCHIVE = R.color.archive_color;
    public static final int COLOR_CODE = R.color.code_color;
    public static final int COLOR_DEFAULT = R.color.text_primary;
    
    // Extension backgrounds
    public static final int BG_EXTENSION_FOLDER = R.drawable.bg_extension_folder;
    public static final int BG_EXTENSION_IMAGE = R.drawable.bg_extension_image;
    public static final int BG_EXTENSION_DOCUMENT = R.drawable.bg_extension_document;
    public static final int BG_EXTENSION_AUDIO = R.drawable.bg_extension_audio;
    public static final int BG_EXTENSION_VIDEO = R.drawable.bg_extension_video;
    public static final int BG_EXTENSION_ARCHIVE = R.drawable.bg_extension_archive;
    public static final int BG_EXTENSION_CODE = R.drawable.bg_extension_code;
    public static final int BG_EXTENSION_DEFAULT = R.drawable.bg_extension_default;
    
    public static int getIconResource(FileItem fileItem) {
        if (fileItem.isDirectory()) {
            return ICON_FOLDER;
        }
        
        String extension = fileItem.getExtension().toLowerCase();
        
        // Image files
        if (isImageFile(extension)) {
            return ICON_IMAGE;
        }
        
        // Document files
        if (isDocumentFile(extension)) {
            if (extension.equals("pdf")) {
                return ICON_PDF;
            }
            return ICON_DOCUMENT;
        }
        
        // Audio files
        if (isAudioFile(extension)) {
            return ICON_AUDIO;
        }
        
        // Video files
        if (isVideoFile(extension)) {
            return ICON_VIDEO;
        }
        
        // Archive files
        if (isArchiveFile(extension)) {
            return ICON_ARCHIVE;
        }
        
        // Code files
        if (isCodeFile(extension)) {
            return ICON_CODE;
        }
        
        // APK files
        if (extension.equals("apk")) {
            return ICON_APK;
        }
        
        return ICON_FILE;
    }
    
    public static int getIconColor(FileItem fileItem) {
        if (fileItem.isDirectory()) {
            return COLOR_FOLDER;
        }
        
        String extension = fileItem.getExtension().toLowerCase();
        
        if (isImageFile(extension)) {
            return COLOR_IMAGE;
        }
        
        if (isDocumentFile(extension)) {
            return COLOR_DOCUMENT;
        }
        
        if (isAudioFile(extension)) {
            return COLOR_AUDIO;
        }
        
        if (isVideoFile(extension)) {
            return COLOR_VIDEO;
        }
        
        if (isArchiveFile(extension)) {
            return COLOR_ARCHIVE;
        }
        
        if (isCodeFile(extension)) {
            return COLOR_CODE;
        }
        
        return COLOR_DEFAULT;
    }
    
    public static int getExtensionBackground(String extension) {
        if (extension == null || extension.isEmpty()) {
            return BG_EXTENSION_DEFAULT;
        }
        
        String ext = extension.toLowerCase();
        
        if (isImageFile(ext)) {
            return BG_EXTENSION_IMAGE;
        }
        
        if (isDocumentFile(ext)) {
            return BG_EXTENSION_DOCUMENT;
        }
        
        if (isAudioFile(ext)) {
            return BG_EXTENSION_AUDIO;
        }
        
        if (isVideoFile(ext)) {
            return BG_EXTENSION_VIDEO;
        }
        
        if (isArchiveFile(ext)) {
            return BG_EXTENSION_ARCHIVE;
        }
        
        if (isCodeFile(ext)) {
            return BG_EXTENSION_CODE;
        }
        
        return BG_EXTENSION_DEFAULT;
    }
    
    public static String getMimeType(String extension) {
        if (extension == null) return null;
        
        switch (extension.toLowerCase()) {
            // Images
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
                
            // Documents
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "txt":
                return "text/plain";
                
            // Audio
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "ogg":
                return "audio/ogg";
            case "m4a":
                return "audio/mp4";
                
            // Video
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mkv":
                return "video/x-matroska";
            case "mov":
                return "video/quicktime";
                
            // Archives
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            case "tar":
                return "application/x-tar";
                
            default:
                return null;
        }
    }
    
    private static boolean isImageFile(String extension) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg"};
        return contains(extension, imageExtensions);
    }
    
    private static boolean isDocumentFile(String extension) {
        String[] docExtensions = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", 
                                  "rtf", "odt", "ods", "odp", "md", "csv"};
        return contains(extension, docExtensions);
    }
    
    private static boolean isAudioFile(String extension) {
        String[] audioExtensions = {"mp3", "wav", "ogg", "m4a", "flac", "aac", "wma", "mid", "midi"};
        return contains(extension, audioExtensions);
    }
    
    private static boolean isVideoFile(String extension) {
        String[] videoExtensions = {"mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp"};
        return contains(extension, videoExtensions);
    }
    
    private static boolean isArchiveFile(String extension) {
        String[] archiveExtensions = {"zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso", "jar"};
        return contains(extension, archiveExtensions);
    }
    
    private static boolean isCodeFile(String extension) {
        String[] codeExtensions = {"java", "kt", "xml", "html", "htm", "css", "js", "cpp", "c", "h", 
                                   "py", "php", "json", "sql", "sh", "bat", "gradle", "yml", "yaml"};
        return contains(extension, codeExtensions);
    }
    
    private static boolean contains(String extension, String[] extensions) {
        for (String ext : extensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
