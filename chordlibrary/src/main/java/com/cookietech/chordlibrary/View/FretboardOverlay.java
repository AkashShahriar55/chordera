package com.cookietech.chordlibrary.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.CircularArray;
import androidx.core.content.ContextCompat;

import com.cookietech.chordlibrary.Variation;
import com.cookietech.chordlibrary.R;

import java.util.ArrayList;
import java.util.Arrays;

public class FretboardOverlay extends View {

    private Variation chord;
    private Canvas mCanvas;
    private Context context;
    private int canvasWidth;
    private int canvasHeight;
    private Boolean isNotesVisible = false;
    private ArrayList<String> notes = new ArrayList<>(Arrays.asList("A","A#","B","C","C#","D","D#","E","F","F#","G","G#"));
    private ArrayList<String> strings = new ArrayList<>(Arrays.asList("E","A","D","G","B","E"));
    private Rect textRect = new Rect();

    public static final double fretboardRatio = 7; // height = width * 7

    private ArrayList<Integer> allFretMiddlePosition = new ArrayList<>();
    private ArrayList<Integer> allStringMiddlePosition = new ArrayList<>();

    int[] fingerDotIds = {R.drawable.finger_dot_1,R.drawable.finger_dot_2,R.drawable.finger_dot_3,R.drawable.finger_dot_4};
    int noFingerDotId = R.drawable.finger_dot;
    int[] stringIds = {R.drawable.string_6,R.drawable.string_5,R.drawable.string_4,R.drawable.string_3,R.drawable.string_2,R.drawable.string_1};
    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private int leftOffset;
    public FretboardOverlay(@NonNull Context context) {
        super(context);
    }

