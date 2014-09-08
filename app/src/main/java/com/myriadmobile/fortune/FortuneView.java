package com.myriadmobile.fortune;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cclose on 9/3/14.
 */
public class FortuneView extends View {

    private ArrayList<FortuneItem> fortuneItems = new ArrayList<FortuneItem>();

    double radius;
    double radianOffset = 0;
    GrooveListener grooveListener;
    int lastGrooveIndex = 0;
    Matrix matrix = new Matrix();

    // Settings
    public double spinSensitivity = 1; // Multipler for spin speed. ie .5, half the speed of finger
    public double frameRate = 40; // Frames per second
    public double friction = 5; // Slows down friction radians per second
    public double velocityClamp = 15;  // clamps max fling to radians per second
    public boolean flingable = true; // Decides if the user can fling
    public boolean grooves = true; // Locks at correct angles
    public int notch = 90; // Where the notch is located in degrees
    public float unselectScaleOffset = .8f; // Scale offset of unselected icons
    public float selectScaleOffset = 1f; // Scale offset of the selected icons
    public float distanceScale = 1; // Float from 0 - 1 (should be) to decide how close to the edge the icons show
    public float centripetalPercent = .25f; // Float from 0 - distancePercent amount of Centripetal force affects you
    public int backgroundResourceId = -1; // Resource id of the background
    public float backgroundScale = 1; // Scale of the background image
    public boolean backgroundCentripetalForce = false;  // Does centripetal force act on the background
    public FortuneItem.HingeType backgroundHinge = FortuneItem.HingeType.Fixed; // Background hinge
    public float minimumSize = .5f;


    Canvas mCanvas;

