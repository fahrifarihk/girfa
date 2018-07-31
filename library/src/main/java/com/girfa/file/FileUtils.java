package com.girfa.file;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by Afrig Aminuddin on 23/07/2017.
 */

public class FileUtils {

    public static String read(File fileSource) {
        try {
            if (!fileSource.exists()) return null;
            return read(new FileInputStream(fileSource));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String read(InputStream source) {
        try {
            StringBuilder content = new StringBuilder();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = source.read(buffer)) != -1) {
                content.append(new String(buffer, 0, n));
            }
            source.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void write(String textSource, File fileDestination) {
        try {
            if (textSource == null) fileDestination.delete();
            write(textSource, new FileOutputStream(fileDestination));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(String textSource, OutputStream destination) {
        try {
            if (textSource == null) return;
            PrintWriter pw = new PrintWriter(destination);
            pw.write(textSource);
            pw.close();
            destination.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void delete(File file) {
        if (file != null) file.delete();
    }

    public static void copy(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[4096];
        int n;
        while ((n = source.read(buffer)) != -1) {
            destination.write(buffer, 0, n);
        }
        destination.close();
        source.close();
    }
}
