package com.cookietech.chordera.chordDisplay;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.content.SearchRecentSuggestionsProvider;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.cookietech.chordera.R;
import com.cookietech.chordera.Util.StringManipulationHelper;
import com.cookietech.chordera.application.AppSharedComponents;
import com.cookietech.chordera.models.TabulatorChordStructure;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TabulatorTextView extends androidx.appcompat.widget.AppCompatTextView {
    TextView dummyTextView;

    ArrayList<Pair<Integer,TabulatorChordStructure>> chordMap = new ArrayList<>();
    Rect chordBackgroundRect = new Rect();
    RectF tempRectF = new RectF();
    Paint chordBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint chordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float extraSpace = dpToPx(15);
    float textHeight = 0;
    Rect chordPositionRect = new Rect();
    Rect measurementRect = new Rect();
    private int initialLineCount;
    private Mode mode;
    TextPaint myTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    ArrayList<Rect> chordTouchableRect = new ArrayList<>();
    HashMap<Rect,String> chordTouchableMap = new HashMap<>();

    public enum Mode{
        Dark,
        Light
    }

    public TabulatorTextView(@NonNull Context context) {
        super(context);
    }

    public TabulatorTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dummyTextView = new TextView(context,attrs);
        setAlpha(0);
    }

    public TabulatorTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v("tabulator onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("tabulator onMeasure h", MeasureSpec.toString(heightMeasureSpec));
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        getPaint().getTextBounds(getText().toString(),0,getText().length(),measurementRect);

        textHeight = getPaint().descent() - getPaint().ascent();
        float totalHeight = (initialLineCount+1) * measurementRect.height() + ((measurementRect.height() +extraSpace) * (initialLineCount));
        Log.d("size_debug", "onMeasure: "+ getPaddingBottom() +" "+ getPaddingTop() + " " + textHeight + " " + measurementRect.height() + " " + initialLineCount);
        int minh = (int) (totalHeight + getPaddingBottom() + getPaddingTop());
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);
        Log.v("tabulator onMeasure h", MeasureSpec.toString(minh)+" " + h);
        Log.d("tabulator_final_debug", "onMeasure: " + h + " " + initialLineCount);
        Log.d("measure_debug", "onMeasure: " + h);
        super.setMeasuredDimension(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        setTextColor(Color.TRANSPARENT);
        Log.d("measure_debug", "onDraw: " + getHeight());
        super.onDraw(canvas);
        if(mode == Mode.Dark){
            myTextPaint.setColor(getResources().getColor(R.color.color_white));
            chordPaint.setColor(getResources().getColor(R.color.color_white));
            chordBackgroundPaint.setColor(getResources().getColor(R.color.tabulator_chord_background_dark));
        }else{
            myTextPaint.setColor(getResources().getColor(R.color.colorPrimary));
            chordPaint.setColor(getResources().getColor(R.color.colorPrimary));
            chordBackgroundPaint.setColor(getResources().getColor(R.color.tabulator_chord_background_light));
        }

        String text = getText().toString();
        Layout layout = getLayout();
        if(layout == null){
            Log.d("text_final_debug", "onDraw: null" + layout.getLineCount());
            return;
            //todo fix the bug
        }
        Log.d("text_final_debug", "onDraw: " + layout.getLineCount());
        for (int i = 0; i < layout.getLineCount(); i++) {
            final int start = layout.getLineStart(i);
            final int end = layout.getLineEnd(i);
            String line = text.substring(start, end);

            final float left = layout.getLineLeft(i);
            final int baseLine = layout.getLineBaseline(i);
            float  offset = (textHeight+ extraSpace) *i;
            Log.d("tabulator_debug", "onDraw: " + offset);
            canvas.drawText(line,
                    left + getTotalPaddingLeft(),
                    // The text will not be clipped anymore
                    // You can add a padding here too, faster than string string concatenation
                    baseLine + getTotalPaddingTop() + offset,
                    myTextPaint);
            Log.d("size_debug", "onMeasure: "+ (baseLine + getTotalPaddingTop() + offset) + " " + layout.getLineCount());
            Log.d("edit_text_test " ,"on Draw");
        }

        chordPaint.setTextSize(getPaint().getTextSize());
        chordPaint.setTextAlign(Paint.Align.LEFT);
        chordPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
        float lastPositionRight = 0;
        int lastBoxPositionTop = -1;
        int count = 0;
        for (Pair<Integer, TabulatorChordStructure> pair : chordMap) {
            String chord = pair.second.getTransposed_chord().toLowerCase();
            chord = chord.substring(0,1).toUpperCase() + chord.substring(1).toLowerCase();
            int lineOfText = layout.getLineForOffset(pair.first);
            float offset = (this.textHeight+ extraSpace) *lineOfText ;
            float xCoordinate =  layout.getPrimaryHorizontal(pair.first)+getTotalPaddingLeft();
            float yCoordinate =  layout.getLineBaseline(lineOfText)+getTotalPaddingTop()+ offset;
            if(!pair.second.isInline()){
                yCoordinate = yCoordinate - (this.textHeight  + extraSpace/2);
            }

            chordPaint.getTextBounds(chord,0,chord.length(),chordBackgroundRect);
            chordBackgroundRect.left = (int) (chordBackgroundRect.left + xCoordinate - dpToPx(4));
            chordBackgroundRect.right = (int) (chordBackgroundRect.right + xCoordinate + dpToPx(4));
            chordBackgroundRect.top = (int) (yCoordinate   + chordBackgroundRect.top - dpToPx(4));
            chordBackgroundRect.bottom = (int) (yCoordinate  + chordBackgroundRect.bottom + dpToPx(4));


           if(lastBoxPositionTop == chordBackgroundRect.top && chordBackgroundRect.left < lastPositionRight){
                Log.d("spacing_debug", "onDraw: " + chordBackgroundRect.left +" "+  pair.second.getChord());
                float pixelToMove = lastPositionRight - chordBackgroundRect.left + dpToPx(4);
                xCoordinate += pixelToMove;
                chordBackgroundRect.left += pixelToMove;
                chordBackgroundRect.right += pixelToMove;
                Log.d("spacing_debug", "onDraw: " + chordBackgroundRect.left + " " + pixelToMove);
            }
            lastBoxPositionTop = chordBackgroundRect.top;
            lastPositionRight = chordBackgroundRect.right;
            tempRectF.set(chordBackgroundRect);
            chordTouchableRect.get(count).set(chordBackgroundRect);
            chordTouchableMap.put(chordTouchableRect.get(count),pair.second.getChord());
            canvas.drawRoundRect(tempRectF,5,5,chordBackgroundPaint);
            canvas.drawText(chord,
                    xCoordinate,
                    // The text will not be clipped anymore
                    // You can add a padding here too, faster than string string concatenation
                    yCoordinate ,
                    chordPaint);

            count++;
        }


        ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.ALPHA,0f,1f);
        animator.setDuration(200);
        animator.start();
    }


    public void setFormattedText(String string){
        setText(generateFormattedText(string));
    }


    public SpannableStringBuilder generateFormattedText(String text){
        SpannableStringBuilder finalStringBuilder = new SpannableStringBuilder();
        requestLayout();
        text = text.replaceAll("\\s{2,}", " ").trim();
        text = text.replace("\\n","\n");


        myTextPaint = getPaint();

        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        float spacingMultiplier = getLineSpacingMultiplier();
        float spacingAddition = getLineSpacingExtra();
        boolean includePadding = getIncludeFontPadding();
        StaticLayout layout = new StaticLayout(text, myTextPaint, width, alignment, spacingMultiplier, spacingAddition, includePadding);

        int globalPointer = 0;
        for (int i = 0; i < layout.getLineCount(); i++) {
            final int start = layout.getLineStart(i);
            final int end = layout.getLineEnd(i);

            String line = text.substring(start, end);

            //Log.d("text_final_debug", "generateFormattedText: " + line);
            // {abcd

            for (int charPointer = 0; charPointer < line.length(); charPointer++) {
                if(charPointer == 0 && line.charAt(0) == ' ' ){
                    continue;
                }
                StringBuilder chordStringBuilder = new StringBuilder();
                char currentChar = line.charAt(charPointer);
                int lookAhead = charPointer;
                if(currentChar == '{' && ++lookAhead < line.length()){
                    chordStringBuilder = new StringBuilder();
                    currentChar = line.charAt(++charPointer);
                    while (currentChar != '}' && ++lookAhead<line.length() ){
                        chordStringBuilder.append(currentChar);
                        finalStringBuilder.append("    ");
                        globalPointer+=4;
                        currentChar = line.charAt(++charPointer);
                    }

                    if(currentChar != '}'){
                        chordStringBuilder.append(currentChar);
                        finalStringBuilder.append("    ");
                        globalPointer+=4;
                    }
                    if(AppSharedComponents.getAllChords().containsKey(chordStringBuilder.toString().toLowerCase())){
                        String chord = chordStringBuilder.toString().toLowerCase();
                        chord = chord.substring(0,1).toUpperCase() + chord.substring(1).toLowerCase();
                        chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer-(4*chordStringBuilder.length()),new TabulatorChordStructure(true,chord,chord)));
                        chordTouchableRect.add(new Rect());
                    }
                    continue;
                }

                if(currentChar == '[' && ++lookAhead < line.length()){
                    chordStringBuilder = new StringBuilder();
                    currentChar = line.charAt(++charPointer);
                    while (currentChar != ']' && ++lookAhead<line.length() ){
                        chordStringBuilder.append(currentChar);
                        currentChar = line.charAt(++charPointer);
                    }
                    if(currentChar != ']'){
                        chordStringBuilder.append(currentChar);
                        if(AppSharedComponents.getAllChords().containsKey(chordStringBuilder.toString().toLowerCase())){
                            String chord = chordStringBuilder.toString().toLowerCase();
                            chord = chord.substring(0,1).toUpperCase() + chord.substring(1).toLowerCase();
                            chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer-1,new TabulatorChordStructure(false,chord,chord)));
                            chordTouchableRect.add(new Rect());
                        }

                        Log.d("tabulator_final_debug", "test : " +globalPointer  + " " + chordStringBuilder.toString());
                    }else if(++lookAhead < line.length()){
                        if(AppSharedComponents.getAllChords().containsKey(chordStringBuilder.toString().toLowerCase())){
                            String chord = chordStringBuilder.toString().toLowerCase();
                            chord = chord.substring(0,1).toUpperCase() + chord.substring(1).toLowerCase();
                            chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer,new TabulatorChordStructure(false,chord,chord)));
                            chordTouchableRect.add(new Rect());
                        }
                        Log.d("tabulator_final_debug", "test : " +globalPointer  + " " + chordStringBuilder.toString());
                        if(line.charAt(lookAhead) == ' '){
                            for (int j = 0; j < chordStringBuilder.length(); j++) {
                                finalStringBuilder.append(" ");
                                globalPointer++;
                            }
                        }
                    }
                    continue;
                }

                finalStringBuilder.append(currentChar);
                globalPointer++;
            }
                /*char character = line.charAt(charPointer);
                StringBuilder chordStringBuilder = new StringBuilder();
                if(character == '{'){
                    character = line.charAt(++charPointer);
                    int startPosition = charPointer;
                    int endPosition = 0;
                    Log.d("text_final_debug", "generateFormattedText: " + globalPointer);

                    while (character != '}'){
                        finalStringBuilder.append(character);
                        divisionList.add(globalPointer);
                        globalPointer++;
                        endPosition = charPointer;
                        charPointer++;
                        character = line.charAt(charPointer);
                    }


                    //finalStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),endPosition,endPosition,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    charPointer++;
                    character = line.charAt(charPointer);
                }
                if(character == '['){
                    character = line.charAt(++charPointer);
                    while (character != ']'){
                        chordStringBuilder.append(character);
                        charPointer++;
                        character = line.charAt(charPointer);
                    }
                    chordMap.put(globalPointer,chordStringBuilder.toString());
                    charPointer++;
                    character = line.charAt(charPointer);
                }

                finalStringBuilder.append(character);
                globalPointer++;
            }
*/
        }

