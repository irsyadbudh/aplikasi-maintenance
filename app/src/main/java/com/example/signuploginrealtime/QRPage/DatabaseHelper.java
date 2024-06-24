package com.example.signuploginrealtime.QRPage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "rekap";
    private DatabaseReference databaseReference;

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(DATABASE_NAME);
    }

    public void insertData(String text, String plainText, String date) {
        String key = databaseReference.push().getKey();
        DataItem dataItem = new DataItem(key, text, plainText, date);
        databaseReference.child(key).setValue(dataItem);
    }

    public void getAllData(DataCallback dataCallback) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                if (dataItem != null) {
                    dataCallback.onDataAdded(dataItem);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                if (dataItem != null) {
                    dataCallback.onDataChanged(dataItem);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                if (dataItem != null) {
                    dataCallback.onDataRemoved(dataItem);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Not implemented
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void deleteData(String id) {
        databaseReference.child(id).removeValue();
    }

    public void deleteAllData() {
        databaseReference.removeValue();
    }

    public interface DataCallback {
        void onDataAdded(DataItem dataItem);
        void onDataChanged(DataItem dataItem);
        void onDataRemoved(DataItem dataItem);
    }
}
