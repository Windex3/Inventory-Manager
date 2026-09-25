package com.example.inventoryapp;

import android.os.Bundle;
import android.database.Cursor;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity{
    //UI Components
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryItems;
    private Button addItemButton;
    private Button smsAlertsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Connecting to the UI components
        recyclerView = findViewById(R.id.recyclerView);
        addItemButton = findViewById(R.id.button7);
        smsAlertsButton = findViewById(R.id.button6);
        databaseHelper = new DatabaseHelper(this);
        inventoryItems = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInventory();

        inventoryAdapter = new InventoryAdapter(inventoryItems, databaseHelper);
        recyclerView.setAdapter(inventoryAdapter);

        //Opens the Add Item screen
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        //Opens the SMS alerts screen
        smsAlertsButton.setOnClickListener(v -> {
            Intent intent = new Intent (
                    InventoryActivity.this,
                    SMSAlertsActivity.class);

            startActivity(intent);
        });


    }

    //Adds the new items to the inventory screen when going back to it
    @Override
    protected void onResume() {
        super.onResume();

        if (inventoryAdapter != null) {
            inventoryItems.clear();
            loadInventory();
            inventoryAdapter.notifyDataSetChanged();
        }
    }

    //Adds all the items to the inventory screen
    private void loadInventory() {
        Cursor cursor = databaseHelper.getAllItems();

        //Runs until there are not more values in the cursor
        while (cursor.moveToNext()) {
            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("item_name")
            );

            int quantity = cursor.getInt(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            InventoryItem item = new InventoryItem(id, name, quantity);

            inventoryItems.add(item);
        }

        cursor.close();
    }
}
