package com.rhino.sacsphero.util;

public class LocationHelper {

    public static final int STREAMING_RATE_DIVISOR = 10;
    private static final int BASE_STREAMING_RATE = 400;

    private boolean tracking;
    private double targetDistance;
    private double distanceMoved;
    private double secondsPerUpdate;

    public LocationHelper() {
        this.tracking = false;
        this.targetDistance = 0;
        this.distanceMoved = 0;

        this.secondsPerUpdate = (STREAMING_RATE_DIVISOR / BASE_STREAMING_RATE);
    }

    public void updateLocation(double velocityX, double velocityY) {
        if(this.tracking) {
            double totalVelocity = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
            double deltaDistance = totalVelocity * secondsPerUpdate;
            this.distanceMoved += deltaDistance;
        }
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
