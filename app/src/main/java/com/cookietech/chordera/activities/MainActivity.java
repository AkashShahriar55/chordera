package com.cookietech.chordera.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.cookietech.chordera.Landing.LandingFragment;
import com.cookietech.chordera.Splash.SplashFragment;
import com.cookietech.chordera.appcomponents.CookieTechFragmentManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    SplashFragment splashFragment;
    LandingFragment landingFragment;
    ActivityMainBinding binding;
    CookieTechFragmentManager cookieTechFragmentManager;
    MainViewModel mainViewModel;
    Observer<String> navigationObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        navigationObserver = new Observer<String>() {
            @Override
            public void onChanged(String tag) {
                navigateTo(tag);
            }
        };

        mainViewModel.getNavigation().observe(this,navigationObserver);

        cookieTechFragmentManager = CookieTechFragmentManager.getInstance();
        splashFragment = SplashFragment.newInstance();
        landingFragment = LandingFragment.newInstance();
        cookieTechFragmentManager.initCookieTechFragmentManager(getSupportFragmentManager());
        cookieTechFragmentManager.addFragmentToBackStack(landingFragment, NavigatorTags.LANDING_FRAGMENT,binding.mainFragmentHolder.getId());
        cookieTechFragmentManager.addFragmentToBackStack(splashFragment, NavigatorTags.SPLASH_FRAGMENT,binding.mainFragmentHolder.getId());

    }

    private void navigateTo(String tag) {
        cookieTechFragmentManager.popFragmentExclusive(tag);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}