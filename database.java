package com.example.exercise7;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText editTextName, editTextEmail, editTextId;
    Button btnAdd, btnView, btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextId = findViewById(R.id.editTextId);
        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(view -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.insertUser(name, email);
            if (inserted) {
                Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding user", Toast.LENGTH_SHORT).show();
            }
        });

        btnView.setOnClickListener(view -> {
            Cursor cursor = dbHelper.getUsers();
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder sb = new StringBuilder();
            while (cursor.moveToNext()) {
                sb.append("ID: ").append(cursor.getInt(0))
                        .append("\nName: ").append(cursor.getString(1))
                        .append("\nEmail: ").append(cursor.getString(2))
                        .append("\n\n");
            }

            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        });

        btnUpdate.setOnClickListener(view -> {
            int id;
            try {
                id = Integer.parseInt(editTextId.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid ID", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();

            boolean updated = dbHelper.updateUser(id, name, email);
            if (updated) {
                Toast.makeText(this, "User Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(view -> {
            int id;
            try {
                id = Integer.parseInt(editTextId.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid ID", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean deleted = dbHelper.deleteUser(id);
            if (deleted) {
                Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deletion Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.exercise7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert Data
    public boolean insertUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1; // If result = -1, insertion failed
    }

    // Read Data
    public Cursor getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Update Data
    public boolean updateUser(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    // Delete Data
    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }
}

