package com.cookietech.chordlibrary.Model;

import java.util.ArrayList;

public class Constants {
    public static final int original_fretboard_width = 600;
    public static final int original_fretboard_height = 4200;
    public static final int original_neck_bar_height = 50;
    public static final int original_fret_height = 50;
    public static final int original_fret_distance = 150;
    public static final int original_fret_offset = 20;
    public static final int original_fretboard_dot_width = 50;

    public static final double fretboard_width_ratio = 0.8;
    public static final double fretboard_height_ratio_with_width = 7;

    private static ArrayList<Integer> allFretMiddlePosition;

    public static ArrayList<Integer> getAllFretMiddlePosition() {
        return allFretMiddlePosition;
    }

    public static void setAllFretMiddlePosition(ArrayList<Integer> allFretMiddlePosition) {
        Constants.allFretMiddlePosition = allFretMiddlePosition;
    }
}
