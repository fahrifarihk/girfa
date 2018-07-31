package com.girfa.image;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by afrig-pc on 1/11/2018.
 */

public class BitmapUtils {

    public static Bitmap circle(Bitmap input, int diameter) {
        Bitmap square = square(input, diameter);
        BitmapShader shader = new BitmapShader(square, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rect = new RectF(0, 0, diameter, diameter);
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(rect, diameter / 2, diameter / 2, paint);
        return output;
    }

    public static Bitmap square(Bitmap input, int side) {
        return rectangle(input, side, side);
    }

    public static Bitmap rectangle(Bitmap input, int width, int height) {
        return ThumbnailUtils.extractThumbnail(input, width, height);
    }

    public static Bitmap scale(Bitmap input, float ratio) {
        int width = (int) (input.getWidth() * ratio);
        int height = (int) (input.getHeight() * ratio);
        return Bitmap.createScaledBitmap(input, width, height, true);
    }

    public static String base64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(bitmap.hasAlpha() ? Bitmap.CompressFormat.PNG
                : Bitmap.CompressFormat.JPEG, 75, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
