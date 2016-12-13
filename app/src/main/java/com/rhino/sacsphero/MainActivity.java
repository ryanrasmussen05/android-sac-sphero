package com.rhino.sacsphero;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.Sphero;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

public class MainActivity extends AppCompatActivity implements RobotChangedStateListener {

    private static final String TAG = "SAC Sphero";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private static final int REQUEST_CODE_LOCATION_PERMISSION_WITH_DISCOVERY = 43;

    private DiscoveryAgentClassic discoveryAgent;
    private ConvenienceRobot connectedRobot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discoveryAgent = DiscoveryAgentClassic.getInstance();
        checkPermissions(REQUEST_CODE_LOCATION_PERMISSION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        discoveryAgent.stopDiscovery();
        if(connectedRobot != null) {
            connectedRobot.sleep();
            connectedRobot = null;
        }
    }

    //on Android M and higher, we must manually ask for "dangerous" permissions
    public void checkPermissions(int requestCode) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if(hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Location permission has not yet been granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
            } else {
                Log.d(TAG, "Location permission already granted");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location is required to connect to Sphero!", Toast.LENGTH_LONG).show();
                }
                return;

            case REQUEST_CODE_LOCATION_PERMISSION_WITH_DISCOVERY:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery();
                } else {
                    Toast.makeText(this, "Location is required to connect to Sphero!", Toast.LENGTH_LONG).show();
                }
        }
    }

    public boolean hasProperPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            return hasLocationPermission == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public void start(View view) {
        if(hasProperPermissions()) {
            startDiscovery();
        } else {
            checkPermissions(REQUEST_CODE_LOCATION_PERMISSION_WITH_DISCOVERY);
        }

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
            showConnectionDialog();
            discoveryAgent.addRobotStateListener(this);
            discoveryAgent.startDiscovery(this);
        } catch (DiscoveryException e) {
            Log.e(TAG, "Could not start discovery. Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showConnectionDialog() {
        ProgressDialog connectionDialog = new ProgressDialog(this);
        connectionDialog.setTitle("Connecting");
        connectionDialog.setMessage("Looking for Sphero");
        connectionDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                discoveryAgent.stopDiscovery();
            }
        });
        connectionDialog.show();
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                connectedRobot = new Sphero(robot);
                discoveryAgent.stopDiscovery();

                connectedRobot.setLed(1f, 0f, 0f);
                connectedRobot.setBackLedBrightness(1.0f);

                TextView statusMessage = (TextView) findViewById(R.id.statusMessage);
                statusMessage.setTextSize(40);
                statusMessage.setText("Connected to: " + robot.getName());

                break;

            case Disconnected:
                startDiscovery();
                break;

            case FailedConnect:
                Log.e(TAG, "FAILED CONNECT");
                break;

            case Offline:
                Log.e(TAG, "OFFLINE");
                break;

            case Connecting:
                Log.e(TAG, "CONNECTING");
                break;

            case Connected:
                Log.e(TAG, "CONNECTED");
        }
    }
}
