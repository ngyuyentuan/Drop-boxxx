package com.dropbox.core.examples.android;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.IOException;
import java.io.InputStream;


public class UploadFileTask extends AsyncTask<String, Void, FileMetadata> {

    private static final String TAG = "UploadFileTask";

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    /**
     * Callback cho kết quả upload
     */
    public interface Callback {
        void onUploadComplete(FileMetadata result);
        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        this.mContext = context.getApplicationContext();
        this.mDbxClient = dbxClient;
        this.mCallback = callback;
    }

    @Override
    protected FileMetadata doInBackground(String... params) {
        if (params.length < 2) {
            mException = new IllegalArgumentException("Need localUri and remoteFolderPath");
            return null;
        }

        String localUri = params[0];         // đường dẫn content://
        String remoteFolderPath = params[1]; // folder trên Dropbox

        try {
            Uri uri = Uri.parse(localUri);

            String remoteFileName = FileUtils.getFileName(mContext, uri);
            if (remoteFileName == null || remoteFileName.trim().isEmpty()) {
                remoteFileName = "upload_" + System.currentTimeMillis();
            }

            try (InputStream inputStream = mContext.getContentResolver().openInputStream(uri)) {
                if (inputStream == null) {
                    mException = new IOException("Cannot open input stream for: " + localUri);
                    return null;
                }

              
                return mDbxClient.files()
                        .uploadBuilder(remoteFolderPath + "/" + remoteFileName)
                        .withMode(WriteMode.OVERWRITE) // ghi đè nếu trùng
                        .uploadAndFinish(inputStream);
            }

        } catch (DbxException | IOException e) {
            Log.e(TAG, "Upload error", e);
            mException = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
        if (mCallback == null) return;

        if (mException != null) {
            mCallback.onError(mException);
        } else if (result != null) {
            mCallback.onUploadComplete(result);
        } else {
            mCallback.onError(new Exception("Upload failed: unknown reason"));
        }
    }
}
