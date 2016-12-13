package com.rhino.sacsphero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.Ollie;
import com.orbotix.Sphero;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.classic.RobotClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.le.RobotLE;

public class MainActivity extends AppCompatActivity implements RobotChangedStateListener {

    private static final String TAG = "MainActivity";
    private ConvenienceRobot connectedRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(connectedRobot != null) {
            //should this be disconnect?
            connectedRobot.sleep();
        }
    }

    public void start(View view) {
        startDiscovery();
    }

    public void turn(View view) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();
        connectedRobot.rotate(90);
    }

    public void drive(View view) {
        if(connectedRobot == null)
            return;

        connectedRobot.setZeroHeading();
        connectedRobot.drive(0, 0.5f);
    }

    public void stop(View view) {
        if(connectedRobot == null)
            return;

        connectedRobot.stop();
    }

    public void startDiscovery() {
        try {
            DualStackDiscoveryAgent.getInstance().addRobotStateListener(this);
            DualStackDiscoveryAgent.getInstance().startDiscovery(this);
        } catch (DiscoveryException e) {
            Log.e(TAG, "Could not start discovery. Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                // Bluetooth Classic (Sphero)
                if (robot instanceof RobotClassic) {
                    connectedRobot = new Sphero(robot);
                }
                // Bluetooth LE (Ollie)
                if (robot instanceof RobotLE) {
                    connectedRobot = new Ollie(robot);
                }

                DualStackDiscoveryAgent.getInstance().stopDiscovery();

                connectedRobot.setLed(0f, 1f, 0f);
                connectedRobot.setBackLedBrightness(1.0f);
                connectedRobot.enableLocator(true);
                //connectedRobot.addResponseListener(this);

                TextView statusMessage = (TextView) findViewById(R.id.statusMessage);
                statusMessage.setTextSize(40);
                statusMessage.setText("Connected to: " + robot.getName());

                break;

            case Disconnected:
                startDiscovery();
                break;
        }
    }

//    @Override
//    public void handleResponse(DeviceResponse deviceResponse, Robot robot) {
//        System.out.println("Response: " + deviceResponse.toString());
//    }
//
//    @Override
//    public void handleStringResponse(String s, Robot robot) {
//        System.out.println("String Response: " + s);
//    }
//
//    @Override
//    public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
//        System.out.println("Async Message: " + asyncMessage.toString());
//
//        if(asyncMessage instanceof DeviceSensorAsyncMessage) {
//            float positionX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
//            float positionY =  ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
//            System.out.println("x: " + positionX + ", y: " + positionY);
//        }
//    }
}
