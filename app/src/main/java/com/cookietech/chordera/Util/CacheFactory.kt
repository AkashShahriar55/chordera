package com.cookietech.chordera.Util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.apache.commons.io.FileUtils

import org.json.JSONObject
import java.io.*
import java.lang.ref.WeakReference
import kotlin.jvm.Throws


class CacheFactory(private val context: WeakReference<Context>) {

    @Throws(Exception::class)
    fun retrieveJsonFromCache(fileName: String, directoryName: String): String {
        val cw = ContextWrapper(context.get())
        val directory: File = cw.getDir(directoryName, Context.MODE_PRIVATE)
        val mypath = File(directory, fileName)
        val reader = BufferedReader(FileReader(mypath))
        var receivedString: String? = ""
        val stringBuilder: StringBuilder = StringBuilder()
        while ((reader.readLine().also { receivedString = it }) != null) {
            stringBuilder.append(receivedString)
        }
        val string: String = stringBuilder.toString()
        reader.close()
        return string
    }

    @Throws(Exception::class)
    fun retrieveJsonFromRaw(name: String): JSONObject {
        var jsonString: String? = null
        val inStrm: InputStream = context.get()!!.resources
                .openRawResource(context.get()!!.resources
                        .getIdentifier(name, "raw", context.get()?.packageName))
        val reader: BufferedReader = BufferedReader(InputStreamReader(inStrm, "UTF-8"))
        var receivedString: String? = ""
        val stringBuilder: StringBuilder = StringBuilder()
        while ((reader.readLine().also { receivedString = it }) != null) {
            stringBuilder.append(receivedString)
        }
        jsonString = stringBuilder.toString()
        inStrm.close()
        return JSONObject(jsonString)
    }

    fun isCacheAvailable(fileName: String, directoryName: String): Boolean {
        val cw = ContextWrapper(context.get())
        val directory: File = cw.getDir(directoryName, Context.MODE_PRIVATE)
        val path = File(directory, fileName)
        return path.exists()
    }

    @Throws(Exception::class)
    fun cacheJson(json: String, filename: String, directory: String?) {
        val cw = ContextWrapper(context.get())
        val fileDirectory: File = cw.getDir(directory, Context.MODE_PRIVATE)
        val file = File(fileDirectory, filename)
        val out = BufferedWriter(FileWriter(file))
        out.write(json)
        out.flush()
        out.close()
    }

    @Throws(Exception::class)
    fun deleteDirectory(directory: String?) {
        val cw = ContextWrapper(context.get())
        val fileDirectory: File = cw.getDir(directory, Context.MODE_PRIVATE)
        FileUtils.deleteDirectory(fileDirectory)
    }

    fun deleteFile() {
        //TODO
    }

}