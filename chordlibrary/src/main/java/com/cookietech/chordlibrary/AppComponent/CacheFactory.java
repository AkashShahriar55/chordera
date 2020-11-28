package com.cookietech.chordlibrary.AppComponent;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Calendar;


public class CacheFactory {
    public enum Format {
        JPG,
        PNG
    }
    private WeakReference<Context> context;

    public CacheFactory(WeakReference<Context> context) {
        this.context = context;
    }

    public void cacheBitmap(Bitmap bitmap,String fileName,String directoryName,Format format) throws Exception{
        String finalFilename = fileName;
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        ContextWrapper cw = new ContextWrapper(context.get());

        File directory = cw.getDir(directoryName, Context.MODE_PRIVATE);

        Log.d("akash_debug", "saveImage: " +fileName);

        switch (format){
            case JPG:
                finalFilename = finalFilename+".jpg";
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case PNG:
                finalFilename = finalFilename+".png";
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
        }

        File file = new File(directory, finalFilename);
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(compressFormat, 100, out);
        out.flush();
        out.close();
    }

    public File cacheBitmapToExternal(Bitmap bitmap,String directoryName,Format format) throws Exception{
        File imageFileFolder = new File(Environment.getExternalStorageDirectory(),directoryName);
        imageFileFolder.mkdir();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.MONTH))
                + fromInt(c.get(Calendar.DAY_OF_MONTH))
                + fromInt(c.get(Calendar.YEAR))
                + fromInt(c.get(Calendar.HOUR_OF_DAY))
                + fromInt(c.get(Calendar.MINUTE))
                + fromInt(c.get(Calendar.SECOND));
        File imageFileName = null;
        if(format == Format.JPG) {
            imageFileName=new File(imageFileFolder, date+".jpg");
            out = new FileOutputStream(imageFileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.d("OutputTest","jpg.......");
        }

        else if(format == Format.PNG){
            imageFileName=new File(imageFileFolder, date + ".png");
            out = new FileOutputStream(imageFileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.d("OutputTest","png.......");
        }


        out.flush();
        out.close();
        // scanPhoto(imageFileName.toString());
        out = null;
        return imageFileName;
    }

    private String fromInt(int i) {
        return String.valueOf(i);
    }


    public Bitmap retrieveBitmap(String bitmapName, String directoryName,Format format) throws Exception{
        ContextWrapper cw = new ContextWrapper(context.get());
        File directory = cw.getDir(directoryName, Context.MODE_PRIVATE);
        switch (format){
            case JPG:
                bitmapName = bitmapName+".jpg";
                break;
            case PNG:
                bitmapName = bitmapName+".png";
                break;
        }
        File mypath = new File(directory, bitmapName);

        return BitmapFactory.decodeStream(new FileInputStream(mypath));
    }

    public JSONObject retrieveJsonFromCache(String fileName, String directoryName) throws Exception{
        ContextWrapper cw = new ContextWrapper(context.get());
        File directory = cw.getDir(directoryName, Context.MODE_PRIVATE);
        File mypath = new File(directory, fileName);
        BufferedReader reader = new BufferedReader(new FileReader(mypath));
        String receivedString = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((receivedString = reader.readLine()) != null){
            stringBuilder.append(receivedString);
        }
        String string = stringBuilder.toString();
        reader.close();
        return new JSONObject(string);
    }

    public JSONObject retrieveJsonFromRaw(String name) throws Exception {
        String jsonString = null;
        InputStream is = context.get().getResources()
                .openRawResource(context.get().getResources()
                        .getIdentifier(name, "raw", context.get().getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String receivedString = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((receivedString = reader.readLine()) != null){
            stringBuilder.append(receivedString);
        }

        jsonString = stringBuilder.toString();
        is.close();
        return new JSONObject(jsonString);
    }


    public boolean isCacheAvailable(String fileName,String directoryName){
        ContextWrapper cw = new ContextWrapper(context.get());
        File directory = cw.getDir(directoryName, Context.MODE_PRIVATE);
        File path = new File(directory, fileName);
        return path.exists();
    }


    public void cacheJson(String json,String filename,String directory) throws Exception{
        ContextWrapper cw = new ContextWrapper(context.get());
        File fileDirectory =  cw.getDir(directory, Context.MODE_PRIVATE);
        File file = new File(fileDirectory, filename);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(json);
        out.flush();
        out.close();
    }


    public void deleteDirectory(String directory){
        ContextWrapper cw = new ContextWrapper(context.get());
        File fileDirectory = cw.getDir(directory, Context.MODE_PRIVATE);
        try {
            FileUtils.deleteDirectory(fileDirectory);
        } catch (IOException e) {
            Log.d("akash_debug", "deleteDirectory: error deleting ");
            e.printStackTrace();
        }
    }

    public void deleteFile(){
        //TODO
    }

}