    public FortuneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FortuneView,
                0, 0);

        try {
            spinSensitivity = a.getFloat(R.styleable.FortuneView_spinSensitivity, 1);
            frameRate = a.getFloat(R.styleable.FortuneView_frameRate, 40);
            friction = a.getFloat(R.styleable.FortuneView_friction, 5);
            velocityClamp = a.getFloat(R.styleable.FortuneView_velocityClamp, 15);
            flingable = a.getBoolean(R.styleable.FortuneView_flingable, true);
            grooves = a.getBoolean(R.styleable.FortuneView_grooves, true);
            unselectScaleOffset = a.getFloat(R.styleable.FortuneView_unselectScaleOffset, 1f);
            selectScaleOffset = a.getFloat(R.styleable.FortuneView_selectScaleOffset, 1f);
            notch = a.getInteger(R.styleable.FortuneView_notch, 90);
            distanceScale = a.getFloat(R.styleable.FortuneView_distanceScale, 1);
            centripetalPercent = a.getFloat(R.styleable.FortuneView_centripetalPercent, .25f);
            backgroundResourceId = a.getResourceId(R.styleable.FortuneView_background, -1);
            if(a.getInteger(R.styleable.FortuneView_backgroundHinge, 0) == 0) {
                backgroundHinge = FortuneItem.HingeType.Fixed;
            } else {
                backgroundHinge = FortuneItem.HingeType.Hinged;
            }
            backgroundScale = a.getFloat(R.styleable.FortuneView_backgroundScale, 1);
            backgroundCentripetalForce = a.getBoolean(R.styleable.FortuneView_backgroundCentripetalForce, false);
            minimumSize = a.getFloat(R.styleable.FortuneView_minimumSize, .5f);
        } finally {
            a.recycle();
        }
    }

    public void setGrooveListener(GrooveListener grooveListener) {
        this.grooveListener = grooveListener;
    }

    public void addFortuneItems(List<FortuneItem> items) {
        fortuneItems.addAll(items);
        reconfigure(true);
    }

    public void addFortuneItem(FortuneItem item) {
        fortuneItems.add(item);
        reconfigure(true);
    }


    private void reconfigure(boolean invalidate) {
        if(mCanvas != null) {
            // Calculate size of bitmaps
            int width = mCanvas.getWidth();
            int height = mCanvas.getHeight();
            radius = width > height ? height / 2 : width / 2;
            if (invalidate)
                invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mCanvas == null) {
            mCanvas = canvas;
            reconfigure(false);
        } else {
            mCanvas = canvas;
        }
        super.onDraw(canvas);

        // Offset radians
        double radians = radianOffset;
        double centripitalForceAmount = (centripetalPercent * swipeVelocity.getCentripetalPercent());

        // Add groove notch
        radians -= notch * Math.PI / 180;

        // Add Background to the canvas.
        if(backgroundResourceId != -1) {

            Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), backgroundResourceId);
            int smallSide = (int)((canvas.getWidth() > canvas.getHeight() ? canvas.getHeight() : canvas.getWidth()) *
                    (backgroundScale - (centripitalForceAmount * (backgroundCentripetalForce ? 1 : 0))));

            // Add background image
            int backgroundX = smallSide / 2;
            int backgroundY = smallSide * (backgroundImage.getHeight() / backgroundImage.getWidth()) / 2;

            matrix.reset();
            if(backgroundHinge == FortuneItem.HingeType.Fixed) {
                matrix.postRotate((float)(radians / Math.PI * 180), backgroundImage.getWidth()/2, backgroundImage.getHeight()/2);
            }
            matrix.postScale(smallSide / (float) backgroundImage.getWidth(), smallSide / (float) backgroundImage.getWidth());
            matrix.postTranslate(canvas.getWidth() / 2 - backgroundX, canvas.getHeight() / 2 - backgroundY);

            canvas.drawBitmap(backgroundImage, matrix, null);
        }

        double rad = radius * (distanceScale - centripitalForceAmount);
        for(int i = 0 ; i < fortuneItems.size(); i ++) {
            // Draw dialItem
            radians = fortuneItems.get(i).drawItem(canvas, rad, radians, getTotalValue(), (i == getSelectedIndex() ? selectScaleOffset : unselectScaleOffset), minimumSize);
        }

        // Notify Listener
        if(getSelectedIndex() != lastGrooveIndex) {
            lastGrooveIndex = getSelectedIndex();
            //Log.d("Groove", "Change: " + lastGrooveIndex);
            if(grooveListener != null)
                grooveListener.onGrooveChange(lastGrooveIndex);
        }
    }

    private float getTotalValue() {
        float total = 0;
        for(FortuneItem di : fortuneItems)
            total += di.value;
        return total;
    }

    double radianStart;
    double lastOffset;
    SwipeVelocity swipeVelocity = new SwipeVelocity();

    Handler flingHandler;
    Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if(!swipeVelocity.active) {
                // Slow the velocity and update
                double offset = swipeVelocity.slowVelocityDown();
                //Log.d("Velocity", "Velo: " + swipeVelocity.velocity);
                // Buffer Variable
                if (Math.abs(swipeVelocity.velocity) > friction / frameRate * 4) {
                    radianOffset += offset;
                    invalidate();
                    startFling();
                } else {
                    swipeVelocity.velocity = 0;
                    if(grooves) {
                        // Lock to a groove
                        lockToGroove();
                    }
                }
            }
        }
    };

    private void startFling() {
        // Not manually swiping
        if(!swipeVelocity.active) {
            if(flingHandler == null)
                flingHandler = new Handler();
            else
                flingHandler.removeCallbacks(flingRunnable);
            flingHandler.postDelayed(flingRunnable, (long)(1000/frameRate));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mCanvas != null) {
            double diffX = event.getX() - mCanvas.getWidth()/2;
            double diffY = event.getY() - mCanvas.getHeight()/2;
            double radianNew = Math.atan(Math.abs(diffY/diffX));

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    radianStart = radianNew;
                    lastOffset = radianOffset;
                    swipeVelocity.clear();
                    swipeVelocity.addFlingPoint(new Date().getTime(), radianOffset);
                    return true;
                case MotionEvent.ACTION_UP:
                    //radianOffset = getLockedRadians();
                    if(flingable) {
                        if (Math.abs(swipeVelocity.findVelocity()) > 4) {
                            // Fling
                            startFling();
                        } else {
                            // Can lock
                            if(grooves) {
                                // Lock to a groove
                                lockToGroove();
                            }
                        }
                    } else {
                        // Can lock
                        if(grooves) {
                            // Lock to a groove
                            lockToGroove();
                        }
                    }

                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:

                    if((diffX > 0 && diffY < 0) || ((diffX < 0 && diffY > 0)))
                        radianOffset = lastOffset + (radianStart - radianNew) * spinSensitivity;
                    else
                        radianOffset = lastOffset - (radianStart - radianNew) * spinSensitivity;

                    radianStart = radianNew;
                    lastOffset = radianOffset;
                    swipeVelocity.addFlingPoint(new Date().getTime(), radianOffset);

                    invalidate();
                    return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public class SwipeVelocity {

        public boolean active;
        private double velocity;
        private int start = 0;
        private int end = 0;
        private FlingPoint[] points = new FlingPoint[10];

        public void clear() {
            start = 0;
            end = 0;
        }

        public void addFlingPoint(long time, double offset) {
            points[end] = new FlingPoint(time, offset);
            end ++;
            if(end >= points.length) {
                end = 0;
            }
            if(end == start) {
                start ++;
                if(start >= points.length) {
                    start = 0;
                }
            }
            active = true;
        }

        // Rad per second
        public double findVelocity() {
            int endIndex = end - 1;
            if(endIndex < 0)
                endIndex = points.length - 1;
            velocity = (points[endIndex].offset - points[start].offset) / (points[endIndex].time - points[start].time) * 1000;
            if(velocity > velocityClamp)
                velocity = velocityClamp;
            if(velocity < -velocityClamp)
                velocity = -velocityClamp;
            active = false;
            return velocity;
        }

        // Slow down velocity based on friction
        public double slowVelocityDown() {
            if(velocity > 0)
                velocity -= friction / frameRate;
            else
                velocity += friction / frameRate;

            return velocity / frameRate;
        }

        public class FlingPoint {
            public long time;
            public double offset;
            public FlingPoint(long time, double offset) {
                this.time = time;
                this.offset = offset;
            }
        }

        public float getCentripetalPercent() {
            float velo = (float)Math.abs(velocity);
            if(velo == 0)
                return 0;
            if(velo < 10)
                return velo/10f;
            return 1;
        }


    }

    private void removeAllMotion() {
        swipeVelocity.velocity = 0;
        // Fling
        flingHandler.removeCallbacks(flingRunnable);
        // Lock to groove
        lockToGrooveHandler.removeCallbacks(lockToGrooveRunnable);
    }

    Handler lockToGrooveHandler = new Handler();
    Runnable lockToGrooveRunnable = new Runnable() {
        @Override
        public void run() {
            if(!swipeVelocity.active) {
                // Animate to the correct groove
                double correctOffset = getLockedRadians();
                double diff = correctOffset - radianOffset;
                if (Math.abs(diff) > 2 / frameRate) {
                    if(diff > 0)
                        diff = 2 / frameRate;
                    else
                        diff = -2 / frameRate;
                    radianOffset += diff;
                    invalidate();
                    lockToGroove();
                } else {
                    radianOffset += diff;
                    invalidate();
                }
            }
        }
    };

    public int getSelectedIndex() {
        if(fortuneItems.size() == 0)
            return 0;

        double offset = radianOffset;
        if(offset < 0) {
            offset -= Math.PI / fortuneItems.size();
        } else {
            offset += Math.PI / fortuneItems.size();
        }
        int index = ((int)(-offset / (Math.PI * 2 / fortuneItems.size()))) % fortuneItems.size();
        if(index < 0)
            index += fortuneItems.size();
        return index;
    }

    private void lockToGroove() {
        // Not manually swiping
        if(!swipeVelocity.active) {
            if(lockToGrooveHandler == null)
                lockToGrooveHandler = new Handler();
            else
                lockToGrooveHandler.removeCallbacks(lockToGrooveRunnable);
            lockToGrooveHandler.postDelayed(lockToGrooveRunnable, (long)(1000/frameRate));
        }
    }

    private double getLockedRadians() {
        // Lock to the nearest lockAtRadians
        double lockAtRadians = Math.PI * 2 / fortuneItems.size();
        double targetOffset = radianOffset;
        double diff = Math.abs(targetOffset) % lockAtRadians;
        if((targetOffset/lockAtRadians) - Math.floor(targetOffset/lockAtRadians) > .5) {
            targetOffset += diff;
        } else {
            targetOffset -= diff;
        }
        return targetOffset;
    }

    public void setSelectedItem(int index) {
        if(index < 0 || index >= fortuneItems.size() || index == getSelectedIndex())
            return;

        // Remove all other runnables affecting the wheel
        removeAllMotion();

        // Find the needed offset for the icon
        double targetOffset = positionOnWheel(index);


        // Find offset from the current position
        double offset = targetOffset - (radianOffset % (Math.PI * 2));
        double velocity;
        if(offset > Math.PI) {
            offset -= Math.PI * 2;
            velocity =  -Math.sqrt(Math.abs(2*friction*offset));
        } else if(offset > 0) {
            velocity =  Math.sqrt(Math.abs(2*friction*offset));
        } else if(offset < -Math.PI && offset > -Math.PI * 2){
            offset += Math.PI * 2;
            velocity = Math.sqrt(Math.abs(2 * friction * offset));
        } else if(offset < -Math.PI * 2){
            offset += Math.PI * 2;
            velocity = -Math.sqrt(Math.abs(2 * friction * offset));
        } else {
            velocity = -Math.sqrt(Math.abs(2 * friction * offset));
        }

        // Start Flinging
        swipeVelocity.velocity = velocity;
        startFling();
    }

    // Returns the position of the notch for an index
    private double positionOnWheel(int index) {
        double total = 0;
        for(int i = 0 ; i < index; i ++) {
            total -= fortuneItems.get(i).value/getTotalValue() * Math.PI * 2;
        }
        return total;
    }

    public int getTotalItems() {
        return fortuneItems.size();
    }
}
