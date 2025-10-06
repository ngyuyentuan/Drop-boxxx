package com.dropbox.core.examples.android;

import com.dropbox.core.v2.DbxClientV2;

/**
 * Factory class để quản lý đối tượng Dropbox client duy nhất trong app.
 */
public class DropboxClientFactory {

    // Biến tĩnh giữ client Dropbox hiện tại
    private static DbxClientV2 sClient;

    /**
     * Khởi tạo client Dropbox sau khi người dùng đã đăng nhập thành công.
     */
    public static void init(DbxClientV2 client) {
        sClient = client;
    }

    /**
     * Lấy client Dropbox hiện tại.
     */
    public static DbxClientV2 getClient() {
        if (sClient == null) {
            throw new IllegalStateException("Dropbox client not initialized.");
        }
        return sClient;
    }

    /**
     * Xóa client Dropbox (dùng khi logout).
     */
    public static void clearClient() {
        sClient = null;
    }
}
