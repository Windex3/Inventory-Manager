package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 1;

    //These values are for the users table
    private static final String USERS_TABLE = "users";
    private static final String USER_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    //These values are for the inventory table
    private static final String INVENTORY_TABLE = "inventory";
    private static final String ITEM_ID = "id";
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_QUANTITY = "quantity";

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Assigns the users table to createUsersTable
        String createUsersTable =
                "CREATE TABLE " + USERS_TABLE + " (" +
                        USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USERNAME + " TEXT UNIQUE NOT NULL, " +
                        PASSWORD + " TEXT NOT NULL)";

        //Assigns the inventory table to createInventoryTable
        String createInventoryTable =
                "CREATE TABLE " + INVENTORY_TABLE + " (" +
                        ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ITEM_NAME + " TEXT NOT NULL, " +
                        ITEM_QUANTITY + " INTEGER NOT NULL)";

        //Creates the tables
        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    //This method is run if the version is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE);

        onCreate(db);
    }

    //Adds a new user account to the users table
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);

        long result = db.insert(USERS_TABLE, null, values);

        return result != -1;
    }

    //Checks if a username and password match an existing account
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = USERNAME + " = ? AND " + PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                USERS_TABLE,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean userExists = cursor.getCount() > 0;
        cursor.close();

        return userExists;
    }

    //Adds a new item to the inventory table(Create)
    public boolean addItem(String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, itemName);
        values.put(ITEM_QUANTITY, quantity);

        long result = db.insert(INVENTORY_TABLE, null, values);

        return result != -1;
    }

    //Returns all inventory items(Read)
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                INVENTORY_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    //Updates the quantity of an existing inventory item(Update)
    public boolean updateItemQuantity(int itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_QUANTITY, newQuantity);

        String selection = ITEM_ID + " = ?";
        String[] selctionArgs = {String.valueOf(itemId)};

        int rowsUpdated = db.update(
                INVENTORY_TABLE,
                values,
                selection,
                selctionArgs
        );

        return rowsUpdated > 0;
    }

    //Deletes an inventory item(Delete)
    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ITEM_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};

        int rowsDeleted = db.delete(
                INVENTORY_TABLE,
                selection,
                selectionArgs
        );

        return rowsDeleted > 0;
    }
}
