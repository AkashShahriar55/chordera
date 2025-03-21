package com.cookietech.chordlibrary.AppComponent;

import android.graphics.Bitmap;

import com.cookietech.chordlibrary.Variation;

public interface ThumbGeneratorListener {

    void onThumbGenerated(int index, Bitmap thumb, Variation chord);
}
