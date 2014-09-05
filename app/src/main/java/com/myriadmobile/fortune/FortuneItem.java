package com.myriadmobile.fortune;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by cclose on 9/3/14.
 */
public class FortuneItem {

    public Bitmap image;
    public int color;
    public enum DialItemType {Image, Section};
    public enum HingeType {Fixed, Hinged};
    public DialItemType type;
    public HingeType hinge = HingeType.Hinged;
    public float value;
    Matrix matrix = new Matrix();

    public FortuneItem(Bitmap image) {
        this.image = image;
        type = DialItemType.Image;
        value = 1;
    }

    public FortuneItem(Bitmap image, HingeType hinge) {
        this.hinge = hinge;
        this.image = image;
        type = DialItemType.Image;
        value = 1;
    }

    public FortuneItem(int color, int value) {
        this.color = color;
        type = DialItemType.Section;
        this.value = value;
    }

    public double drawItem(Canvas canvas, double radius, double radians, float totalValue, float sizeMultipler, float miniumSize) {

        double incrementRadians = Math.PI * 2 * (value/totalValue);

        double circum = Math.PI * radius * 2;
        double dialogalSize = circum * (value/totalValue);
        double radAspect = Math.atan(image.getHeight()/(double)image.getWidth());
        int imageNewWidth = (int)(dialogalSize * Math.cos(radAspect) * sizeMultipler);

        // Is the imagewidth to big?
        if(radius > (canvas.getHeight() > canvas.getWidth() ? canvas.getWidth() : canvas.getHeight())/2) {
            // Radius is to big!
            double diff = radius - ((canvas.getHeight() > canvas.getWidth() ? canvas.getWidth() : canvas.getHeight())/2);
            Log.d("Radius Bigger", "Diff: " + diff);

            if(imageNewWidth - diff * 2 < imageNewWidth * miniumSize) {
                imageNewWidth = imageNewWidth/2;
                radius = ((canvas.getHeight() > canvas.getWidth() ? canvas.getWidth() : canvas.getHeight())/2);
            } else {
                imageNewWidth -= diff * 2;
                radius = ((canvas.getHeight() > canvas.getWidth() ? canvas.getWidth() : canvas.getHeight())/2);
            }
        }

        if(type == DialItemType.Image) {
            // Center of circle placement
            int centerX = (int) (Math.cos(radians) * (radius - imageNewWidth/2));
            int centerY = (int) (Math.sin(radians) * (radius - imageNewWidth/2));

            int bmpCenterX = imageNewWidth / 2;
            int bmpCenterY = imageNewWidth * (image.getHeight() / image.getWidth()) / 2;

            matrix.reset();
            if(hinge == HingeType.Fixed) {
                matrix.postRotate((float)(radians / Math.PI * 180), image.getWidth()/2, image.getHeight()/2);
            }
            matrix.postScale(imageNewWidth / (float) image.getWidth(), imageNewWidth / (float) image.getWidth());
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
