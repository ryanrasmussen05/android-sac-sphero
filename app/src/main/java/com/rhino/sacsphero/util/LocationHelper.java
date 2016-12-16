package com.rhino.sacsphero.util;

public class LocationHelper {

    private boolean tracking;
    private double targetDistance;
    private double distanceMoved;
    private double startX;
    private double startY;
    private double currentX;
    private double currentY;

    public LocationHelper() {
        this.tracking = false;
        this.targetDistance = 0;
        this.distanceMoved = 0;
        this.startX = 0;
        this.startY = 0;
    }

    public void updateLocation(double x, double y) {
        this.currentX = x;
        this.currentY = y;

        if(this.tracking) {
            double deltaX = Math.abs(x - this.startX);
            double deltaY = Math.abs(y - this.startY);
            this.distanceMoved = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        }
    }

    public void startTracking(double targetDistance) {
        this.tracking = true;
        this.distanceMoved = 0;
        this.targetDistance = targetDistance;

        this.startX = this.currentX;
        this.startY = this.currentY;
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
