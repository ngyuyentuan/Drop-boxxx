package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.dropbox.core.android.Auth;


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

    
    protected abstract void loadData();
}
