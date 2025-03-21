package com.cookietech.chordera.chordDisplay.chordFormatter


import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.lang.Exception
import java.lang.StringBuilder


class ChordFormater(var lyricsWithChord: String, var rootWidth: Int) {
    var lyrics: String
    var sb = SpannableStringBuilder()
    init {
        lyrics = getLyrics(lyricsWithChord);
    }


    fun getLyrics(lyricsWithChord: String): String {
        var ly = lyricsWithChord.replace("\\[(.*?)\\]".toRegex(),"").replace("\\n","\n")
        ly =  "$ly     \n"
        // wraping the lyrics as per the textView size so that they won't break in middle of any word
        var wrapLyrics = wrapText(rootWidth,ly)
        sb.append(wrapLyrics)
        return wrapLyrics
    }

    fun wrapText(textViewWidth: Int, ly: String): String{
        var temp = ""
        var sentence = ""
        val array = ly.split(" ".toRegex()).toTypedArray() // split by space
        for(word in array) {
            if(temp.length + word.length < textViewWidth) {
                temp += " $word"
            } else {
                sentence += temp + "\n"
                temp = word
            }
        }

        return  sentence.replaceFirst(" ".toRegex(),"") + temp
    }

    fun getProcessedChord(start: Int) : SpannableStringBuilder {
        try{
            val length = lyricsWithChord.length
            var i = 0
            var j = 0
            while (i < length) {
                var c = lyricsWithChord[i]
                val st = StringBuilder()
                if(c == '{'){
                    val startPoint = ++i
                    c = lyricsWithChord[startPoint]
                    while (c != '}') {
                        st.append(c)
                        i++
                        c = lyricsWithChord[i]
                    }
                    val end = i;
                    i++
                    sb.setSpan(ForegroundColorSpan(Color.parseColor("#00D49A")), startPoint, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                /*if (c == '[') {
                    c = lyricsWithChord[++i]
                    while (c != ']') {
                        st.append(c)
                        i++
                        c = lyricsWithChord[i]
                    }
                    i++
                    sb.setSpan(ChordSpan(st.toString()), start + j + 1, start + j + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }*/
                i++
                j++
            }
        }catch (error:Exception){
            Log.d("tab_view_debug", "getProcessedChord: "+error.localizedMessage)
        }

        return sb
    }

}