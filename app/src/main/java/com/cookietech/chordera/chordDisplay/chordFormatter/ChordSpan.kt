package com.cookietech.chordera.chordDisplay.chordFormatter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.text.style.ReplacementSpan
import android.util.Log

class ChordSpan(var chord: String) : ReplacementSpan() {
    override fun draw(
        canvas: Canvas,
        text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val space = fm.ascent - fm.descent + fm.leading
        val testTextSize = paint.textSize
        val bounds = Rect()
        //paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        val desiredTextSize = (testTextSize * 0.8).toFloat()

        // Set the paint for that size.
        paint.textSize = desiredTextSize
        paint.color = Color.BLUE
        canvas.drawText(chord, x, y + space.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        fm: FontMetricsInt?
    ): Int {
        if (fm != null) {
            val space = paint.getFontMetricsInt(fm)
            fm.ascent -= space
            fm.top -= space
        }
        return Math.round(paint.measureText(text, start, end))
    }

}