package com.dhiviyad.workoutapp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

import static java.lang.System.in;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "Main Activity";

    IWorkoutAidlInterface remoteService;
    ArrayList<MyBroadcastReceiver> broadcastReceivers;
    RemoteConnection remoteConnection;
    boolean isBound = false;
    Fragment horizontalFragment = new RecordWorkoutHorizontalFragment();
    Fragment verticalFragment = new RecordWorkoutVerticalFragment();


    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IWorkoutAidlInterface.Stub.asInterface((IBinder) service);
            Log.v(TAG, "remote service connected");
            Toast.makeText(MainActivity.this, "service connected " , Toast.LENGTH_LONG).show();
            try {
                boolean recordingWorkout = remoteService.getWorkoutState();
                Button b = (Button) findViewById(R.id.workout_toggle_button);
                if(b != null) {
                    Toast.makeText(MainActivity.this, "recordigWorkout = " + recordingWorkout , Toast.LENGTH_LONG).show();
                    if (recordingWorkout == true) {
                        b.setText("Stop workout");
                    } else b.setText("Start workout");
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
//            Toast.makeText(MainActivity.this, "remote service disconnected", Toast.LENGTH_LONG).show();
            Log.v(TAG, "remote service disconnected");
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Intent detected => " + intent.getAction() , Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Mainactivity create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google play services is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
            Toast.makeText(this, "app doesn't have permissions", Toast.LENGTH_LONG).show();
            return;
        }

        bindService();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment contentFragment = null;
        Configuration config = getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            contentFragment = horizontalFragment;
        } else {
            contentFragment = verticalFragment;
        }
        contentFragment.setRetainInstance(true);

        fragmentTransaction.replace(R.id.fragment_placeholder, contentFragment);
        fragmentTransaction.commit();
        registerBroadCastReceivers();
    }

    @Override
    public void onDestroy(){
        unregisterBroadcaseReceivers();
//        if(isBound) getApplicationContext().unbindService(remoteConnection);
    }



//    @Override
//    public void onPause(){
//        getApplicationContext().unregisterReceiver(broadcastReceiver);
//        getApplicationContext().unbindService(remoteConnection);
//    }

    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
//        createBroadcaseReceiver(IntentFilterNames.LOCATION_RECEIVED);
//        createBroadcaseReceiver(IntentFilterNames.TEST_RECEIVED);
    }

    private void createBroadcaseReceiver(String intentName){
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }

    private void unregisterBroadcaseReceivers() {
        super.onDestroy();
        for(MyBroadcastReceiver br : broadcastReceivers){
            getApplicationContext().unregisterReceiver(br);
        }
        broadcastReceivers = null;
    }

    private void bindService() {
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("com.dhiviyad.workoutapp", WorkoutRemoteService.class.getName());
        if(!getApplicationContext().bindService(intent, remoteConnection, BIND_AUTO_CREATE)){
            Toast.makeText(this, "failed to bind remote service", Toast.LENGTH_LONG).show();
            isBound = false;
        } else isBound = true;
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            googleAPI.getErrorDialog(this, status, 0).show();//getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void toggleWorkout(View v) throws RemoteException {
//        Log.i(TAG, "Clicked toggleWorkout button");
        Button b = (Button) findViewById(R.id.workout_toggle_button);
        String buttonStr =  b.getText().toString().toLowerCase();
        if(buttonStr.equals("start workout")){
            buttonStr = "Stop workout";
            remoteService.startWorkout();
        } else {
            buttonStr = "Start workout";
            remoteService.stopWorkout();
        }
        b.setText(buttonStr);
    }

    public void showProfilePage(View v){
        Log.i(TAG, "Clicked showProfilePage button");
        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
    }
}
