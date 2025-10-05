//package com.dropbox.core.examples.android;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import com.dropbox.core.android.Auth;
//import com.dropbox.core.examples.android.internal.OpenWithActivity;
//import com.dropbox.core.v2.users.FullAccount;
//
///**
// * Activity that shows information about the currently logged-in Dropbox user.
// */
//public class UserActivity extends DropboxActivity {
//
//    private static final String PREFS_NAME = "dropbox-sample";
//    private static final String KEY_CREDENTIAL = "dbx-credential";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user);
//
//        Toolbar toolbar = findViewById(R.id.app_bar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(R.string.app_name);
//        }
//
//        Button loginButton = findViewById(R.id.login_button);
//        loginButton.setOnClickListener(v -> {
//            // Dùng OAuth2 PKCE flow (chuẩn mới)
//            Auth.startOAuth2PKCE(
//                    UserActivity.this,
//                    getString(R.string.app_key),
//                    null,
//                    null,
//                    null,
//                    null
//            );
//        });
//
//        Button filesButton = findViewById(R.id.files_button);
//        filesButton.setOnClickListener(v ->
//                startActivity(FilesActivity.getIntent(UserActivity.this, "", true))
//        );
//
//        Button openWithButton = findViewById(R.id.open_with);
//        openWithButton.setOnClickListener(v -> {
//            Intent openWithIntent = new Intent(UserActivity.this, OpenWithActivity.class);
//            startActivity(openWithIntent);
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (hasToken()) {
//            findViewById(R.id.login_button).setVisibility(View.GONE);
//            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
//            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
//            findViewById(R.id.type_text).setVisibility(View.VISIBLE);
//            findViewById(R.id.files_button).setEnabled(true);
//            findViewById(R.id.open_with).setEnabled(true);
//            loadData();
//        } else {
//            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.email_text).setVisibility(View.GONE);
//            findViewById(R.id.name_text).setVisibility(View.GONE);
//            findViewById(R.id.type_text).setVisibility(View.GONE);
//            findViewById(R.id.files_button).setEnabled(false);
//            findViewById(R.id.open_with).setEnabled(false);
//        }
//    }
//
//    @Override
//    protected void loadData() {
//        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
//            @Override
//            public void onComplete(FullAccount result) {
//                if (result != null) {
//                    ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
//                    ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
//                    ((TextView) findViewById(R.id.type_text)).setText(result.getAccountType().name());
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e(getClass().getName(), "Failed to get account details.", e);
//            }
//        }).execute();
//    }
//
//    /**
//     * Kiểm tra xem người dùng đã lưu thông tin xác thực Dropbox chưa.
//     */
//    private boolean hasToken() {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        String serializedCred = prefs.getString(KEY_CREDENTIAL, null);
//        return serializedCred != null;
//    }
//}
