package com.example.signuploginrealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDatabaseRef;

    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword); // Make sure you have this EditText in your layout
        saveButton = findViewById(R.id.saveButton);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    public void showData() {
        Intent intent = getIntent();
        if (intent != null) {
            editName.setText(intent.getStringExtra("name"));
            editEmail.setText(intent.getStringExtra("email"));

            // Set the password if available
            String password = intent.getStringExtra("password");
            if (password != null) {
                editPassword.setText(password);
            }

            // Set the other fields as non-editable
            editEmail.setFocusable(false);
            editEmail.setClickable(false);
            editPassword.setFocusable(false);
            editPassword.setClickable(false);
        }
    }


    private void updateProfile() {
        final String newName = editName.getText().toString().trim();

        if (currentUser != null) {
            // Re-authenticate and update name
            currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Name updated successfully
                                updateUserProfileData(newName);
                            } else {
                                // Handle name update error
                                Toast.makeText(EditProfileActivity.this, "Failed to update name.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Handle the case where user is not authenticated
            Toast.makeText(EditProfileActivity.this, "User is not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfileData(String name) {
        DatabaseReference userRef = userDatabaseRef.child(currentUser.getUid());
        userRef.child("name").setValue(name);

        // Set the result for the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("email", currentUser.getEmail());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void setResultAndFinish(String name, String email) {
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveChanges() {
        String updatedName = editName.getText().toString().trim();
        String updatedEmail = editEmail.getText().toString().trim();

        // Update data di Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(updatedName)
                    .build();

            currentUser.updateProfile(profileUpdates);
        }

        // Update data di Firebase Realtime Database
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseRef.child(currentUser.getUid()).child("name").setValue(updatedName);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", updatedName);
        resultIntent.putExtra("email", updatedEmail);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
