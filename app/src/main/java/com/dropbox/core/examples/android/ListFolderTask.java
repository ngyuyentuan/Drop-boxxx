package com.dropbox.core.examples.android;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

/**
 * Task để lấy danh sách file/thư mục từ một đường dẫn trên Dropbox.
 */
public class ListFolderTask extends AsyncTask<String, Void, ListFolderResult> {
    private static final String TAG = ListFolderTask.class.getSimpleName();

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    /**
     * Interface để trả kết quả về cho Activity.
     */
    public interface Callback {
        void onDataLoaded(ListFolderResult result);
        void onError(Exception e);
    }

    public ListFolderTask(DbxClientV2 dbxClient, Callback callback) {
        this.mDbxClient = dbxClient;
        this.mCallback = callback;
    }

    // Chạy trên luồng nền (Background Thread)
    @Override
    protected ListFolderResult doInBackground(String... params) {
        String path = params[0];
        try {
            // LỆNH GỌI MẠNG NẰM Ở ĐÂY
            return mDbxClient.files().listFolder(path);
        } catch (DbxException e) {
            Log.e(TAG, "Failed to list folder.", e);
            mException = e;
        }
        return null;
    }

    // Chạy trên luồng chính (UI Thread) sau khi doInBackground hoàn thành
    @Override
    protected void onPostExecute(@Nullable ListFolderResult result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(new Exception("Failed to get folder list."));
        } else {
            mCallback.onDataLoaded(result);
        }
    }
}