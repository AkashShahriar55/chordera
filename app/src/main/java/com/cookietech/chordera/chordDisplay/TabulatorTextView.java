package com.cookietech.chordera.chordDisplay;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.cookietech.chordera.R;
import com.cookietech.chordera.models.TabulatorChordStructure;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TabulatorTextView extends androidx.appcompat.widget.AppCompatTextView {
    StringBuilder tabStringBuilder = new StringBuilder();
    Paint highLightPaint = new Paint();
    TextView dummyTextView;

    StaticLayout staticLayout;
    ArrayList<Pair<Integer,TabulatorChordStructure>> chordMap = new ArrayList<>();
    ArrayList<Integer> blankSpace = new ArrayList<>();
    Rect chordBackgroundRect = new Rect();
    RectF tempRectF = new RectF();
    Paint chordBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint chordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float extraSpace = dpToPx(10);
    float textHeight = 0;
    Rect chordPositionRect = new Rect();
    Rect measurementRect = new Rect();
    private int initialLineCount;
    private int textColor;
    private Mode mode;
    TextPaint myTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

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
        textColor = getResources().getColor(R.color.colorPrimary);
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


        textHeight = getPaint().descent() - getPaint().ascent();
        float totalHeight = initialLineCount * textHeight + ((textHeight +extraSpace) * (initialLineCount+1));
        int minh = (int) (totalHeight + getPaddingBottom() + getPaddingTop());
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);
        Log.v("tabulator onMeasure h", MeasureSpec.toString(minh)+" " + h);
        Log.d("tabulator_final_debug", "onMeasure: " + h + " " + initialLineCount);
        super.setMeasuredDimension(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        setTextColor(Color.TRANSPARENT);
        Log.d("Tabulator_final", "onDraw: ");
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
            Log.d("tabulator", "onDraw: null");
            return;
            //todo fix the bug
        }
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
            Log.d("edit_text_test " ,"on Draw");
        }

        chordPaint.setTextSize(getPaint().getTextSize());
        chordPaint.setTextAlign(Paint.Align.LEFT);
        chordPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
        float lastPositionRight = 0;
        int lastBoxPositionTop = -1;
        for (Pair<Integer, TabulatorChordStructure> pair : chordMap) {
            if (layout == null) { // Layout may be null right after change to the text view
                // Do nothing
            }
            String chord = pair.second.getChord();
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
            canvas.drawRoundRect(tempRectF,5,5,chordBackgroundPaint);
            canvas.drawText(pair.second.getChord(),
                    xCoordinate,
                    // The text will not be clipped anymore
                    // You can add a padding here too, faster than string string concatenation
                    yCoordinate ,
                    chordPaint);
        }
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

        int width = getMeasuredWidth();

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

                    chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer-(4*chordStringBuilder.length()),new TabulatorChordStructure(true,chordStringBuilder.toString())));
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
                        chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer-1,new TabulatorChordStructure(false,chordStringBuilder.toString())));
                        Log.d("tabulator_final_debug", "test : " +globalPointer  + " " + chordStringBuilder.toString());
                    }else if(++lookAhead < line.length()){
                        chordMap.add(new Pair<Integer, TabulatorChordStructure>(globalPointer,new TabulatorChordStructure(false,chordStringBuilder.toString())));
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


    public void setCustomColor(int color) {
        textColor = color;
        invalidate();
    }

    public void setMode(Mode mode){
        this.mode = mode;
        invalidate();
    }
}
