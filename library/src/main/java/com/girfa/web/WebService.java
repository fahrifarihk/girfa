package com.girfa.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.girfa.BuildConfig;
import com.girfa.auth.Auth;
import com.girfa.file.FileUtils;
import com.girfa.file.Document;
import com.girfa.file.Directory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Afrig Aminuddin on 27/03/2017.
 */

public class WebService {
    private static final String NAME = WebService.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final String method;
    private final URL url;
    private final Document request;
    private final Document response;

    public static void initialize(Context context, String apiUrl) {
        WebService.context = context.getApplicationContext();
        FileUtils.write(apiUrl, Directory.getRawFile(context, "web/url"));
    }

    private static String apiUrl() {
        return FileUtils.read(Directory.getRawFile(context, "web/url"));
    }

    public static WebService get(String path) {
        return new WebService("GET", path);
    }

    public static WebService post(String path) {
        return new WebService("POST", path);
    }

    public static WebService put(String path) {
        return new WebService("PUT", path);
    }

    public static WebService delete(String path) {
        return new WebService("DELETE", path);
    }

    private WebService(String method, String path) {
        if (context == null) {
            throw new ExceptionInInitializerError("WebService is not initialized yet");
        }
        this.method = method;
        this.url = initURL(apiUrl() + "/" + path);
        File dir = Directory.getWebDir(context, url.getPath());
        String file = Auth.sha1(this.url.getQuery()) + "." + method;
        this.request = new Document(new File(dir, file + ".req.json"));
        this.response = new Document(new File(dir, file + ".resp.json"));
    }

    private URL initURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Document getRequest() {
        return request;
    }

    public Document getResponse() {
        return response;
    }

    public Document getData() {
        return response.getDocument("data");
    }

    public Document getError() {
        return response.getDocument("error");
    }

    public void send(Callback callback) {
        new Task(callback).execute();
    }

    public void delete() {
        request.delete();
        response.delete();
    }

    private class Task extends AsyncTask<Void, Void, Document> {
        private final Callback callback;
        private int code;

        private Task(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Document doInBackground(Void... voids) {
            HttpURLConnection con = null;
            try {
                if (BuildConfig.DEBUG) {
                    Log.d(NAME, method + " " + url.toString());
                    Log.d(NAME, method + ".Request " + request.toString(4));
                }
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);
                con.setRequestMethod(method);
                con.setRequestProperty("Authorization", "Bearer " + getAuth());
                if (request.size() > 0) {
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-type", "application/json");
                    request.write(con.getOutputStream());
                }
                con.connect();
                code = con.getResponseCode();
                InputStream stream = con.getErrorStream();
                String data = null;
                try {
                    if (stream == null && code == 200) {
                        response.clear();
                        stream = con.getInputStream();
                    }
                    data = FileUtils.read(stream);
                    response.read(data);
                    setAuth(response.getString("auth"));
                } catch (Document.DocumentException e) {
                    error(data);
                } finally {
                    if (stream != null) stream.close();
                }
            } catch (SocketTimeoutException e) {
                error("Slow internet connection");
            } catch (UnknownHostException e) {
                error("No internet connection");
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
                error(e.getClass().getSimpleName() + " " + e.getMessage());
            } finally {
                if (con != null) con.disconnect();
            }
            if (BuildConfig.DEBUG) {
                Log.d(NAME, method + ".Response " + response.toString(4));
            }
            return response;
        }

        private String getAuth() {
            return FileUtils.read(Directory.getRawFile(context, "web/auth"));
        }

        private void setAuth(String auth) {
            if (auth != null) {
                FileUtils.write(auth, Directory.getRawFile(context, "web/auth"));
            }
        }

        private void error(String message) {
            response.set("error.code", code);
            response.set("error.message", message);
        }

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            if (callback != null) {
                if (response.has("error")) {
                    callback.onError(WebService.this, response.getDocument("error"));
                } else {
                    callback.onSuccess(WebService.this, response.getDocument("data"));
                }
            }
            if (code == 403) {
                Auth.expired();
            }
        }
    }

    public interface Callback {
        void onSuccess(WebService web, Document data);

        void onError(WebService web, Document error);
    }
}
