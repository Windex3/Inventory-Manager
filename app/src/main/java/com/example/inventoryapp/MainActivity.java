package com.example.inventoryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    //UI components
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;

    //Database helper
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Connect Java variables to the XML components
        usernameInput = findViewById(R.id.editTextText3);
        passwordInput = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.button);
        createAccountButton = findViewById(R.id.button2);

        //Gives access to the SQLite database
        databaseHelper = new DatabaseHelper(this);

        //Login button
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Please enter a username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean userExists = databaseHelper.checkUser(username, password);

            if (userExists) {
                Toast.makeText(this,
                        "Login Successful",
                        Toast.LENGTH_SHORT).show();

                //Connects MainActivity to InventoryActivity
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                //Changes the screen to InventoryActivity
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Create Account button
        createAccountButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Please enter a username and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean accountCreated = databaseHelper.addUser(username, password);

            if (accountCreated) {
                Toast.makeText(this,
                        "Account Created Successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Unable to create account, Username may already exist.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}