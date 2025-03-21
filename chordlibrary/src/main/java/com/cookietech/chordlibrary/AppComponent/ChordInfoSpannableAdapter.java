package com.cookietech.chordlibrary.AppComponent;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.cookietech.chordlibrary.Variation;

public class ChordInfoSpannableAdapter extends SpannableStringBuilder {
    Variation chord;
    int notes_heading_color = Color.parseColor("#53A6F6");
    public ChordInfoSpannableAdapter( Variation chord) {
        super("");
        this.chord = chord;
        buildFormat();
    }

    private void buildFormat() {
        append("Notes");
        setSpan(new RelativeSizeSpan(1.2f),0,length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        setSpan(new ForegroundColorSpan(notes_heading_color),0,length(),SPAN_EXCLUSIVE_EXCLUSIVE);
        append("\n");

    }
}
