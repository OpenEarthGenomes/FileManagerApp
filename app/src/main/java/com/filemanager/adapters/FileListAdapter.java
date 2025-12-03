package com.filemanager.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.filemanager.FileTypeDetector;
import com.filemanager.FileUtils;
import com.filemanager.R;
import com.filemanager.models.FileItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    
    public interface OnFileClickListener {
        void onFileClick(FileItem fileItem, int position);
        void onFileLongClick(FileItem fileItem, int position);
    }
    
    private Context context;
    private List<FileItem> fileList;
    private OnFileClickListener listener;
    private boolean selectionMode = false;
    private SimpleDateFormat dateFormat;
    
    public FileListAdapter(Context context, List<FileItem> fileList, OnFileClickListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }
    
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }
    
    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem fileItem = fileList.get(position);
        holder.bind(fileItem, position);
    }
    
    @Override
    public int getItemCount() {
        return fileList.size();
    }
    
    class FileViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private ImageView ivIcon;
        private ImageView ivHidden;
        private TextView tvName;
        private TextView tvSize;
        private TextView tvDate;
        private TextView tvExtension;
        private CheckBox cbSelect;
        private View divider;
        
        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            ivHidden = itemView.findViewById(R.id.iv_hidden);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvExtension = itemView.findViewById(R.id.tv_extension);
            cbSelect = itemView.findViewById(R.id.cb_select);
            divider = itemView.findViewById(R.id.divider);
        }
        
        void bind(FileItem fileItem, int position) {
            // Set file name
            tvName.setText(fileItem.getName());
            
            // Set file size
            if (fileItem.isDirectory()) {
                tvSize.setText("Folder");
            } else {
                String sizeText = FileUtils.formatFileSize(fileItem.getSize());
                tvSize.setText(sizeText);
            }
            
            // Set date
            String dateText = dateFormat.format(new Date(fileItem.getLastModified()));
            tvDate.setText(dateText);
            
            // Set extension
            if (fileItem.isDirectory()) {
                tvExtension.setText("DIR");
                tvExtension.setBackgroundResource(R.drawable.bg_extension_folder);
            } else {
                String ext = fileItem.getExtension().toUpperCase();
                if (ext.length() > 4) ext = ext.substring(0, 4);
                tvExtension.setText(ext);
                
                // Set extension background based on file type
                int bgResource = FileTypeDetector.getExtensionBackground(fileItem.getExtension());
                tvExtension.setBackgroundResource(bgResource);
            }
            
            // Set icon
            int iconResource = FileTypeDetector.getIconResource(fileItem);
            ivIcon.setImageResource(iconResource);
            
            // Set icon color based on file type
            int iconColor = FileTypeDetector.getIconColor(fileItem);
            ivIcon.setColorFilter(ContextCompat.getColor(context, iconColor));
            
            // Show/hide hidden indicator
            if (fileItem.isHidden()) {
                ivHidden.setVisibility(View.VISIBLE);
                container.setAlpha(0.7f);
            } else {
                ivHidden.setVisibility(View.GONE);
                container.setAlpha(1.0f);
            }
            
            // Selection mode
            if (selectionMode) {
                cbSelect.setVisibility(View.VISIBLE);
                cbSelect.setChecked(false); // Reset state
            } else {
                cbSelect.setVisibility(View.GONE);
            }
            
            // Set border
            container.setBackgroundResource(R.drawable.bg_file_item);
            
            // Set click listeners
            container.setOnClickListener(v -> {
                if (selectionMode) {
                    cbSelect.setChecked(!cbSelect.isChecked());
                    listener.onFileClick(fileItem, position);
                } else {
                    listener.onFileClick(fileItem, position);
                }
            });
            
            container.setOnLongClickListener(v -> {
                listener.onFileLongClick(fileItem, position);
                return true;
            });
            
            // Divider visibility
            if (position == fileList.size() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public void updateData(List<FileItem> newFileList) {
        fileList.clear();
        fileList.addAll(newFileList);
        notifyDataSetChanged();
    }
}
