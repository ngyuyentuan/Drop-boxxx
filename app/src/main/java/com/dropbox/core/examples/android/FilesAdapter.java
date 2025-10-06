package com.dropbox.core.examples.android; // Sửa package nếu cần

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.examples.android.model.DropboxFile;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MetadataViewHolder> {
    private List<Metadata> mFiles;
    private final Picasso mPicasso;
    private final Callback mCallback;

    public interface Callback {
        void onFolderClicked(FolderMetadata folder);
        void onFileClicked(FileMetadata file);
    }

    public FilesAdapter(Picasso picasso, List<DropboxFile> callback) {
        this.mPicasso = picasso;
        this.mCallback = callback;
        this.mFiles = new ArrayList<>();
    }

    public void setFiles(List<Metadata> files) {
        mFiles = files;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MetadataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dropbox_file, parent, false); // Đảm bảo tên file layout đúng
        return new MetadataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetadataViewHolder holder, int position) {
        Metadata item = mFiles.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    // -- ViewHolder Class -- //
    public class MetadataViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView nameView;
        private final TextView sizeView;

        public MetadataViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.file_icon);
            nameView = itemView.findViewById(R.id.file_name);
            sizeView = itemView.findViewById(R.id.file_size);
        }

        public void bind(final Metadata item) {
            nameView.setText(item.getName());

            itemView.setOnClickListener(v -> {
                if (item instanceof FolderMetadata) {
                    mCallback.onFolderClicked((FolderMetadata) item);
                } else if (item instanceof FileMetadata) {
                    mCallback.onFileClicked((FileMetadata) item);
                }
            });

            if (item instanceof FileMetadata) {
                FileMetadata file = (FileMetadata) item;
                sizeView.setText(formatSize(file.getSize()));
                mPicasso.load(R.drawable.ic_file).into(iconView); // Thay thế bằng thumbnail nếu có
            } else if (item instanceof FolderMetadata) {
                sizeView.setText("Thư mục");
                mPicasso.load(R.drawable.ic_folder_blue_36dp).into(iconView);
            }
        }

        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String pre = "KMGTPE".charAt(exp-1) + "";
            return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }
    }
}