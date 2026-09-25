package com.example.inventoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.List;

public class InventoryAdapter  extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>{
    //Holds the inventory items that will be displayed in the RecyclerView
    private List<InventoryItem> inventoryItems;
    //Provides access to the inventory database
    private DatabaseHelper databaseHelper;

    //Receives the inventory data and database helper from InventoryActivity
    public InventoryAdapter(List<InventoryItem> inventoryItems, DatabaseHelper databaseHelper) {
        this.inventoryItems = inventoryItems;
        this.databaseHelper = databaseHelper;
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        //UI components
        TextView itemName;
        TextView quantity;
        Button decreaseButton;
        Button increaseButton;
        Button deleteButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            //Connects to the UI Components
            itemName = itemView.findViewById(R.id.textView3);
            quantity = itemView.findViewById(R.id.textView5);
            decreaseButton = itemView.findViewById(R.id.button3);
            increaseButton = itemView.findViewById(R.id.button5);
            deleteButton = itemView.findViewById(R.id.button);
        }
    }

    //Creates a new inventory item view using item_inventory.xml
    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);

        return new InventoryViewHolder(view);
    }

    //Places inventory data into each item displayed by the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);

        //Displays items current information
        holder.itemName.setText(item.getName());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        //Increases quantity
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;

            if (databaseHelper.updateItemQuantity(item.getId(), newQuantity)) {
                item.setQuantity(newQuantity);
                holder.quantity.setText(String.valueOf(newQuantity));
            }
        });

        //Decrease quantity
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
               int newQuantity = item.getQuantity() - 1;

               if (databaseHelper.updateItemQuantity(item.getId(), newQuantity)) {
                   item.setQuantity(newQuantity);
                   holder.quantity.setText(String.valueOf(newQuantity));

                   //Send an SMS alert when inventory reaches zero
                   if (newQuantity == 0) {
                       sendLowInventoryAlert (holder.itemView.getContext(), item);
                   }
               }
            }
        });

        //Delete item
        holder.deleteButton.setOnClickListener(v -> {
            if (databaseHelper.deleteItem(item.getId())) {
                int currentPosition = holder.getBindingAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {
                    inventoryItems.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                }
            }
        });
    }

    private void sendLowInventoryAlert(Context context, InventoryItem item) {
        //Checks that the user granted SMS permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Gets the saved phone number
        SharedPreferences preferences =
                context.getSharedPreferences(
                        "SMSSettings",
                        Context.MODE_PRIVATE);

        String phoneNumber = preferences.getString("phoneNumber", "");

        //Doesn't send if there is no saved phone number
        if (phoneNumber.isEmpty()) {
            return;
        }

        String message =
                "Inventory Alert: " + item.getName()
                    + " has reached zero.";

        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }
}
