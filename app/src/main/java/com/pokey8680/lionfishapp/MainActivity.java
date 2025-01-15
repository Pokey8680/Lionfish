package com.pokey8680.lionfishapp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText filePathEditText;
    private Button selectFileButton, startUpdateButton;
    private AlertDialog outputLogDialog;
    private TextView logTextView;

    private static final int FILE_PICKER_REQUEST_CODE = 1; // Request code for file picker
    private static final int REQUEST_CODE_MANAGE_STORAGE = 2; // Request code for managing storage permission

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePathEditText = findViewById(R.id.file_path_edit_text);
        selectFileButton = findViewById(R.id.select_file_button);
        startUpdateButton = findViewById(R.id.start_update_button);

        selectFileButton.setOnClickListener(v -> openFilePicker());
        startUpdateButton.setOnClickListener(v -> startUpdateProcess());
    }



    private void openFilePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Open the folder picker for Android 11 and above
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        } else {
            // Open the file picker for Android versions below 11
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // Can be customized for specific file types
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        }
    }



    // Start the update process with the selected file
    private void startUpdateProcess() {
        String filePath = filePathEditText.getText().toString();
        if (filePath.isEmpty() || !filePath.endsWith(".img")) {
            showErrorDialog("Please select a valid .img file before starting the update.");
            return;
        }

        showOutputLogDialog();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> new UpdateTask(filePath).execute());
    }

    private void showOutputLogDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_output_log, null);

        logTextView = dialogView.findViewById(R.id.log_text_view);
        Button closeDialogButton = dialogView.findViewById(R.id.close_dialog_button);

        closeDialogButton.setOnClickListener(v -> outputLogDialog.dismiss());

        outputLogDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        outputLogDialog.show();
    }

    private void appendLog(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (logTextView != null) {
                logTextView.append(message + "\n");
                // Auto-scroll to the bottom
                ScrollView parentScrollView = (ScrollView) logTextView.getParent();
                parentScrollView.post(() -> parentScrollView.fullScroll(View.FOCUS_DOWN));
            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private class UpdateTask {
        private final String filePath;

        public UpdateTask(String filePath) {
            this.filePath = filePath;
        }

        public void execute() {
            try {
                appendLog("Starting update process...");
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());

                appendLog("Starting phh-ota-make...");
                outputStream.writeBytes("setprop ctl.start phh-ota-make\n");
                outputStream.flush();
                Thread.sleep(2000);

                // Handle copying the file
                appendLog("Copying file to /dev/phh-ota...");
                String targetPath = "/dev/phh-ota";
                if (copyFile(Uri.parse(filePath), targetPath)) {  // Ensure filePath is parsed as a URI
                    appendLog("File copied successfully.");
                } else {
                    appendLog("Failed to copy the file.");
                }


                appendLog("Starting phh-ota-switch...");
                outputStream.writeBytes("setprop ctl.start phh-ota-switch\n");
                outputStream.flush();
                Thread.sleep(2000);

                appendLog("Finishing process...");
                outputStream.writeBytes("exit\n");
                outputStream.flush();
                outputStream.close();

                process.waitFor();
                appendLog("Update process completed successfully.");
            } catch (Exception e) {
                appendLog("Error: " + e.getMessage());
            }
        }
    }

    // Copy the selected file to the destination path
    private boolean copyFile(Uri sourceUri, String destFilePath) {
        if (sourceUri == null) {
            appendLog("Source URI is null.");
            return false;
        }

        try {
            // Open InputStream using ContentResolver
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) {
                appendLog("Failed to open input stream.");
                return false;
            }

            // Temporary file path to store the copied data
            File tempFile = new File(getCacheDir(), "temp_file.img");
            OutputStream tempOut = new FileOutputStream(tempFile);

            // Write to the temporary file
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                tempOut.write(buffer, 0, length);
            }

            inputStream.close();
            tempOut.close();

            // Use root permissions to move the file to the destination
            String command = "su -c 'cp " + tempFile.getAbsolutePath() + " " + destFilePath + "'";
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            tempFile.delete();

            if (exitCode == 0) {
                appendLog("File copied successfully.");
                return true;
            } else {
                appendLog("Root command failed with exit code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            appendLog("Error copying file: " + e.getMessage());
            return false;
        }
    }




    // Handle the result from the file picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // If a folder was selected (for Android 11 and higher), open file picker inside that folder
                    Uri folderUri = selectedUri;
                    openFilePickerInsideFolder(folderUri);
                } else {
                    // For pre-Android 11 devices, it directly picks the file
                    String filePath = getFilePath(selectedUri);
                    filePathEditText.setText(filePath);
                }
            }
        }
    }

    // This method opens the file picker inside the selected folder (Android 11+)
    private void openFilePickerInsideFolder(Uri folderUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // You can specify the MIME type of files you want to allow
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, folderUri); // Set initial folder to the selected one
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    // Helper method to get the file path from the URI
    private String getFilePath(Uri uri) {
        String path = null;
        if ("content".equals(uri.getScheme())) {
            // Accessing content URIs (new Android versions)
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        } else if ("file".equals(uri.getScheme())) {
            // Accessing file URIs (older Android versions)
            path = new File(uri.getPath()).getAbsolutePath();
        }
        return path;
    }


    // Helper method to get the file name from the URI
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }


    // Helper method to extract the file name from the URI
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;

        // Check if the URI is content:// (e.g., document providers)
        if (uri.getScheme().equals("content")) {
            ContentResolver resolver = getContentResolver();
            try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }

        // If URI is of file type, just extract the file name from the path
        if (fileName == null && uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }

        return fileName;
    }

}
