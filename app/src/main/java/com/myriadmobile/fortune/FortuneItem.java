package com.myriadmobile.fortune;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by cclose on 9/3/14.
 */
public class FortuneItem {

    public Bitmap image;
    public int color;
    public enum DialItemType {Image, Section};
    public DialItemType type;
    public float value;
    Matrix matrix = new Matrix();

    public FortuneItem(Bitmap image) {
        this.image = image;
        type = DialItemType.Image;
        value = 1;
    }

    public FortuneItem(int color, int value) {
        this.color = color;
        type = DialItemType.Section;
        this.value = value;
    }

    public double drawItem(Canvas canvas, double radius, double radians, float totalValue) {

        double incrementRadians = Math.PI * 2 * (value/totalValue);

        double circum = Math.PI * radius * 2;
        double dialogalSize = circum * (value/totalValue);
        double radAspect = Math.atan(image.getHeight()/(double)image.getWidth());
        int imageSizeX = (int)(dialogalSize * Math.cos(radAspect));

        if(type == DialItemType.Image) {
            // Center of circle placement
            int centerX = (int) (Math.cos(radians) * (radius - imageSizeX/2));
            int centerY = (int) (Math.sin(radians) * (radius - imageSizeX/2));

            int bmpCenterX = imageSizeX / 2;
            int bmpCenterY = imageSizeX * (image.getHeight() / image.getWidth()) / 2;

            matrix.reset();

            matrix.postScale(imageSizeX / (float) image.getWidth(), imageSizeX / (float) image.getWidth());
            matrix.postTranslate(canvas.getWidth() / 2 + centerX - bmpCenterX, canvas.getHeight() / 2 + centerY - bmpCenterY);

            canvas.drawBitmap(image, matrix, null);
        } else {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setAntiAlias(true);

            RectF rectF = new RectF(canvas.getWidth()/2 - (float)radius,canvas.getHeight()/2 - (float)radius,canvas.getWidth()/2 + (float)radius,canvas.getHeight()/2 + (float)radius);
            canvas.drawArc(rectF, radToDeg(radians - incrementRadians/2), radToDeg(radians + incrementRadians/2) - radToDeg(radians - incrementRadians/2), true, paint);

        }
        // Increment radians
        radians += incrementRadians;

        return radians;
    }

    public float radToDeg(double rad) {
        return (float)(rad / Math.PI * 180);
    }

}
