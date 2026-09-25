package com.example.inventoryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity{
    //UI components
    private EditText itemNameInput;
    private EditText quantityInput;
    private Button cancelButton;
    private Button saveButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Connect Java variables to XML components
        itemNameInput = findViewById(R.id.textView11);
        quantityInput = findViewById(R.id.textView12);
        cancelButton = findViewById(R.id.button9);
        saveButton = findViewById(R.id.button10);

        //Gives access to database
        databaseHelper = new DatabaseHelper(this);

        //Close the Add Item screen without saving
        cancelButton.setOnClickListener(v -> finish());

        //Save a new inventory item
        saveButton.setOnClickListener(v -> {
            String itemName = itemNameInput.getText().toString().trim();
            String quantityText = quantityInput.getText().toString().trim();

            if (itemName.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(this,
                        "Please enter an item name and quantity",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            boolean itemAdded = databaseHelper.addItem(itemName, quantity);

            if (itemAdded) {
                Toast.makeText(this,
                        "Item added successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this,
                        "Unable to add item",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
