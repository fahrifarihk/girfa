package com.girfa.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.girfa.auth.Auth;
import com.girfa.BuildConfig;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Afrig Aminuddin on 23/07/2017.
 */

public class Directory {

    public static File getDataDir(Context context) {
        return new File(context.getApplicationInfo().dataDir);
    }

    public static File getWebDir(Context context, String path) {
        File file = new File(new File(getDataDir(context), "web"), Auth.sha1(path));
        file.getParentFile().mkdirs();
        return file;
    }


    public static File getImageDir(Context context, String path) {
        File dir = new File(new File(getDataDir(context), "img"), Auth.sha1(path));
        dir.mkdirs();
        return dir;
    }

    private static Map<String, Document> documents;

    public static Document getDocument(Context context, String path) {
        if (documents == null) documents = new LinkedHashMap<>();
        if (documents.containsKey(path)) return documents.get(path);
        File file = new File(new File(getDataDir(context), "doc"), Auth.sha1(path) + ".json");
        file.getParentFile().mkdirs();
        Document document = new Document(file);
        Directory.documents.put(path, document);
        return document;
    }

    private static Map<String, File> raws;

    public static File getRawFile(Context context, String path) {
        if (raws == null) raws = new LinkedHashMap<>();
        if (raws.containsKey(path)) return raws.get(path);
        File raw = new File(new File(getDataDir(context), "raw"), Auth.sha1(path) + ".raw");
        raw.getParentFile().mkdirs();
        Directory.raws.put(path, raw);
        return raw;
    }
}
