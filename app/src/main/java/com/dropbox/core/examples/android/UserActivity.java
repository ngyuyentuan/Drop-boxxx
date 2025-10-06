package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.dropbox.core.examples.android.internal.OpenWithActivity;
import com.dropbox.core.v2.users.FullAccount;

public class UserActivity extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v ->
                AuthManager.startOAuth(UserActivity.this, getString(R.string.app_key))
        );

        Button filesButton = findViewById(R.id.files_button);
        filesButton.setOnClickListener(v -> {
            Intent intent = FilesActivity.newIntent(UserActivity.this);
            startActivity(intent);
        });

        Button openWithButton = findViewById(R.id.open_with);
        openWithButton.setOnClickListener(v -> {
            Intent openIntent = new Intent(UserActivity.this, OpenWithActivity.class);
            startActivity(openIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AuthManager.hasAccessToken(this)) {
            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.files_button).setEnabled(true);
            findViewById(R.id.open_with).setEnabled(true);
            loadData();
        } else {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.files_button).setEnabled(false);
            findViewById(R.id.open_with).setEnabled(false);
        }
    }

    @Override
    protected void onClientsReady() {}

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                    ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
                    ((TextView) findViewById(R.id.type_text)).setText(result.getAccountType().name());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("UserActivity", "Failed to get account details.", e);
            }
        }).execute();
    }

    // ✅ Thêm menu để hiện lại trên toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            DropboxClientFactory.init(null);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            recreate();
            return true;
        }

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
