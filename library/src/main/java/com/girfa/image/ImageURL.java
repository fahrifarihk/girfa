package com.girfa.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.girfa.file.Directory;
import com.girfa.file.Document;
import com.girfa.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

/**
 * Created by Afrig Aminuddin on 2/17/2017.
 */

public class ImageURL {
    private static final String NAME = ImageURL.class.getSimpleName();

    private ImageURL() {}

    public static void request(ImageView imageView, String url) {
        if (imageView == null || url == null) return;
        Task task = new Task(imageView, url, Type.RECTANGLE);
        post(imageView, task);
    }

    public static void reload(ImageView imageView, String url) {
        if (imageView == null || url == null) return;
        Task task = new Task(imageView, url, Type.RECTANGLE);
        task.clearCache();
        post(imageView, task);
    }

    public static void requestCircle(ImageView imageView, String url) {
        if (imageView == null || url == null) return;
        Task task = new Task(imageView, url, Type.CIRCLE);
        post(imageView, task);
    }

    public static void reloadCircle(ImageView imageView, String url) {
        if (imageView == null || url == null) return;
        Task task = new Task(imageView, url, Type.CIRCLE);
        task.clearCache();
        post(imageView, task);
    }

    private static void post(ImageView imageView, final Task task) {
        if (task.image.exists()) {
            task.imageView.setImageBitmap(BitmapFactory.decodeFile(task.image.getAbsolutePath()));
        } else {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    task.execute();
                }
            });
        }
    }

    private static class Task extends AsyncTask<Void, Void, Bitmap> {
        private final ImageView imageView;
        private final String url;
        private final Type type;
        private final File dir;
        private final File raw;
        private final File image;
        private final Document cache;
        private int max;

        private Task(ImageView imageView, String url, Type type) {
            this.imageView = imageView;
            this.url = url;
            this.type = type;
            this.dir = Directory.getImageDir(imageView.getContext(), url);
            this.raw = new File(dir, "raw.jpg");
            StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
            this.image = new File(dir, sha1(ste.getClassName()
                    + ste.getLineNumber()) + "." + type.ordinal() + type.extension);
            this.cache = new Document(new File(dir, "cache.json"));

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.max = Math.max(imageView.getWidth(), imageView.getHeight());
        }

        private void request() {
            try {
                Date expires = new Date(cache.getLong("expires"));
                if (expires.after(new Date())) {
                    return;
                }
                if (BuildConfig.DEBUG) Log.d(NAME, "request " + url);
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                if (cache.has("modified"))
                    con.setRequestProperty("If-Modified-Since", cache.getString("modified"));
                if (cache.has("etag"))
                    con.setRequestProperty("If-None-Match", cache.getString("etag"));
                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);
                con.connect();
                if (con.getResponseCode() == 200) {
                    if (!raw.getParentFile().exists()) raw.getParentFile().mkdirs();
                    if (!raw.exists()) raw.createNewFile();
                    copy(con.getInputStream(), new FileOutputStream(raw));
                    cache.set("expires", con.getExpiration());
                    cache.set("modified", con.getHeaderField("Last-Modified"));
                    cache.set("etag", con.getHeaderField("ETag"));
                    if (BuildConfig.DEBUG) Log.d(NAME, "cache " + cache.toString());
                    clearScaled(false);
                } else if  (con.getResponseCode() == 304 && !raw.exists()) {
                    clearCache();
                    request();
                } else if (BuildConfig.DEBUG) {
                    Log.d(NAME, "code " + con.getResponseCode());
                }
                con.disconnect();
            } catch (Exception e) {
                clearScaled(true);
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }

        private void scale() {
            try {
                if (!raw.exists()) return;
                if (BuildConfig.DEBUG) Log.d(NAME, "scale " + max);
                Bitmap rawImage = BitmapFactory.decodeFile(raw.getAbsolutePath());
                OutputStream os = new FileOutputStream(image);
                switch (type) {
                    case CIRCLE:
                        scaleCircle(rawImage, os);
                        break;
                    default:
                        scaleRectangle(rawImage, os);
                        break;
                }
                os.flush();
                os.close();
            } catch (Exception e) {
                clearScaled(true);
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }

        private void scaleRectangle(Bitmap input, OutputStream os) {
            int width = input.getWidth();
            int height = input.getHeight();
            float ratio = Math.max((float) max / width, (float) max / height);
            width = ratio < 1 ? Math.round(ratio * width) : width;
            height = ratio < 1 ? Math.round(ratio * height) : height;
            Bitmap output = Bitmap.createScaledBitmap(input, width, height, true);
            output.compress(input.hasAlpha() ? Bitmap.CompressFormat.PNG
                    : Bitmap.CompressFormat.JPEG, 75, os);
        }

        private void scaleCircle(Bitmap input, OutputStream os) {
            int width = input.getWidth();
            int height = input.getHeight();
            int side = Math.min(width, height);
            float ratio = Math.min(1f, Math.max((float) max / width, (float) max / height));
            Matrix matrix = new Matrix();
            matrix.postScale(ratio, ratio);
            Bitmap in = Bitmap.createBitmap(input, (width - side) / 2,
                    (height - side) / 2, side, side, matrix, true);
            int diameter = (int) (side * ratio);
            BitmapShader shader = new BitmapShader(in, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);
            RectF rect = new RectF(0, 0, diameter, diameter);
            Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(rect, diameter / 2, diameter / 2, paint);
            output.compress(Bitmap.CompressFormat.PNG, 75, os);
        }

        private void clearScaled(boolean includeRaw) {
            File[] files = dir.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.getName().endsWith(".json")) continue;
                if (!includeRaw && file.equals(raw)) continue;
                file.delete();
            }
        }

        private boolean clearCache() {
            return cache.delete();
        }

        private String sha1(String text) {
            try {
                if (text == null) return "NULL";
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] textBytes = text.getBytes("UTF-8");
                md.update(textBytes, 0, textBytes.length);
                byte[] sha1hash = md.digest();
                return Base64.encodeToString(sha1hash, 11);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (max == 0) return null;
            if (!image.exists()) {
                request();
                scale();
            }
            return BitmapFactory.decodeFile(image.getAbsolutePath());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (imageView != null && result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    public static void copy(InputStream src, OutputStream dest) throws IOException {
        byte[] buffer = new byte[4096];
        int n;
        while ((n = src.read(buffer)) != -1) {
            dest.write(buffer, 0, n);
        }
        dest.close();
        src.close();
    }

    private enum Type {
        RECTANGLE(".jpg"), CIRCLE(".png");

        String extension;
        private Type(String extension) {
            this.extension = extension;
        }
    }
}
