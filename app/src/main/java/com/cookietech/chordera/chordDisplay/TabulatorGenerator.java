package com.cookietech.chordera.chordDisplay;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;

import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

public class TabulatorGenerator {
    SpannableStringBuilder finalStringBuilder = new SpannableStringBuilder();
    StaticLayout staticLayout;

    public SpannableStringBuilder generateFormattedText(String text){
        text = text.replace("\\n","\n");

        TextPaint normalTextPaint = new TextPaint();
        normalTextPaint.setAntiAlias(true);
        normalTextPaint.setTextSize(dpToPx(14));
        normalTextPaint.setColor(Color.BLACK);

        int width = 200;
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        float spacingMultiplier = 1;
        float spacingAddition = 0;
        boolean includePadding = false;
        StaticLayout layout = new StaticLayout(text, normalTextPaint, width, alignment, spacingMultiplier, spacingAddition, includePadding);
        int globalPointer = 0;
        for (int i = 0; i < layout.getLineCount(); i++) {
            final int start = layout.getLineStart(i);
            final int end = layout.getLineEnd(i);

            String line = text.substring(start, end);
            for (int charPointer = 0; charPointer < line.length(); charPointer++) {
                char character = line.charAt(charPointer);
                if(character == '{'){
                    character = line.charAt(++charPointer);
                    int startDivision = globalPointer;
                    int endDivision = globalPointer;
                    while (character != '}'){
                        endDivision = globalPointer;
                        finalStringBuilder.append(character);
                        globalPointer++;
                        charPointer++;
                        character = line.charAt(charPointer);
                    }
                    /*if(charPointer != end){
                        finalStringBuilder.append("\n");
                        globalPointer++;
                    }*/
                    charPointer++;
                    character = line.charAt(charPointer);
                    Log.d("tabulator_debug", "generateFormattedText: " + globalPointer);
                    finalStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),startDivision,endDivision+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                finalStringBuilder.append(character);
                globalPointer++;
            }


        }

        return finalStringBuilder;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
