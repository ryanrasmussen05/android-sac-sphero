package com.rhino.sacsphero.util;

public class LocationHelper {

    private boolean tracking;
    private double targetDistance;
    private double distanceMoved;
    private double previousX;
    private double previousY;

    public LocationHelper() {
        this.tracking = false;
        this.targetDistance = 0;
        this.distanceMoved = 0;
        this.previousX = 0;
        this.previousY = 0;
    }

    public void updateLocation(double x, double y) {
        if(this.tracking) {
            double deltaX = Math.abs(x - this.previousX);
            double deltaY = Math.abs(y - this.previousY);
            double deltaDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            this.distanceMoved += deltaDistance;
        }

        this.previousX = x;
        this.previousY = y;
    }

    public void startTracking(double targetDistance) {
        this.tracking = true;
        this.distanceMoved = 0;
        this.targetDistance = targetDistance;
    }

    public boolean shouldStop() {
        if(!this.tracking) {
            return false;
        }

        boolean targetAchieved = this.distanceMoved >= this.targetDistance;
        if(targetAchieved) {
            this.tracking = false;
        }

        return targetAchieved;
    }
}
