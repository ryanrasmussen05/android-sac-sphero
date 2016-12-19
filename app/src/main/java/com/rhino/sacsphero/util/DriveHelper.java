package com.rhino.sacsphero.util;

import com.orbotix.ConvenienceRobot;
import com.orbotix.macro.MacroObject;
import com.orbotix.macro.cmd.Delay;
import com.orbotix.macro.cmd.Roll;
import com.orbotix.macro.cmd.RotationRate;
import com.orbotix.macro.cmd.Stop;

public class DriveHelper {

    public static void Turn(ConvenienceRobot connectedRobot, int heading) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();

        MacroObject macro = new MacroObject();
        macro.addCommand(new RotationRate(0.1f));
        macro.addCommand(new Roll(0, heading, 0));
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(connectedRobot.getRobot());
        macro.playMacro();
    }

    public static void Drive(ConvenienceRobot connectedRobot, int timeInterval, float speed) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();

        MacroObject macro = new MacroObject();
        macro.addCommand(new Roll(speed, 0, 0));
        macro.addCommand(new Delay(timeInterval));
        macro.addCommand(new Stop(0));
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(connectedRobot.getRobot());
        macro.playMacro();
    }
}
