package com.rhino.sacsphero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orbotix.ConvenienceRobot;
import com.orbotix.Sphero;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.command.BackLEDOutputCommand;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.DeviceCommand;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DiscoveryAgentEventListener, RobotChangedStateListener{

    private static final String TAG = "MainActivity";

    private DiscoveryAgent discoveryAgent;
    private ConvenienceRobot connectedRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (discoveryAgent != null) {
            for (Robot robot : discoveryAgent.getConnectedRobots()) {
                robot.sleep();
            }
        }
    }

    public void start(View view) {
        startDiscovery();
    }

    public void turn(View view) {
        connectedRobot.setZeroHeading();
        connectedRobot.rotate(90);
    }

    public void drive(View view) {
        connectedRobot.setZeroHeading();
        connectedRobot.drive(0, 1.0f);
    }

    public void startDiscovery() {
        try {
            discoveryAgent = DiscoveryAgentClassic.getInstance();
            discoveryAgent.addDiscoveryListener(this);
            discoveryAgent.addRobotStateListener(this);
            discoveryAgent.startDiscovery(this);
        } catch (DiscoveryException e) {
            Log.e(TAG, "Could not start discovery. Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleRobotsAvailable(List<Robot> robots) {
        discoveryAgent.connect(robots.get(0));
    }

    @Override
    public void changedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                discoveryAgent.stopDiscovery();
                discoveryAgent.removeDiscoveryListener(this);

                connectedRobot = new Sphero(robot);
                connectedRobot.setLed(0f, 1f, 0f);
                connectedRobot.setBackLedBrightness(1.0f);

                TextView statusMessage = (TextView) findViewById(R.id.statusMessage);
                statusMessage.setTextSize(40);
                statusMessage.setText("Connected to: " + robot.getName());

                break;

            case Disconnected:
                startDiscovery();
                break;
        }
    }
}
