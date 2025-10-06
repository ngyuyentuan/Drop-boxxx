package com.dropbox.core.examples.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.examples.android.model.DropboxFile;
import java.util.*;

import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.FileInputStream;
import java.io.InputStream;

public class FilesActivity extends DropboxActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private FilesAdapter adapter;
    private List<DropboxFile> fileList = new ArrayList<>();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private static final int PICK_FILE_REQUEST = 100; // Mã request khi chọn file

    public static Intent newIntent(Context context) {
        return new Intent(context, FilesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilesAdapter(this, fileList);
        recyclerView.setAdapter(adapter);
        // --- Toolbar setup ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dropbox Client");

        // --- Drawer setup ---
        drawerLayout = findViewById(R.id.drawer_files_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // --- Header setup ---
        if (navigationView.getHeaderCount() > 0) {
            TextView nameView = navigationView.getHeaderView(0).findViewById(R.id.name_view);
            TextView emailView = navigationView.getHeaderView(0).findViewById(R.id.email_view);
            TextView typeView = navigationView.getHeaderView(0).findViewById(R.id.type_view);

            nameView.setText("Nguyễn Tuấn");
            emailView.setText("nguyennhutientuan@gmail.com");
            typeView.setText("BASIC");
        }

        // --- Floating button ---
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> openFilePicker());
    }

    // --- Mở cửa sổ chọn file ---
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    // --- Nhận kết quả chọn file ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadFileToDropbox(fileUri);
            }
        }
    }

    // --- Upload file lên Dropbox ---
    private void uploadFileToDropbox(Uri fileUri) {
        new Thread(() -> {
            try {
                // Mở stream đọc file
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(fileUri, "r");
                if (pfd == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show());
                    return;
                }

                InputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
                String fileName = getFileNameFromUri(fileUri);

                // Upload lên Dropbox
                FileMetadata metadata = DropboxClientFactory.getClient()
                        .files()
                        .uploadBuilder("/" + fileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);

                runOnUiThread(() -> Toast.makeText(this,
                        "Uploaded: " + metadata.getName(),
                        Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // --- Lấy tên file từ URI ---
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getLastPathSegment();
        if (path == null) return "unknown_file";
        int cut = path.lastIndexOf('/');
        return (cut != -1) ? path.substring(cut + 1) : path;
    }

    @Override
    protected void onClientsReady() {
        loadData();
    }

    @Override
    protected void loadData() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            try {
                DbxClientV2 client = DropboxClientFactory.getClient();
                ListFolderResult result = client.files().listFolder("");

                List<DropboxFile> fileList = new ArrayList<>();
                for (Metadata metadata : result.getEntries()) {
                    if (metadata instanceof FileMetadata) {
                        FileMetadata fileMeta = (FileMetadata) metadata;
                        fileList.add(new DropboxFile(
                                fileMeta.getName(),
                                fileMeta.getPathLower(),
                                fileMeta.getSize()
                        ));
                    }
                }

                runOnUiThread(() -> {
                    com.dropbox.core.examples.android.FilesAdapter adapter = new com.dropbox.core.examples.android.FilesAdapter(this, fileList);
                    recyclerView.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error loading files: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
        // TODO: Load files from Dropbox API để hiển thị danh sách
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_files) {
            Toast.makeText(this, "Files selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_notifications) {
            Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public List<DropboxFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<DropboxFile> fileList) {
        this.fileList = fileList;
    }
}
