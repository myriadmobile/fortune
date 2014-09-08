package com.myriadmobile.fortune.paths;

/**
 * Created by cclose on 9/8/14.
 */
public abstract class CustomPath {
    public abstract double getRadiusAtRadians(double radians);
    public boolean sizeBasedOnRadius() {return true;}
}
