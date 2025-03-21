package com.cookietech.chordera.application;

import com.cookietech.chordlibrary.ChordClass;
import com.cookietech.chordlibrary.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppSharedComponents {
    private static double [] tick = new double[3001];
    private static double [] tock = new double[3001];
    private final static int tickSampleSize = 1000;

    public static double[] getTick() {
        return tick;
    }

    public static double[] getTock() {
        return tock;
    }

    public static int getTickSampleSize() {
        return tickSampleSize;
    }

    public static void setTick(int index,double tick) {
       AppSharedComponents.tick[index] = tick;
    }

    public static void setTock(int index,double tock) {
        AppSharedComponents.tock[index] = tock;
    }

    public static final ArrayList<String> major_key_circle = new ArrayList<String>(
            Arrays.asList("C", "C#", "D","D#","E","F","F#","G","G#","A","A#","B"));

    public static final ArrayList<String> minor_key_circle = new ArrayList<String>(
            Arrays.asList("Cm", "C#m", "Dm","D#m","Em","Fm","F#m","Gm","G#m","Am","A#m","Bm"));

    private static ArrayList<Root> roots = new ArrayList<>();
    private static  Map<String,ChordClass> allChords = new HashMap<>();

    public static ArrayList<Root> getRoots() {
        return roots;
    }

    public static void setRoots(ArrayList<Root> roots) {
        AppSharedComponents.roots = roots;

    }

    public static Map<String, ChordClass> getAllChords() {
        return allChords;
    }

    public static void setAllChords(Map<String, ChordClass> allChords) {
        AppSharedComponents.allChords = allChords;
    }
}
