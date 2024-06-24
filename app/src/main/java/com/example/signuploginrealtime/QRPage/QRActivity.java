package com.example.signuploginrealtime.QRPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.signuploginrealtime.R;
import com.google.firebase.FirebaseApp;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRActivity extends AppCompatActivity {

    private TextView textEditText;
    private Spinner plainSpinner;
    private TextView dateEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;


    private void showInputTextPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = getLayoutInflater().inflate(R.layout.input_text_popup, null);
        builder.setView(popupView);
        AlertDialog alertDialog = builder.create();

        EditText inputText = popupView.findViewById(R.id.inputText);
        Button saveInputButton = popupView.findViewById(R.id.saveInputButton);

        saveInputButton.setOnClickListener(v -> {
            String additionalInfo = inputText.getText().toString();
            // Mengatur hasil input ke resultTextView
            TextView resultTextView = findViewById(R.id.resultTextView);
            resultTextView.setText("Description : " + additionalInfo);

            alertDialog.dismiss();
        });

        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // Initialize Firebase (Add this line before using any Firebase features)
        FirebaseApp.initializeApp(this);

        textEditText = findViewById(R.id.textTextView);
        plainSpinner = findViewById(R.id.plainSpinner);
        dateEditText = findViewById(R.id.dateTextView);
        saveButton = findViewById(R.id.btnsave);
        databaseHelper = new DatabaseHelper(this); // Gunakan constructor dengan parameter Context

        Button scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(v -> {
            // Inisialisasi IntentIntegrator
            IntentIntegrator integrator = new IntentIntegrator(QRActivity.this);
            // Set tipe barcode yang ingin discan (QR Code)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            // Set pesan yang akan ditampilkan saat menunggu scan
            integrator.setPrompt("Scan QR Code");
            // Jalankan proses scanning
            integrator.initiateScan();
        });

        // Daftar pilihan untuk combo box (Spinner)
        String[] options = {"✓", "✗", "∅"};

        // Buat adapter untuk combo box (Spinner)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter ke combo box (Spinner)
        plainSpinner.setAdapter(adapter);

        saveButton.setOnClickListener(v -> saveData());


        plainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = plainSpinner.getSelectedItem().toString();
                if (selectedItem.equals("✗") || selectedItem.equals("∅")) {
                    // Tampilkan popup input teks
                    showInputTextPopup();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nothing to do here
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrData = result.getContents();
                textEditText.setText(qrData);

                // Tampilkan tanggal
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());
                dateEditText.setText(currentDate);
            }
        }
    }

    private void saveData() {
        String text = textEditText.getText().toString();
        String plainText = plainSpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString();

        // Check if any of the fields is empty
        if (text.isEmpty() || date.isEmpty()) {
            // Show an alert indicating that fields must be filled
            showAlertDialog("Error", "Please fill all required fields before saving.");
        } else {
            // Data is complete, proceed to save
            databaseHelper.insertData(text, plainText, date);
            showToast("Data berhasil disimpan");
            // Memanggil method untuk memunculkan data baru di RekapActivity

        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}