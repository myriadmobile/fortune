package com.myriadmobile.fortune;

import android.os.Handler;
import android.view.MotionEvent;

import java.util.Date;

/**
 * Created by cclose on 9/8/14.
 */
public class SwipeController {

    // Settings
    private double velocityClamp;
    private double friction;
    private double frameRate;
    private RedrawListener redrawListener;
    private boolean grooves;
    private boolean flingable;
    private double spinSensitivity;

    private boolean userActive;
    private double velocity;
    private int start = 0;
    private int end = 0;
    private FlingPoint[] points = new FlingPoint[10];
    private double radianOffset;
    private int totalItems;

    double radianStart;
    double lastOffset;
    Handler flingHandler;

    public SwipeController(RedrawListener redrawListener, double velocityClamp, double friction, int frameRate, boolean grooves, boolean flingable, double spinSensitivity) {
        this.velocityClamp = velocityClamp;
        this.friction = friction;
        this.frameRate = frameRate;
        this.redrawListener = redrawListener;
        this.grooves = grooves;
        this.flingable = flingable;
        this.spinSensitivity = spinSensitivity;
    }

    /**
     * Calculates the percentage the centripetal force acts on wheel
     * @return 0-1 where 0 is no force and 1 is max force
     */
    public float getCentripetalPercent() {
        float velo = (float)Math.abs(velocity);
        if(velo == 0)
            return 0;
        if(velo < 10)
            return velo/10f;
        return 1;
    }

    /**
     * @return The wheels offset from 0 (not from a notch or starting position)
     */
    public double getRadianOffset() {
        return radianOffset;
    }

