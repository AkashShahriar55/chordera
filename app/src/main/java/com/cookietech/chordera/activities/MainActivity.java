package com.cookietech.chordera.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.cookietech.chordera.Splash.SplashFragment;
import com.cookietech.chordera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    SplashFragment splashFragment;
    FragmentManager fragmentManager;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        splashFragment = SplashFragment.newInstance();
        fragmentManager = getSupportFragmentManager();
        addFragmentToBackStack(splashFragment,splashFragment.getTag(),binding.mainFragmentHolder.getId());
    }

    public void addFragmentToBackStack(Fragment fragment, String tag, int containerViewId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(binding.mainFragmentHolder.getId(), fragment);
        fragmentTransaction.commit();
    }
}