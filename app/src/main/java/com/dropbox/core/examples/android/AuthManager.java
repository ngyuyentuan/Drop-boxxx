package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;

public class AuthManager {
    private static final String PREFS_NAME = "dropbox-sample";
    private static final String KEY_ACCESS_TOKEN = "access-token";

    /** Bắt đầu quá trình đăng nhập OAuth2 */
    public static void startOAuth(Context context, String appKey) {
        Auth.startOAuth2Authentication(context, appKey);
    }

    /** Lưu lại access token sau khi người dùng đăng nhập */
    public static void handleAuthResult(Context context) {
        String accessToken = Auth.getOAuth2Token();
        if (accessToken != null) {
            storeAccessToken(context, accessToken);

            // Tạo client Dropbox
            DbxRequestConfig config = DbxRequestConfig.newBuilder("DropboxSampleApp").build();
            DbxClientV2 client = new DbxClientV2(config, accessToken);
            DropboxClientFactory.init(client);

        }
    }

    private static void storeAccessToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static String getStoredAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public static boolean hasAccessToken(Context context) {
        return getStoredAccessToken(context) != null;
    }
    public static void logout(Context context) {
        DropboxClientFactory.clearClient();
        SharedPreferences prefs = context.getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE);
        prefs.edit().remove("access-token").apply();
    }

}
