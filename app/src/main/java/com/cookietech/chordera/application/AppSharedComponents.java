package com.cookietech.chordera.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            Arrays.asList("Cm", "Cm#", "Dm","Dm#","Em","Fm","Fm#","Gm","Gm#","Am","Am#","Bm"));
}
