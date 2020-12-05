package com.cookietech.chordlibrary;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class ChordFactory {
    private Context context;
    private ArrayList<Root> roots = new ArrayList<>();


    public ChordFactory(Context context) {
        this.context = context;
    }

    public ArrayList<Root> getRoots(){
        String jsonString = "";
        jsonString = readJsonStringFromAsset();
        try {
            JSONObject rootJsonObject = new JSONObject(jsonString);
            Iterator<String> keys = rootJsonObject.keys();

            roots = new ArrayList<>();

            while (keys.hasNext()){
                String rootKey = keys.next();
                JSONArray classJsonArray = rootJsonObject.getJSONArray(rootKey);
                ArrayList<ChordClass> chordClasses = new ArrayList<>();
                chordClasses = decodeChordClasses(classJsonArray);
                Root root = new Root(rootKey, chordClasses);
                roots.add(root);

            }
        } catch (JSONException e) {
            Log.d("akash_debug", "getRoots: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return roots;
    }

    private ArrayList<ChordClass> decodeChordClasses(JSONArray classJsonArray) throws JSONException {
        ArrayList<ChordClass> chordClasses = new ArrayList<>();
        for (int i = 0; i < classJsonArray.length(); i++) {
            JSONObject classJsonObject = classJsonArray.getJSONObject(i);
            String className = classJsonObject.getString("class");
            String name = classJsonObject.getString("name");
            JSONArray chordsJsonArray = classJsonObject.getJSONArray("variation");
            ArrayList<Variation> chords = new ArrayList<>();
            chords = decodeChords(chordsJsonArray);
            ChordClass chordClass = new ChordClass(className,name, chords);
            chordClasses.add(chordClass);
        }
        return chordClasses;
    }

    private ArrayList<Variation> decodeChords(JSONArray chordsJsonArray) throws JSONException {
        ArrayList<Variation> chords = new ArrayList<>();
        for (int i = 0; i < chordsJsonArray.length(); i++) {
            JSONObject chordJsonObject = chordsJsonArray.getJSONObject(i);
            ArrayList<Integer> notesArray = new ArrayList<Integer>();
            JSONArray notesJsonArray = chordJsonObject.getJSONArray("notes");
            for (int j = 0; j < notesJsonArray.length(); j++) {
                int notes = notesJsonArray.getInt(j);
                notesArray.add(notes);
            }
            ArrayList<Integer> fingersArray = new ArrayList<Integer>();
            JSONArray fingersJsonArray = chordJsonObject.getJSONArray("fingers");
            for (int j = 0; j < fingersJsonArray.length(); j++) {
                int fingers = fingersJsonArray.getInt(j);
                fingersArray.add(fingers);
            }
            Variation chord = new Variation(notesArray,fingersArray);
            chords.add(chord);
        }
        return chords;
    }

    private String readJsonStringFromAsset() {
        String jsonString = "";
        InputStream is = context.getResources()
                .openRawResource(context.getResources()
                        .getIdentifier("chordera_chord_database", "raw", context.getPackageName()));
        char[] buffer = new char[1024];
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String receivedString = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((receivedString = reader.readLine()) != null){
                stringBuilder.append(receivedString);
            }

            jsonString = stringBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }


}
