package com.cookietech.chordera.application;

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
}
