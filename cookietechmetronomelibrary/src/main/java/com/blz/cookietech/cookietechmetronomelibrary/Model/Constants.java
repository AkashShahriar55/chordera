package com.blz.cookietech.cookietechmetronomelibrary.Model;

public class Constants {

    private static int bpm = 20;
    private final static double [] tick = new double[1001];
    private final static double [] tock = new double[1001];
    private final static int tickSampleSize = 1000;


    public static double[] getTick() {
        return tick;
    }

    public static double[] getTock() {
        return tock;
    }

    public static void setTickValue(int index,double value){
        tick[index] = value;
    }

    public static void setTockValue(int index,double value){
        tock[index] = value;
    }

    public static int getBpm() {
        return bpm;
    }

    public static void setBpm(int bpm) {
        Constants.bpm = bpm;
    }
}
