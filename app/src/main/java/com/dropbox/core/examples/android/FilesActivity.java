package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

/**
 * Activity để hiển thị và quản lý file của người dùng.
 */
public class FilesActivity extends DropboxActivity {

    private static final String TAG = FilesActivity.class.getName();

    /**
     * Phương thức tĩnh để tạo Intent khởi động Activity này một cách tường minh.
     * Đây là phương thức đã bị thiếu trước đó.
     * @param context Context của nơi gọi (ví dụ: UserActivity).
     * @param path Đường dẫn thư mục cần hiển thị.
     * @return Intent đã được cấu hình để khởi động FilesActivity.
     */
    public static Intent getIntent(Context context, String path) {
        Intent intent = new Intent(context, FilesActivity.class);
        // Gửi kèm đường dẫn thư mục qua Intent
        intent.putExtra("EXTRA_PATH", path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Định nghĩa lại (override) phương thức loadData() từ lớp cha DropboxActivity.
     * Đây là nơi để bắt đầu tải dữ liệu sau khi client được khởi tạo.
     */
    @Override
    protected void loadData() {
        // Kiểm tra xem client đã sẵn sàng chưa trước khi sử dụng
        if (DropboxClientFactory.getClient() != null) {
            // Client đã sẵn sàng, có thể bắt đầu tải dữ liệu
            Log.d(TAG, "Dropbox client đã sẵn sàng. Bắt đầu tải dữ liệu file...");
            Toast.makeText(this, "Client is ready! Fetching files...", Toast.LENGTH_SHORT).show();

            // Ví dụ: gọi phương thức để tải danh sách file
            // fetchFiles();
        } else {
            // Client chưa sẵn sàng, có thể do người dùng chưa đăng nhập
            Log.d(TAG, "Chưa thể tải dữ liệu, người dùng cần đăng nhập.");
        }
    }
}