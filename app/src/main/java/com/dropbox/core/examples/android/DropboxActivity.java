package com.dropbox.core.examples.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public abstract class DropboxActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        String accessToken = AuthManager.getStoredAccessToken(this);

        if (accessToken == null) {
            // Nếu chưa login → kiểm tra kết quả sau khi login
            AuthManager.handleAuthResult(this);
            accessToken = AuthManager.getStoredAccessToken(this);
        }

        if (accessToken != null) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("DropboxSampleApp").build();
            DbxClientV2 client = new DbxClientV2(config, accessToken);
            DropboxClientFactory.init(client);

            onClientsReady();
        } else {
            // Nếu vẫn chưa có token → bắt đầu đăng nhập
            AuthManager.startOAuth(this, getString(R.string.app_key));
        }
    }

    protected abstract void onClientsReady();

    protected abstract void loadData();
}
