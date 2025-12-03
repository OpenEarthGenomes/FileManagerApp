package com.filemanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.filemanager.adapters.FileListAdapter;
import com.filemanager.models.FileItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileBrowserActivity extends AppCompatActivity implements 
        FileListAdapter.OnFileClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    
    private static final String TAG = "FileBrowserActivity";
    
    // UI Components
    private Toolbar toolbar;
    private TextView tvCurrentPath;
    private TextView tvStorageInfo;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout bottomMenu;
    private ImageButton btnBack, btnHome, btnSelect, btnMenu;
    private LinearLayout operationMenu;
    private ImageButton btnCopy, btnMove, btnDelete, btnRename, btnProperties;
    
    // Data
    private FileListAdapter fileListAdapter;
    private List<FileItem> currentFileList = new ArrayList<>();
    private String currentPath;
    private boolean showHiddenFiles = false;
    private int sortType = 0; // 0: Name, 1: Size, 2: Date, 3: Type
    
    // Threading
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // Selection mode
    private boolean isSelectionMode = false;
    private List<FileItem> selectedItems = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupBottomMenu();
        setupOperationMenu();
        setupListeners();
        
        // Get initial path from intent
        String pathType = getIntent().getStringExtra("PATH_TYPE");
        if (pathType != null) {
            switch (pathType) {
                case FileUtils.PATH_INTERNAL_STORAGE:
                    currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    break;
                case FileUtils.PATH_EXTERNAL_STORAGE:
                    List<String> externalPaths = FileUtils.getExternalStoragePaths(this);
                    if (externalPaths.size() > 1) {
                        currentPath = externalPaths.get(1); // Assume second is SD card
                    } else {
                        currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    }
                    break;
                case FileUtils.PATH_RECENT_FILES:
                    // For recent files, we'll show a special view
                    currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    break;
                default:
                    currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        } else {
            currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        
        loadDirectory(currentPath);
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCurrentPath = findViewById(R.id.tv_current_path);
        tvStorageInfo = findViewById(R.id.tv_storage_info);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        progressBar = findViewById(R.id.progress_bar);
        bottomMenu = findViewById(R.id.bottom_menu);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnSelect = findViewById(R.id.btn_select);
        btnMenu = findViewById(R.id.btn_menu);
        operationMenu = findViewById(R.id.operation_menu);
        btnCopy = findViewById(R.id.btn_copy);
        btnMove = findViewById(R.id.btn_move);
        btnDelete = findViewById(R.id.btn_delete);
        btnRename = findViewById(R.id.btn_rename);
        btnProperties = findViewById(R.id.btn_properties);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileListAdapter = new FileListAdapter(this, currentFileList, this);
        recyclerView.setAdapter(fileListAdapter);
        
        // Add item decoration for borders
        recyclerView.addItemDecoration(new FileItemDecoration(this));
    }
    
    private void setupBottomMenu() {
        // Set rounded corners programmatically
        bottomMenu.setBackgroundResource(R.drawable.rounded_menu_bottom);
    }
    
    private void setupOperationMenu() {
        operationMenu.setVisibility(View.GONE);
        operationMenu.setBackgroundResource(R.drawable.rounded_menu_operation);
    }
    
    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
        
        btnBack.setOnClickListener(v -> navigateUp());
        btnHome.setOnClickListener(v -> navigateToHome());
        btnSelect.setOnClickListener(v -> toggleSelectionMode());
        btnMenu.setOnClickListener(v -> toggleOperationMenu());
        
        // Operation buttons
        btnCopy.setOnClickListener(v -> copySelectedFiles());
        btnMove.setOnClickListener(v -> moveSelectedFiles());
        btnDelete.setOnClickListener(v -> deleteSelectedFiles());
        btnRename.setOnClickListener(v -> renameSelectedFile());
        btnProperties.setOnClickListener(v -> showProperties());
    }
    
    @SuppressLint("SetTextI18n")
    private void loadDirectory(String path) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        
        executorService.execute(() -> {
            List<FileItem> files = FileUtils.getFilesInDirectory(path);
            
            // Apply sorting
            sortFileList(files);
            
            // Update storage info
            String storageInfo = FileUtils.getStorageInfo(path);
            
            mainHandler.post(() -> {
                currentPath = path;
                currentFileList.clear();
                currentFileList.addAll(files);
                
                tvCurrentPath.setText(path);
                tvStorageInfo.setText(storageInfo);
                
                fileListAdapter.notifyDataSetChanged();
                
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                
                updateToolbarTitle();
            });
        });
    }
    
    private void sortFileList(List<FileItem> files) {
        switch (sortType) {
            case 0: // Name
                Collections.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() != f2.isDirectory()) {
                        return f1.isDirectory() ? -1 : 1;
                    }
                    return f1.getName().compareToIgnoreCase(f2.getName());
                });
                break;
                
            case 1: // Size
                Collections.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() != f2.isDirectory()) {
                        return f1.isDirectory() ? -1 : 1;
                    }
                    return Long.compare(f2.getSize(), f1.getSize());
                });
                break;
                
            case 2: // Date
                Collections.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() != f2.isDirectory()) {
                        return f1.isDirectory() ? -1 : 1;
                    }
                    return Long.compare(f2.getLastModified(), f1.getLastModified());
                });
                break;
                
            case 3: // Type
                Collections.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() != f2.isDirectory()) {
                        return f1.isDirectory() ? -1 : 1;
                    }
                    return f1.getExtension().compareToIgnoreCase(f2.getExtension());
                });
                break;
        }
    }
    
    private void updateToolbarTitle() {
        File currentDir = new File(currentPath);
        String title = currentDir.getName();
        if (title.isEmpty()) {
            title = currentDir.getPath();
        }
        toolbar.setTitle(title);
    }
    
    private void navigateUp() {
        File currentDir = new File(currentPath);
        File parentDir = currentDir.getParentFile();
        
        if (parentDir != null && parentDir.exists()) {
            loadDirectory(parentDir.getAbsolutePath());
        } else {
            finish();
        }
    }
    
    private void navigateToHome() {
        String homePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!homePath.equals(currentPath)) {
            loadDirectory(homePath);
        }
    }
    
    private void toggleSelectionMode() {
        isSelectionMode = !isSelectionMode;
        
        if (isSelectionMode) {
            btnSelect.setImageResource(R.drawable.ic_deselect);
            fileListAdapter.setSelectionMode(true);
            operationMenu.setVisibility(View.VISIBLE);
        } else {
            btnSelect.setImageResource(R.drawable.ic_select);
            fileListAdapter.setSelectionMode(false);
            selectedItems.clear();
            operationMenu.setVisibility(View.GONE);
        }
        
        fileListAdapter.notifyDataSetChanged();
    }
    
    private void toggleOperationMenu() {
        if (operationMenu.getVisibility() == View.VISIBLE) {
            operationMenu.setVisibility(View.GONE);
        } else {
            operationMenu.setVisibility(View.VISIBLE);
        }
    }
    
    // File operation methods
    private void copySelectedFiles() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show destination picker dialog
        // Implementation would go here
        Toast.makeText(this, "Copy " + selectedItems.size() + " files", Toast.LENGTH_SHORT).show();
    }
    
    private void moveSelectedFiles() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "Move " + selectedItems.size() + " files", Toast.LENGTH_SHORT).show();
    }
    
    private void deleteSelectedFiles() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        // Implementation would go here
        Toast.makeText(this, "Delete " + selectedItems.size() + " files", Toast.LENGTH_SHORT).show();
    }
    
    private void renameSelectedFile() {
        if (selectedItems.size() != 1) {
            Toast.makeText(this, "Select exactly one file to rename", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show rename dialog
        // Implementation would go here
        Toast.makeText(this, "Rename file", Toast.LENGTH_SHORT).show();
    }
    
    private void showProperties() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show properties dialog
        // Implementation would go here
        Toast.makeText(this, "Show properties", Toast.LENGTH_SHORT).show();
    }
    
    // File click listeners
    @Override
    public void onFileClick(FileItem fileItem, int position) {
        if (isSelectionMode) {
            // Toggle selection
            if (selectedItems.contains(fileItem)) {
                selectedItems.remove(fileItem);
            } else {
                selectedItems.add(fileItem);
            }
            fileListAdapter.notifyItemChanged(position);
        } else {
            // Open file or directory
            if (fileItem.isDirectory()) {
                loadDirectory(fileItem.getPath());
            } else {
                openFile(fileItem);
            }
        }
    }
    
    @Override
    public void onFileLongClick(FileItem fileItem, int position) {
        if (!isSelectionMode) {
            toggleSelectionMode();
            selectedItems.add(fileItem);
            fileListAdapter.notifyItemChanged(position);
        }
    }
    
    private void openFile(FileItem fileItem) {
        // Use Android's default intent to open file
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(fileItem.getPath());
        
        // Get MIME type
        String mimeType = FileTypeDetector.getMimeType(fileItem.getExtension());
        if (mimeType == null) {
            mimeType = "*/*";
        }
        
        intent.setDataAndType(android.net.Uri.fromFile(file), mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No application found to open this file", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            navigateUp();
            return true;
        } else if (id == R.id.menu_new_folder) {
            createNewFolder();
            return true;
        } else if (id == R.id.menu_refresh) {
            loadDirectory(currentPath);
            return true;
        } else if (id == R.id.menu_sort) {
            showSortDialog();
            return true;
        } else if (id == R.id.menu_view) {
            toggleViewMode();
            return true;
        } else if (id == R.id.menu_show_hidden) {
            toggleHiddenFiles();
            return true;
        } else if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void createNewFolder() {
        // Implementation for creating new folder
        Toast.makeText(this, "Create new folder", Toast.LENGTH_SHORT).show();
    }
    
    private void showSortDialog() {
        // Implementation for sort dialog
        String[] sortOptions = {"Name", "Size", "Date", "Type"};
        // Would typically show a dialog or bottom sheet
        Toast.makeText(this, "Sort by dialog", Toast.LENGTH_SHORT).show();
    }
    
    private void toggleViewMode() {
        // Toggle between list and grid view
        Toast.makeText(this, "Toggle view mode", Toast.LENGTH_SHORT).show();
    }
    
    private void toggleHiddenFiles() {
        showHiddenFiles = !showHiddenFiles;
        loadDirectory(currentPath);
        Toast.makeText(this, 
            showHiddenFiles ? "Showing hidden files" : "Hiding hidden files", 
            Toast.LENGTH_SHORT).show();
    }
    
    private void showAboutDialog() {
        // Show about dialog
        Toast.makeText(this, "File Manager Pro v1.0", Toast.LENGTH_SHORT).show();
    }
    
    // Swipe refresh
    @Override
    public void onRefresh() {
        loadDirectory(currentPath);
    }
    
    // Update selected items
    public void updateSelectedItems(List<FileItem> items) {
        this.selectedItems = items;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
