package com.example.smartanticurruptionbeuroplatform;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FragmentDemoActivity extends AppCompatActivity {

    Button btnHomeFragment, btnDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo);

        btnHomeFragment = findViewById(R.id.btnHomeFragment);
        btnDetailsFragment = findViewById(R.id.btnDetailsFragment);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        btnHomeFragment.setOnClickListener(v ->
                loadFragment(new HomeFragment()));

        btnDetailsFragment.setOnClickListener(v ->
                loadFragment(new DetailsFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
