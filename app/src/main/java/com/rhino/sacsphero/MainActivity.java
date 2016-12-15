package com.rhino.sacsphero;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.Sphero;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.subsystem.SensorControl;
import com.rhino.sacsphero.util.InputFilterMinMax;

public class MainActivity extends AppCompatActivity implements RobotChangedStateListener, ResponseListener {

    private static final String TAG = "SAC Sphero";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;
    private static final int REQUEST_CODE_LOCATION_PERMISSION_WITH_DISCOVERY = 43;

    private int numMoves;

    private DiscoveryAgentClassic discoveryAgent;
    private ConvenienceRobot connectedRobot;
    private boolean inGame;

    private ProgressDialog connectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inGame = false;
        numMoves = 0;
        discoveryAgent = DiscoveryAgentClassic.getInstance();
        checkPermissions(REQUEST_CODE_LOCATION_PERMISSION);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(inGame) {
            setupLabyrinthScreen();
        } else {
            setupHomeScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectSphero();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!inGame) {
            menu.findItem(R.id.disconnect_sphero).setVisible(connectedRobot != null);
            menu.findItem(R.id.sleep_sphero).setVisible(connectedRobot != null);
            menu.findItem(R.id.exit_game).setVisible(false);
        } else {
            menu.findItem(R.id.disconnect_sphero).setVisible(false);
            menu.findItem(R.id.sleep_sphero).setVisible(false);
            menu.findItem(R.id.exit_game).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.disconnect_sphero:
                disconnectSphero();
                return true;
            case R.id.sleep_sphero:
                shutdownSphero();
                return true;
            case R.id.exit_game:
                exitLabyrinth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    //TODO maybe move these
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
            if(!discoveryAgent.isDiscovering()) {
                discoveryAgent.startDiscovery(this);
            }
        } catch (DiscoveryException e) {
            Log.e(TAG, "Could not start discovery. Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showConnectionDialog() {
        connectionDialog = new ProgressDialog(this);
        connectionDialog.setTitle("Connecting");
        connectionDialog.setMessage("Looking for Sphero");
        connectionDialog.setCanceledOnTouchOutside(false);
        connectionDialog.setCancelable(false);
        connectionDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disconnectSphero();
            }
        });
        connectionDialog.show();
    }

    public void disconnectSphero() {
        if(discoveryAgent.isDiscovering()) {
            discoveryAgent.stopDiscovery();
        }
        discoveryAgent.removeRobotStateListener(this);

        if(connectedRobot != null) {
            connectedRobot.disconnect();
            connectedRobot = null;
        }

        discoveryAgent.disconnectAll();
        invalidateOptionsMenu();
    }

    public void shutdownSphero() {
        if(discoveryAgent.isDiscovering()) {
            discoveryAgent.stopDiscovery();
        }
        discoveryAgent.removeRobotStateListener(this);

        if(connectedRobot != null) {
            connectedRobot.sleep();
            connectedRobot = null;
        }

        setupHomeScreen();
        invalidateOptionsMenu();
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch (type) {
            case Online:
                Log.e(TAG, "ONLINE");
                connectedRobot = new Sphero(robot);
                discoveryAgent.stopDiscovery();

                connectedRobot.setLed(1f, 0f, 0f);
                connectedRobot.setBackLedBrightness(1.0f);
                connectedRobot.enableSensors(SensorFlag.LOCATOR.longValue(), SensorControl.StreamingRate.STREAMING_RATE10);

                if(inGame) {
                    setupLabyrinthScreen();
                } else {
                    setupHomeScreen();
                }

                connectionDialog.dismiss();
                invalidateOptionsMenu();
                break;

            case Disconnected:
                handleDisconnect();
                Log.e(TAG, "DISCONNECTED");
                break;

            case Connecting:
                connectionDialog.setMessage("Found: " + robot.getName());
                Log.e(TAG, "CONNECTING");
                break;

            case Connected:
                connectionDialog.setMessage("Connecting to: " + robot.getName());
                Log.e(TAG, "CONNECTED");
        }
    }

    @Override
    public void handleResponse(DeviceResponse deviceResponse, Robot robot) {
        //Log.d(TAG, deviceResponse.toString());
    }

    @Override
    public void handleStringResponse(String s, Robot robot) {
        //Log.d(TAG, s);
    }

    @Override
    public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
//        if (asyncMessage instanceof DeviceSensorAsyncMessage) {
//            float positionX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
//            float positionY = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
//        } else {
//            Log.d(TAG, asyncMessage.toString());
//        }
    }

    public void handleDisconnect() {
        //TODO
        Log.e(TAG, "UNEXPECTED DISCONNECT");
    }

    public void startLabyrinth(View view) {
        setContentView(R.layout.activity_labyrinth);
        setupLabyrinthScreen();
        inGame = true;
        numMoves = 0;
        invalidateOptionsMenu();
    }

    public void exitLabyrinth() {
        inGame = false;
        setContentView(R.layout.activity_main);
        setupHomeScreen();
        invalidateOptionsMenu();
    }

    private void setupHomeScreen() {
        if(connectedRobot != null) {
            findViewById(R.id.connectButton).setVisibility(View.GONE);
            findViewById(R.id.startLabyrinthButton).setVisibility(View.VISIBLE);

            TextView statusMessage = (TextView) findViewById(R.id.statusMessage);
            statusMessage.setTextSize(20);
            statusMessage.setText(getString(R.string.connect_message, connectedRobot.getRobot().getName()));
        } else {
            findViewById(R.id.connectButton).setVisibility(View.VISIBLE);
            findViewById(R.id.startLabyrinthButton).setVisibility(View.GONE);

            TextView statusMessage = (TextView) findViewById(R.id.statusMessage);
            statusMessage.setText(null);
        }
    }

    private void setupLabyrinthScreen() {
        if(connectedRobot != null) {
            findViewById(R.id.reConnectButton).setVisibility(View.GONE);

            findViewById(R.id.driveFiveButton).setEnabled(true);
            findViewById(R.id.driveTenButton).setEnabled(true);
            findViewById(R.id.driveTwentyButton).setEnabled(true);
            findViewById(R.id.turnLeftButton).setEnabled(true);
            findViewById(R.id.turnRightButton).setEnabled(true);
            findViewById(R.id.turnInput).setEnabled(true);
        } else {
            findViewById(R.id.reConnectButton).setVisibility(View.VISIBLE);

            findViewById(R.id.driveFiveButton).setEnabled(false);
            findViewById(R.id.driveTenButton).setEnabled(false);
            findViewById(R.id.driveTwentyButton).setEnabled(false);
            findViewById(R.id.turnLeftButton).setEnabled(false);
            findViewById(R.id.turnRightButton).setEnabled(false);
            findViewById(R.id.turnInput).setEnabled(false);
        }

        ((TextView) findViewById(R.id.numberOfMoves)).setText(String.valueOf(numMoves));
        ((EditText) findViewById(R.id.turnInput)).setFilters(new InputFilter[]{ new InputFilterMinMax(0, 360)});
    }
}