    /**
     * This is to set the total items which is used in calculating the location of grooves
     * @param totalItems total items in the list
     */
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * The work horse of the class, This calculates all user input with the Swipe Controller
     * @param event touch event
     * @param width width of the touchable surface
     * @param height height of the touchable surface
     * @return
     */
    public boolean handleUserEvent(MotionEvent event, double width, double height) {

        double diffX = event.getX() - width/2;
        double diffY = event.getY() - height/2;
        double radianNew = Math.atan(Math.abs(diffY/diffX));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                removeAllMotion();
                radianStart = radianNew;
                lastOffset = radianOffset;
                clear();
                addFlingPoint(new Date().getTime(), radianOffset);
                userActive = true;
                return true;
            case MotionEvent.ACTION_UP:
                userActive = false;
                //radianOffset = getLockedRadians();
                if(flingable && Math.abs(calculateFlingVelocity()) > 4) {
                    // Fling
                    startFling();
                } else if (grooves) {
                    // Lock to a groove
                    lockToGroove();
                }

                redrawListener.redraw();
                return true;
            case MotionEvent.ACTION_MOVE:

                if((diffX > 0 && diffY < 0) || ((diffX < 0 && diffY > 0)))
                    radianOffset = lastOffset + (radianStart - radianNew) * spinSensitivity;
                else
                    radianOffset = lastOffset - (radianStart - radianNew) * spinSensitivity;

                radianStart = radianNew;
                lastOffset = radianOffset;
                addFlingPoint(new Date().getTime(), radianOffset);

                redrawListener.redraw();
                return true;
        }
        return false;
    }

    /**
     * Flings the wheel to a position in radians on the wheel if there is no
     * user input
     * @param targetOffset radian target
     * @return
     */
    public boolean flingToRadians(double targetOffset) {
        // Only works if the user is not touching it
        if(!userActive) {
            // Remove all other runnables affecting the wheel
            removeAllMotion();

            // Find offset from the current position
            double offset = targetOffset - (radianOffset % (Math.PI * 2));
            double velocity;
            if (offset > Math.PI) {
                offset -= Math.PI * 2;
                velocity = -Math.sqrt(Math.abs(2 * friction * offset));
            } else if (offset > 0) {
                velocity = Math.sqrt(Math.abs(2 * friction * offset));
            } else if (offset < -Math.PI && offset > -Math.PI * 2) {
                offset += Math.PI * 2;
                velocity = Math.sqrt(Math.abs(2 * friction * offset));
            } else if (offset < -Math.PI * 2) {
                offset += Math.PI * 2;
                velocity = -Math.sqrt(Math.abs(2 * friction * offset));
            } else {
                velocity = -Math.sqrt(Math.abs(2 * friction * offset));
            }

            // Start Flinging
            this.velocity = velocity;

            startFling();
        }
        return userActive;
    }

    /**
     * Starts the calculation of flinging
     */
    private void startFling() {
        // Not manually swiping
        if(!userActive) {
            if (flingHandler == null)
                flingHandler = new Handler();
            else
                flingHandler.removeCallbacks(flingRunnable);
            flingHandler.postDelayed(flingRunnable, (long) (1000 / frameRate));
        }
    }

    /**
     * Runnable in charge of flinging the wheel
     */
    Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            // Slow the velocity and update
            double offset = slowVelocityDown();
            //Log.d("Velocity", "Velo: " + swipeVelocity.velocity);
            // Buffer Variable
            if (Math.abs(velocity) > friction / frameRate * 4) {
                radianOffset += offset;
                redrawListener.redraw();
                startFling();
            } else {
                velocity = 0;
                if(grooves) {
                    // Lock to a groove
                    lockToGroove();
                }
            }
        }
    };

    /**
     * This will lock to the nearest groove
     */
    public void lockToGroove() {
        // Not manually swiping
        if(!userActive) {
            if(lockToGrooveHandler == null)
                lockToGrooveHandler = new Handler();
            else
                lockToGrooveHandler.removeCallbacks(lockToGrooveRunnable);
            lockToGrooveHandler.postDelayed(lockToGrooveRunnable, (long)(1000/frameRate));
        }
    }

    /**
     * Runnable in charge of locking to a groove
     * this is currently linear interpolation
     */
    Handler lockToGrooveHandler = new Handler();
    Runnable lockToGrooveRunnable = new Runnable() {
        @Override
        public void run() {
            // Animate to the correct groove
            double correctOffset = getLockedRadians();
            double diff = correctOffset - radianOffset;
            if (Math.abs(diff) > 2 / frameRate) {
                if(diff > 0)
                    diff = 2 / frameRate;
                else
                    diff = -2 / frameRate;
                radianOffset += diff;
                redrawListener.redraw();
                lockToGroove();
            } else {
                radianOffset += diff;
                redrawListener.redraw();
            }
        }
    };

    /**
     * UTIL function to add recent touch inputs
     * @param time
     * @param offset
     */
    private void addFlingPoint(long time, double offset) {
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
    }

    /**
     * Clears the cicular queue array for recent touch inputs
     */
    private void clear() {
        start = 0;
        end = 0;
    }

    /**
     * calculates the velocity of the recent touch inputs
     * @return velocity in radians per second
     */
    private double calculateFlingVelocity() {
        int endIndex = end - 1;
        if(endIndex < 0)
            endIndex = points.length - 1;
        velocity = (points[endIndex].offset - points[start].offset) / (points[endIndex].time - points[start].time) * 1000;
        if(velocity > velocityClamp)
            velocity = velocityClamp;
        if(velocity < -velocityClamp)
            velocity = -velocityClamp;
        return velocity;
    }

    /**
     * Applys friction to the velocity
     * @return amount the wheel should move during that frame
     */
    private double slowVelocityDown() {
        if(velocity > 0)
            velocity -= friction / frameRate;
        else
            velocity += friction / frameRate;

        return velocity / frameRate;
    }

    /**
     * Class to contain recent touch event data
     */
    public class FlingPoint {
        public long time;
        public double offset;
        public FlingPoint(long time, double offset) {
            this.time = time;
            this.offset = offset;
        }
    }

    /**
     * removes all movement on the wheel
     * includes flinging and locking to a groove
     */
    private void removeAllMotion() {
        velocity = 0;
        // Fling
        if (flingHandler != null)
            flingHandler.removeCallbacks(flingRunnable);
        // Lock to groove
        if (lockToGrooveHandler != null)
            lockToGrooveHandler.removeCallbacks(lockToGrooveRunnable);
    }

    /**
     * @return the nearest groove in radians
     */
    private double getLockedRadians() {
        // Lock to the nearest lockAtRadians
        double lockAtRadians = Math.PI * 2 / totalItems;
        double targetOffset = radianOffset;
        double diff = Math.abs(targetOffset) % lockAtRadians;
        if((targetOffset/lockAtRadians) - Math.floor(targetOffset/lockAtRadians) > .5) {
            targetOffset += diff;
        } else {
            targetOffset -= diff;
        }
        return targetOffset;
    }

}