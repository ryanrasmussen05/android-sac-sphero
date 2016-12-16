package com.rhino.sacsphero.util;

import com.orbotix.ConvenienceRobot;

public class DriveHelper {

    public static void Turn(ConvenienceRobot connectedRobot, int heading) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();
        connectedRobot.rotate(heading);
    }

    public static void Stop(ConvenienceRobot connectedRobot) {
        if(connectedRobot == null)
            return;

        connectedRobot.stop();
    }

    public static void Drive(ConvenienceRobot connectedRobot) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();
        connectedRobot.drive(0, 0.2f);
    }
}
