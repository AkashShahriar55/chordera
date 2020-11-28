package com.cookietech.chordlibrary.AppComponent;

import android.graphics.Bitmap;

import com.cookietech.chordlibrary.Chord;

public interface ThumbGeneratorListener {

    void onThumbGenerated(int index, Bitmap thumb, Chord chord);
}