/*        Log.d("tabulator_final_debug", "generateFormattedText: "+ globalPointer +" "+ finalStringBuilder.toString().length());
        for (Pair<Integer, TabulatorChordStructure> pair : chordMap) {
            Log.d("tabulator_final_debug", "generateFormattedText: " +pair.first  + " " + pair.second.getChord());

        }*/

        StaticLayout initialLayout = new StaticLayout(finalStringBuilder.toString(), myTextPaint, width, alignment, spacingMultiplier, spacingAddition, includePadding);
        initialLineCount = initialLayout.getLineCount();
        Log.d("text_final_debug", "generateFormattedText: length" + initialLineCount);
        return finalStringBuilder;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public void setMode(Mode mode){
        this.mode = mode;
        invalidate();
    }

    public ArrayList<Rect> getChordTouchableRect() {
        return chordTouchableRect;
    }

    public HashMap<Rect, String> getChordTouchableMap() {
        return chordTouchableMap;
    }


    public void setTranspose(int transpose){
        for (Pair<Integer, TabulatorChordStructure> pair : chordMap) {
            String chord = pair.second.getChord().toLowerCase();
            Pattern pattern = Pattern.compile("[A-Za-z]#?m?");
            Matcher matcher = pattern.matcher(chord);
            String key = "";
            if(matcher.find()){
                key = matcher.group();
            }
            String remains = chord.substring(matcher.end());
           // Log.d("transpose_debug", "setTranspose: " + key+ " " + chord.substring(matcher.end()));

            pair.second.setTransposed_chord(StringManipulationHelper.getTransposedChord(key.toLowerCase(),transpose)+remains);
        }
        invalidate();
    }
}
