package com.example.signuploginrealtime.NavigationBar;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.signuploginrealtime.ListItem.ListActivity;
import com.example.signuploginrealtime.QRPage.QRActivity;
import com.example.signuploginrealtime.QRPage.RekapActivity;
import com.example.signuploginrealtime.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the ImageView
        ImageView imageView4 = rootView.findViewById(R.id.imageView4);
        ImageView imageView6 = rootView.findViewById(R.id.imageView6);
        ImageView imageView5 = rootView.findViewById(R.id.imageView5);

        // Set click listener for the ImageView
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the intent to start QRActivity
                Intent intent = new Intent(getActivity(), QRActivity.class);
                startActivity(intent);
            }
        });


        // Di dalam HomeFragment, ketika Anda pindah ke RekapActivity
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tampilkan RekapActivity dari HomeFragment
                Intent intent = new Intent(getActivity(), RekapActivity.class);
                startActivity(intent);
            }
        });


        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the intent to start QRActivity
                Intent intent = new Intent(getActivity(), ListActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
