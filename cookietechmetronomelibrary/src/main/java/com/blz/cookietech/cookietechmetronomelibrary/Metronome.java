package com.blz.cookietech.cookietechmetronomelibrary;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blz.cookietech.cookietechmetronomelibrary.Model.Constants;

public class Metronome extends HandlerThread {

    double[] tick;
    double[] tock;
    byte[] tickByte;
    byte[] tockByte;


    private static final String NAME = "Metronome Thread";
    public static final int MESSAGE_PLAY_PAUSE = 1;
    private final AudioGenerator audioGenerator = new AudioGenerator(44100);
    public Looper looper;
    public Handler handler;
    // handler is associated with the message queue of this thread

    BeatGenerator beatGenerator = new BeatGenerator();

    public Metronome(double[] tick, double[] tock, int bpm, int subdivision, int timeSignature) {
        super(NAME, Process.THREAD_PRIORITY_FOREGROUND);
        this.tick = tick;
        this.tock = tock;
        beatGenerator.setBpm(bpm);
        beatGenerator.setSubDivision(subdivision);
        beatGenerator.setTimeSignature(timeSignature);
    }



    @Override
    public void run() {

        Log.d("akash_debug", "start of run():" + System.currentTimeMillis());

        audioGenerator.createPlayer();
        tickByte = get16BitPcm(tick);
        tockByte = get16BitPcm(tock);
        //handler will only work if there is a looper
        Looper.prepare(); // it creates both looper and msg queue

        looper = Looper.myLooper();//initialize looper

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("akash_debug", "message comes:");
                switch (msg.what){
                    case Metronome.MESSAGE_PLAY_PAUSE:
                        if(msg.arg1== 1){
                            beatGenerator.setPlaying(true);
                        }else if(msg.arg1 == 0){
                            beatGenerator.setPlaying(false);
                        }

                        break;

                }
            }
        };

        // handler is associated with the message queue of this thread
        // start the loopers loop
        Log.d("akash_debug", "start of loop: " + System.currentTimeMillis());
        Looper.loop(); // this is an infinite loop until get a exit msg


        audioGenerator.destroyAudioTrack();



        Log.d("akash_debug", "End of run():");
    }


    public byte[] get16BitPcm(double[] samples) {
        byte[] generatedSound = new byte[2 * samples.length];
        int index = 0;
        for (double sample : samples) {
            // scale to maximum amplitude
            short maxSample = (short) ((sample * Short.MAX_VALUE));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[index++] = (byte) (maxSample & 0x00ff);
            generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

        }
        return generatedSound;
    }

    public void stopMetronome() {
        audioGenerator.pauseAudioTrack();
        beatGenerator.setPlaying(false);
    }

    public void playMetronome() {
        if(audioGenerator.isPlayerCreated()){
            audioGenerator.playAudioTrack();
        }
        beatGenerator.setPlaying(true);
        handler.post(beatGenerator);
    }

    public void destroyAndReleaseResource() {
        beatGenerator.setPlaying(false);
        this.looper.quitSafely();
    }

    public void setBpm(int bpm) {
        beatGenerator.setBpm(bpm);
    }

    public void setTimeSignature(int timeSignature) {
        beatGenerator.setTimeSignature(timeSignature);
    }

    public void setSubDivision(int subDivision) {
        beatGenerator.setSubDivision(subDivision);
    }


    private class BeatGenerator implements Runnable{
        int bpm = 100;
        boolean playing = false;
        private int timeSignature = 4;
        private int[] combination = {1};
        private boolean beatSlice = false;
        private int subDivision =1;

        public void setPlaying(boolean playing) {
            this.playing = playing;
        }

        private int calcBeatSlice(int subdivision) {
            return  (int) ((((float)60/bpm)*44100)/((float) subdivision/2));
        }
        private long sleepTime(int subdivision){
            return (long) ((60000/bpm)/(subdivision*2));
        }

        @Override
        public void run() {


            int timeSignaturePointer = 1;

            do{
                Log.d("akash_debug", "run: ");

                int beatSlice = calcBeatSlice(subDivision);
                Log.d("akash_debug", "run: " + beatSlice );
                byte[] sample = new byte[beatSlice];
                if(timeSignaturePointer == 1){
                    System.arraycopy(tickByte, 0, sample, 0, Math.min(beatSlice, tickByte.length));
                }else{
                    System.arraycopy(tockByte, 0, sample, 0, Math.min(beatSlice, tockByte.length));
                }

                if(timeSignaturePointer >= timeSignature*subDivision){
                    timeSignaturePointer = 1;
                }else{
                    timeSignaturePointer++;
                }


                audioGenerator.writeSound(sample);
            }while (playing);


        }

        public void setBpm(int bpm) {
            this.bpm = bpm;
        }

        public void setTimeSignature(int timeSignature) {
            this.timeSignature = timeSignature;
        }

        public void setSubDivision(int subDivision) {
            this.subDivision = subDivision;
        }
    }
}
