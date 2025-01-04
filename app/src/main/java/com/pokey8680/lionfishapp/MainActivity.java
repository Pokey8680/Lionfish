package com.pokey8680.lionfishapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private EditText filePathEditText;
    private Button selectFileButton, startUpdateButton;
    private AlertDialog progressDialog;  // Declare the progress dialog

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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*"); // Accept all file types
        String[] mimeTypes = {"application/octet-stream"}; // Focus on binary files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Ensure file is openable
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        handleSelectedFile(uri);
                    }
                }
            }
    );

    private void handleSelectedFile(Uri uri) {
        try {
            // Get the file name (optional, just for display)
            String fileName = getFileNameFromUri(uri);

            if (fileName.endsWith(".img")) {
                filePathEditText.setText(fileName);

                // If you need to process the file, open it as an InputStream
                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    // Process the file as needed
                }
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Please select a valid .img file.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setMessage("Failed to handle the selected file.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String fileName = "";
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        return fileName;
    }

    private void startUpdateProcess() {
        String filePath = filePathEditText.getText().toString();
        if (filePath.isEmpty() || !filePath.endsWith(".img")) {
            new AlertDialog.Builder(this)
                    .setMessage("Please select a valid .img file before starting the update.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Show the "Please wait..." dialog on the main thread
        progressDialog = new AlertDialog.Builder(this)
                .setMessage("Please wait until the update is finished.")
                .setCancelable(false)  // Disable dismissing the dialog
                .create();
        progressDialog.show();

        // Run the update process in a background thread
        new UpdateTask(filePath).execute();
    }

    private class UpdateTask extends AsyncTask<Void, Void, String> {
        private final String filePath;

        public UpdateTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Request root access and execute commands
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());

                // Start phh-ota-make
                outputStream.writeBytes("setprop ctl.start phh-ota-make\n");
                outputStream.flush();

                // Wait for the process to initialize
                Thread.sleep(10000);

                // Copy the .img file
                outputStream.writeBytes(String.format("cp %s /dev/phh-ota\n", filePath));
                outputStream.flush();

                // Start phh-ota-switch
                outputStream.writeBytes("setprop ctl.start phh-ota-switch\n");
                outputStream.flush();

                // Wait for the OTA process to finalize
                Thread.sleep(10000);

                // Close the output stream
                outputStream.writeBytes("exit\n");
                outputStream.flush();
                outputStream.close();
                process.waitFor();

                // Return success message
                return "Done! You can reboot to apply the update.";
            } catch (Exception e) {
                e.printStackTrace();
                // Return error message
                return "An error occurred during the update process. Please check the logs.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Dismiss the progress dialog
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // Show the result dialog on the main thread
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(result)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
