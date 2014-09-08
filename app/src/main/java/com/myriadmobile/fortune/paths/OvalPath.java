package com.myriadmobile.fortune.paths;

import android.util.Log;

/**
 * Created by cclose on 9/8/14.
 */
public class OvalPath extends CustomPath {

    double A = 1;
    double B = 1;

    public OvalPath(double A, double B) {
        this.A = A;
        this.B = B;
    }

    @Override
    public double getRadiusAtRadians(double radians) {

        double scale = Math.sqrt(Math.pow(A,2) + Math.pow(B,2));
        double a = A * scale;
        double b = B * scale;

        return (a*b)/Math.sqrt(Math.pow((b*Math.cos(radians)),2) + Math.pow((a*Math.sin(radians)),2));
    }

    @Override
    public boolean sizeBasedOnRadius() {
        return false;
    }
}