    public FretboardOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Log.d("akash_fretboard_inspect", "FretboardOverlay: background");
    }

    public void setChord(Variation chord) {
        this.chord = chord;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        mCanvas.translate(leftOffset,0);
        calculateMiddlePositions();
        generateChord();


        Log.d("akash_fretboard_inspect", "onDraw: foreground");
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        canvasWidth = (int) (w*0.8);
        leftOffset = (w - canvasWidth)/2;
        canvasHeight = (int) (canvasWidth * fretboardRatio);

        setMeasuredDimension(w, canvasHeight);
    }

    private void calculateMiddlePositions() {
        int distanceBetweenFrets = (150 * canvasHeight) / 4200;
        int startOffset = (50 * canvasHeight) / 4200;
        int fretOffset = (20 * canvasHeight) / 4200;

        Log.d("akash_debug", "addFrets: " +startOffset+" "+fretOffset);

        int prevTop = 0;

        for (int i = 1; i <= 24; i++) {
            int top = 0;
            if(i==1){
                top = distanceBetweenFrets + startOffset;
            }else{
                top = (int) (prevTop + fretOffset + (distanceBetweenFrets - (2.5 * (i-1))));
            }

            int middle = prevTop + fretOffset +  (top - (prevTop + fretOffset))/2;
            Log.d("akash_fretboard_debug", "calculateMiddlePositions: " + middle);
            allFretMiddlePosition.add(middle);
            prevTop = top;
        }


    }

    public void generateChord(){
        addString();
        if(chord != null){
            Log.d("akash_fretboard_debug", "generateChord: ");
            addChord();
            addFretNumbers();
        }

    }

    private void addFretNumbers() {
        for (int i = chord.getFirstFret(); i <= chord.getLastFret(); i++) {
            int middle = allFretMiddlePosition.get(i-1);
            mCanvas.save();
            mCanvas.translate(-leftOffset,0);
            mCanvas.rotate(270);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            int size = leftOffset/2;
            textPaint.setTextSize(size);
            Rect bounds = new Rect();
            textPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), bounds);
            int textHeight = bounds.height();
            int textWidth = bounds.width();
            int posx = (leftOffset/2)+textHeight/2;
            int posy = middle+ (textWidth/2);
            mCanvas.drawText(String.valueOf(i),-posy,posx,textPaint);
            mCanvas.restore();
        }

    }

    private void addChord() {
        ArrayList<Integer> fingers = chord.getFingers();
        ArrayList<Integer> notes = chord.getNotes();

        for (int finger = 1; finger <= 4; finger++) {
            int count = 0;
            int fret = -1;
            int firstOccurrenceString = -1;
            int lastOccurrenceString = -1;

            for (int i = 0; i < 6; i++) {
                if(fingers.get(i) == finger){
                    count++;
                    if(firstOccurrenceString == -1){
                        firstOccurrenceString = i;
                        fret = notes.get(i);
                    }else{
                        lastOccurrenceString = i;
                    }
                }
            }

            Log.d("akash_debug", "addChord: "+ finger + " " + firstOccurrenceString + " " + lastOccurrenceString + " " + count);

            if(count == 1){
                addFinger(firstOccurrenceString,fret,finger);
            }else if(count > 1){
                addBar(firstOccurrenceString,lastOccurrenceString,fret);
                addFinger(firstOccurrenceString,fret,finger);
                addFinger(lastOccurrenceString,fret,finger);
            }

        }
    }

    private void addBar(int firstOccurrenceString, int lastOccurrenceString, int fret) {
        //add bar
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context,R.color.bar_color));
        mCanvas.drawRect(allStringMiddlePosition.get(firstOccurrenceString), allFretMiddlePosition.get(fret-1) - 25, allStringMiddlePosition.get(lastOccurrenceString), allFretMiddlePosition.get(fret-1) + 25,paint);
    }

    private void addFinger(int OccurrenceString, int fret,int finger) {
        int changedWidth = (100 * canvasWidth) / 600;
        int offSet = changedWidth / 2;
        //add fingers
        if(isNotesVisible){
            Bitmap fingerDot = BitmapFactory.decodeResource(getResources(),noFingerDotId);
            fingerDot = Bitmap.createScaledBitmap(fingerDot,changedWidth, changedWidth,false);
            mCanvas.drawBitmap(fingerDot, allStringMiddlePosition.get(OccurrenceString) - offSet,allFretMiddlePosition.get(fret-1)-offSet,null);
            float textSize = changedWidth/2.5f;
            textPaint.setTextSize(textSize);
            textPaint.setColor(Color.WHITE);

            int rootPosition = notes.indexOf(strings.get(OccurrenceString));
            int notePosition = (fret%12);
            ArrayList<String> changedNotes = new ArrayList<>(notes.subList(rootPosition, notes.size()));
            changedNotes.addAll(notes.subList(0,rootPosition));
            Log.d("akash_notes_debug", "addFinger: " + changedNotes);
            String note = changedNotes.get(notePosition);
            Log.d("akash_notes_debug", "addFinger: " + note);
            textPaint.getTextBounds(note,0,note.length(),textRect);

            mCanvas.drawText(note, allStringMiddlePosition.get(OccurrenceString)-textRect.width()/2f,allFretMiddlePosition.get(fret-1)+textRect.height()/2f,textPaint);
        }else{
            Bitmap fingerDot = BitmapFactory.decodeResource(getResources(),fingerDotIds[finger-1]);
            fingerDot = Bitmap.createScaledBitmap(fingerDot,changedWidth, changedWidth,false);
            mCanvas.drawBitmap(fingerDot, allStringMiddlePosition.get(OccurrenceString) - offSet,allFretMiddlePosition.get(fret-1)-offSet,null);
        }


    }






    private void addString() {
        int mainWidth = 20;
        int mainHeight = 4200;
        int mainPadding = 50;

        int changedWidth = (mainWidth* canvasWidth)/600;
        int changedHeight = canvasHeight;
        int changedPadding = (mainPadding*canvasWidth)/600;
        int offset = changedWidth/2;

        int middlePortion = canvasWidth - ( 2* changedPadding);
        int stringInterval = middlePortion/5;

        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(50);
        if(chord != null){
            ArrayList<Integer> notes = chord.getNotes();
            for (int i = 0; i < notes.size(); i++) {
                int stringLeft = canvasWidth - changedPadding - stringInterval * (5-i);
                allStringMiddlePosition.add(stringLeft);
                Bitmap string = BitmapFactory.decodeResource(getResources(),stringIds[i]);
                string = Bitmap.createScaledBitmap(string,changedWidth, changedHeight,false);
                if(notes.get(i) == -1){

                    mCanvas.drawBitmap(string, stringLeft - offset,0,alphaPaint);
                }

                else{

                    mCanvas.drawBitmap(string, stringLeft - offset,0,null);

                }

            }


        }else{
            for (int i = 0; i < 6; i++) {
                int stringLeft = canvasWidth - changedPadding - stringInterval * (5-i);
                allStringMiddlePosition.add(stringLeft);
                Bitmap string = BitmapFactory.decodeResource(getResources(),stringIds[i]);
                string = Bitmap.createScaledBitmap(string,changedWidth, changedHeight,false);
                mCanvas.drawBitmap(string, stringLeft - offset,0,null);
            }
        }





    }


    public void setNotesVisible(Boolean bool) {
        isNotesVisible = bool;
        invalidate();
    }
}
