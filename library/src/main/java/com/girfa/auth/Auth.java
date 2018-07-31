package com.girfa.auth;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.util.Base64;
import android.util.Log;

import com.girfa.file.Directory;
import com.girfa.file.FileUtils;
import com.girfa.BuildConfig;
import com.girfa.web.WebService;
import com.girfa.file.Document;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.Serializable;
import java.security.MessageDigest;

/**
 * Created by Afrig Aminuddin on 23/11/2016.
 */

public class Auth {
    private static final String NAME = Auth.class.getName();
    private static final String E_FAILED = "Auth failed",
            E_EXPIRED = "Auth expired",
            E_SIGNEDOUT = "Signed out";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Auth() {
    }

    public static Document getProfile() {
        return Directory.getDocument(context, "profile/auth");
    }

    public static void updateProfile(final Callback callback) {
        WebService web = WebService.put("profile/auth");
        web.getRequest().copyFrom(getProfile());
        web.send(new WebService.Callback() {
            @Override
            public void onSuccess(WebService web, Document data) {
                getProfile().copyFrom(data);
                if (callback != null) callback.onSuccess(data);
            }

            @Override
            public void onError(WebService web, Document error) {
                if (callback != null) callback.onError(error.getString("message"));
            }
        });
    }

    public static void checkIn(Context context, Callback callback) {
        Auth.context = context.getApplicationContext();
        Document profile = getProfile();
        if (callback == null) return;
        if (profile.isEmpty()) callback.onError(lastError());
        else callback.onSuccess(profile);
    }

    public static void signIn(Context context, final Document auth, final Callback callback) {
        Auth.context = context.getApplicationContext();
        DeviceName.with(context).request(new DeviceName.Callback() {
            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                WebService web = WebService.post("profile/auth");
                Document req = web.getRequest();
                req.set("brand", info.manufacturer);
                req.set("model", info.getName());
                req.set("os", "Android " + Build.VERSION.RELEASE);
                req.set("auth", auth);
                web.send(new WebService.Callback() {
                    @Override
                    public void onSuccess(WebService web, Document data) {
                        data.copyTo(getProfile());
                        if (callback != null) callback.onSuccess(data);
                    }

                    @Override
                    public void onError(WebService web, Document error) {
                        if (callback != null) callback.onError(E_FAILED);
                        if (BuildConfig.DEBUG) Log.d(NAME, "signIn " + error);
                    }
                });
            }
        });
    }

    public static void signOut() {
        Firebase.signOut();
        getProfile().delete();
        setError(E_SIGNEDOUT);
        WebService.delete("profile/auth").send(null);
        restart();
    }

    public static void expired() {
        Firebase.signOut();
        getProfile().delete();
        setError(E_EXPIRED);
        restart();
    }

    private static void restart() {
        if (context == null) return;
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        ComponentName component = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(component);
        context.startActivity(mainIntent);
    }

    private static String lastError() {
        File file = Directory.getRawFile(context, "profile/error");
        String error = FileUtils.read(file);
        file.delete();
        return error;
    }

    private static void setError(String error) {
        FileUtils.write(error, Directory.getRawFile(context, "profile/error"));
    }

    public static String sha1(String text) {
        try {
            if (text == null) return "NULL";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = text.getBytes("UTF-8");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return Base64.encodeToString(sha1hash, 11);
        } catch (Exception e) {
            return "NULL";
        }
    }

    public interface Callback extends Serializable {
        void onSuccess(Document data);

        void onError(String error);
    }
}
