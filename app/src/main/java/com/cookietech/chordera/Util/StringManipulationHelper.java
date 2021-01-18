package com.cookietech.chordera.Util;

import static com.cookietech.chordera.application.AppSharedComponents.major_key_circle;
import static com.cookietech.chordera.application.AppSharedComponents.minor_key_circle;

public class StringManipulationHelper {


    public static String getTransposedChord(String key,int transposeValue){
        String actualKey = "";
        int currentKeyPosition;
        int transposedPosition;
        int key_value_for_transpose;
        for (String value:major_key_circle){
            if(value.equalsIgnoreCase(key)){
                currentKeyPosition = major_key_circle.indexOf(value);
                transposedPosition = currentKeyPosition+transposeValue;
                key_value_for_transpose = transposedPosition>=0? (Math.abs(transposedPosition)%12):(12-Math.abs(transposedPosition)%12);
                actualKey = major_key_circle.get(key_value_for_transpose);
            }

        }
        for (String value:minor_key_circle){
            if(value.equalsIgnoreCase(key)){
                currentKeyPosition = minor_key_circle.indexOf(value);
                transposedPosition = currentKeyPosition+transposeValue;
                key_value_for_transpose = transposedPosition>=0? (Math.abs(transposedPosition)%12):(12-Math.abs(transposedPosition)%12);
                actualKey = minor_key_circle.get(key_value_for_transpose);
            }
        }

        return actualKey;
    }
}
