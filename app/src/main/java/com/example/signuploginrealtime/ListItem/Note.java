package com.example.signuploginrealtime.ListItem;

public class Note {
    String key, title, content;
    boolean isChecked; // Tambahkan atribut isChecked

    public Note() {

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getKey() {
        return key;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
