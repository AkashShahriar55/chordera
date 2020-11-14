package com.cookietech.chordera.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blz.cookietech.cookietechmetronomelibrary.MetronomeFragment;
import com.cookietech.chordera.Landing.LandingFragment;
import com.cookietech.chordera.R;
import com.cookietech.chordera.Splash.SplashFragment;
import com.cookietech.chordera.appcomponents.CookieTechFragmentManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.ActivityMainBinding;
import com.cookietech.chordlibrary.Fragment.ChordLibraryFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    SplashFragment splashFragment;
    LandingFragment landingFragment;
    ChordLibraryFragment chordLibraryFragment;
    MetronomeFragment metronomeFragment;
    ActivityMainBinding binding;
    CookieTechFragmentManager cookieTechFragmentManager;
    MainViewModel mainViewModel;
    Observer<String> navigationObserver;
    long lastBackButtonPressed = 0;

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
        chordLibraryFragment = ChordLibraryFragment.newInstance(System.currentTimeMillis());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        metronomeFragment = MetronomeFragment.newInstance(pendingIntent, AppSharedComponents.getTick(),AppSharedComponents.getTock());
        cookieTechFragmentManager.initCookieTechFragmentManager(getSupportFragmentManager());
        if(savedInstanceState == null){
            navigateTo(NavigatorTags.LANDING_FRAGMENT);
            mainViewModel.setNavigation(NavigatorTags.SPLASH_FRAGMENT);
        }else{
            Log.d("akash_test_debug", "onCreate: restart ");
        }



        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    Log.d("akash_test_debug", "onPermissionsChecked: granted");
                }
                else {
                    Log.d("akash_test_debug", "onPermissionsChecked: denied");
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .onSameThread()
                .check();





    }

    private void navigateTo(String tag) {

        if(cookieTechFragmentManager.getIsFragmentAvailable(tag)){
            cookieTechFragmentManager.popFragmentExclusive(tag);
        }else{
            if(tag.equals(NavigatorTags.LANDING_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(landingFragment, NavigatorTags.LANDING_FRAGMENT,binding.mainFragmentHolder.getId(), R.anim.enter_zoom_in_fade_in,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_zoom_out_fade_out);
            }else if(tag.equalsIgnoreCase(NavigatorTags.SPLASH_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(splashFragment, NavigatorTags.SPLASH_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_left,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_left);
            }else if(tag.equalsIgnoreCase(NavigatorTags.CHORD_LIBRARY_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(chordLibraryFragment,NavigatorTags.CHORD_LIBRARY_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }else if(tag.equalsIgnoreCase(NavigatorTags.METRONOME_FRAGMENT)){
                cookieTechFragmentManager.addFragmentToBackStackWithAnimation(metronomeFragment,NavigatorTags.METRONOME_FRAGMENT,binding.mainFragmentHolder.getId(),R.anim.enter_from_right,R.anim.exit_zoom_out_fade_out,R.anim.enter_zoom_in_fade_in,R.anim.exit_to_right);
            }
        }

        Log.d("akash_debug", "navigateTo: " + cookieTechFragmentManager.getFragmentsTagList() + tag);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        if(Objects.requireNonNull(mainViewModel.getNavigation().getValue()).equals( NavigatorTags.SPLASH_FRAGMENT)){
            return;
        }
        if(Objects.requireNonNull(mainViewModel.getNavigation().getValue()).equals( NavigatorTags.LANDING_FRAGMENT)){
            Toast.makeText(this,"Double tap to exit",Toast.LENGTH_SHORT).show();
            long interval = System.currentTimeMillis() - lastBackButtonPressed;
            if(interval < 1500 && interval > 0){
                finish();
            }else{
                lastBackButtonPressed = System.currentTimeMillis();
            }
            return;
        }

        lastBackButtonPressed = System.currentTimeMillis();

        List<String> taglist = cookieTechFragmentManager.getFragmentsTagList();

        mainViewModel.setNavigation(taglist.get(taglist.size() - 2));
    }
}