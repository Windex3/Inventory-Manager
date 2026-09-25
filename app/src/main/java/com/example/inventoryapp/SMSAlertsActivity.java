package com.example.inventoryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SMSAlertsActivity extends AppCompatActivity {
    //UI components
    private Button enableSMSButton;
    private Button continueButton;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private EditText phoneNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_alerts);

        //Connects to UI components
        enableSMSButton = findViewById(R.id.button8);
        continueButton = findViewById(R.id.button4);
        phoneNumberInput = findViewById(R.id.editTextPhone);

        //Handles the result of the SMS permission request
        requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                Toast.makeText(this,
                                        "SMS alerts enabled",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this,
                                        "SMS permission denied",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                );

        //Request permission when the user chooses to enable SMS alerts
        enableSMSButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this,
                        "Please enter a phone number",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences preferences = getSharedPreferences("SMSSettings", MODE_PRIVATE);
            preferences.edit().putString("phoneNumber", phoneNumber).apply();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "SMS alerts are already enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
            }
        });

        //Continue using the app without SMS
        continueButton.setOnClickListener(v -> finish());
    }
}
