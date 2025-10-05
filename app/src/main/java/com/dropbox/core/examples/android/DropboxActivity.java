package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.dropbox.core.android.Auth;

/**
 * Lớp Activity cơ sở để xử lý việc xác thực và khởi tạo Dropbox client.
 */
public abstract class DropboxActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);

        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                prefs.edit().putString("access-token", accessToken).apply();
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        loadData();
    }

    /**
     * Phương thức trừu tượng để tải dữ liệu cần thiết cho Activity.
     */
    protected abstract void loadData();
}