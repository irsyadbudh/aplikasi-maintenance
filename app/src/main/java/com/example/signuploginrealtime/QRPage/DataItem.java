package com.example.signuploginrealtime.QRPage;

import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataItem {
    private String id;
    private String itemName;
    private String information;
    private String date;

    public DataItem() {
        // Default constructor required for Firebase Realtime Database
    }

    public DataItem(String id, String itemName, String information, String date) {
        this.id = id;
        this.itemName = itemName;
        this.information = information;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                ", information='" + information + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    // Exclude the "id" field from being saved to Firebase Realtime Database
    @Exclude
    public String getKey() {
        // When retrieving data from Firebase, use getKey() to get the automatically generated key
        // Firebase uses this key as the "id" for each data item in the database
        return id;
    }

    // Exclude the "id" field from being saved to Firebase Realtime Database
    @Exclude
    public void setKey(String key) {
        // This method is not needed because Firebase generates the "id" automatically
    }

    public long getDateTimestamp() {
        // Parse the date and return its timestamp
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(this.date);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
