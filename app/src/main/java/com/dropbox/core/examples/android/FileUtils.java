package com.dropbox.core.examples.android;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FileUtils {

    /**
     * Lấy tên file từ Uri
     *
     * @param context Context để truy cập ContentResolver
     * @param uri     Uri của file (content:// hoặc file://)
     * @return Tên file (ví dụ: myfile.jpg) hoặc null nếu không tìm được
     */
    public static String getFileName(Context context, Uri uri) {
        String result = null;

        // Nếu Uri là dạng content:// thì query cột DISPLAY_NAME
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        // Nếu Uri là dạng file:// hoặc không tìm được tên thì lấy từ path
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }
}
