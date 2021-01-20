package com.cookietech.chordera.chordDisplay.chordDetails;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cookietech.chordlibrary.ChordClass;

public class ChordVariationAdapter extends FragmentStateAdapter {
    ChordClass chordClass;
    public ChordVariationAdapter(@NonNull Fragment fragment,ChordClass chordClass) {
        super(fragment);
        this.chordClass = chordClass;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("variation_debug", "onViewCreated: variation fragment is creating in adapter");
        return ChordVariationFragment.newInstance(chordClass.getVariations().get(position),chordClass.getName(),position+1);
    }

    @Override
    public int getItemCount() {
        return chordClass.getVariations().size();
    }
}
