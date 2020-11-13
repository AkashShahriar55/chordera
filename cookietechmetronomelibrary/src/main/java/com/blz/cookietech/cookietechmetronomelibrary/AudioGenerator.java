package com.blz.cookietech.cookietechmetronomelibrary;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRouting;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

public class AudioGenerator {

    private int sampleRate;
    private AudioTrack audioTrack;

    public AudioGenerator(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double[] getSineWave(int samples,int sampleRate,double frequencyOfTone){
        double[] sample = new double[samples];
        for (int i = 0; i < samples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequencyOfTone));
        }
        return sample;
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



    public void createPlayer(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            AudioFormat audioFormat = new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build();
            audioTrack = new AudioTrack(audioAttributes,audioFormat,
                     sampleRate,
                    AudioTrack.PERFORMANCE_MODE_LOW_LATENCY,1);
        }else {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, sampleRate,
                    AudioTrack.MODE_STREAM);
        }

        //audioTrack.play();
    }

    public void writeSound(byte[] samples) {
        Log.d("akash_debug", "writeSound: " + System.currentTimeMillis());
        int count = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            count = audioTrack.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
        }else{
            count = audioTrack.write(samples, 0, samples.length);
        }
        Log.d("akash_debug", "writeSound: " + System.currentTimeMillis() + " " + count);
    }

    public void pauseAudioTrack() {
        audioTrack.pause();
        audioTrack.flush();
    }

    public void playAudioTrack(){
        audioTrack.play();
    }

    public boolean isPlayerCreated(){
        return audioTrack != null;
    }

    public void destroyAudioTrack(){
        audioTrack.stop();
        audioTrack.flush();
        audioTrack.release();
    }

}
