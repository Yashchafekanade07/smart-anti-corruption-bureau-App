package com.example.smartanticurruptionbeuroplatform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

    public class HomeFragment extends Fragment {

        public HomeFragment() {
            // Required empty public constructor
        }

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {

            TextView textView = new TextView(requireContext());
            textView.setText("Welcome to ACB Home Fragment!");
            textView.setTextSize(22);
            textView.setTextColor(0xFFFFFFFF);
            textView.setGravity(android.view.Gravity.CENTER);

            return textView;
        }
    }
