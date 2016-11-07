package com.dhiviyad.workoutapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiviyad.workoutapp.dataLayer.UserDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;
import com.dhiviyad.workoutapp.database.DatabaseHelper;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    UserDetails user;
    WorkoutDetails totalWorkouts, weeklyWorkouts;
    PopupWindow editUsernamePopup = null;
    PopupWindow editWeightPopup = null;
    PopupWindow editHeightPopup = null;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = new DatabaseHelper(getApplicationContext());
        updateUserDetails();

        totalWorkouts = db.getTotalWorkout();
        setTotalWorkoutData(new WorkoutDetails());

        weeklyWorkouts = db.getWeeklyWorkout();
        setWeeklyWorkoutData(new WorkoutDetails());
        Toast.makeText(this, "Total workout count => " + weeklyWorkouts.getWorkoutCount() + " => " +weeklyWorkouts.getDistance(), Toast.LENGTH_SHORT).show();
        bindService();
        registerBroadCastReceivers();
    }

    public void setWeeklyWorkoutData(WorkoutDetails currentWorkout) {
        TextView totalTextView = (TextView) findViewById(R.id.weekly_distance);
        totalTextView.setText(StringUtils.getFormattedDistance(weeklyWorkouts.getDistance() + currentWorkout.getDistance()) + " mi");
        totalTextView = (TextView) findViewById(R.id.weekly_duration);
        long duration = (weeklyWorkouts.getDuration() + currentWorkout.getDuration() / 1000);
        totalTextView.setText(StringUtils.getFormattedTime(duration));
        totalTextView = (TextView) findViewById(R.id.weekly_workouts);
        totalTextView.setText((weeklyWorkouts.getWorkoutCount() + currentWorkout.getWorkoutCount()) + " times");
        totalTextView = (TextView) findViewById(R.id.weekly_calories);
        totalTextView.setText(StringUtils.getFormattedDistance(weeklyWorkouts.getCaloriesBurnt() + currentWorkout.getCaloriesBurnt()) + " cal");
    }


    private void setTotalWorkoutData(WorkoutDetails currentWorkout) {
        TextView totalTextView = (TextView) findViewById(R.id.all_time_distance);
        totalTextView.setText(StringUtils.getFormattedDistance(totalWorkouts.getDistance() + currentWorkout.getDistance()) + " mi");
        totalTextView = (TextView) findViewById(R.id.all_time_duration);
        long duration = (totalWorkouts.getDuration() + currentWorkout.getDuration() / 1000);
        totalTextView.setText(StringUtils.getFormattedTime(duration));
        totalTextView = (TextView) findViewById(R.id.all_time_workouts);
        totalTextView.setText((totalWorkouts.getWorkoutCount() + currentWorkout.getWorkoutCount()) + " times");
        totalTextView = (TextView) findViewById(R.id.all_time_calories);
        totalTextView.setText(StringUtils.getFormattedDistance(totalWorkouts.getCaloriesBurnt() + currentWorkout.getCaloriesBurnt()) + " cal");
    }

    private void updateUserDetails() {
        user = db.fetchUserDetails();
        Button b = (Button) findViewById(R.id.username_btn);
        b.setText(user.getName());
        b = (Button) findViewById(R.id.weight_btn);
        b.setText(user.getWeight() + "");
        b = (Button) findViewById(R.id.height_btn);
        b.setText(user.getHeight() + "");
        RadioGroup rg = (RadioGroup) findViewById(R.id.gender_radio_gp);
        if (user.getGender().equals("M")) {
            rg.check(R.id.male_radio);
        } else {
            rg.check(R.id.female_radio);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.male_radio) {
                    user.setGender("M");
                } else user.setGender("F");
                db.updateUserDetails(user, user.getId());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    public void showUsernamePopup(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editUsernamePopup = new PopupWindow(
                inflater.inflate(R.layout.fragment_edit_username_popup, null, false),
                1000,
                950,
                true);
        EditText usernameText = (EditText) editUsernamePopup.getContentView().findViewById(R.id.editText_username);
        usernameText.setText(user.getName());
        editUsernamePopup.showAtLocation(this.findViewById(R.id.user_profile_layout), Gravity.CENTER, 0, 0);
    }

    public void closeEditUsernameCancel(View view) {
//        Log.v("My tag", "Clicked OKKKKKKK");
        editUsernamePopup.dismiss();
    }

    public void okEditUsernamePopup(View v) {
        EditText usernameText = (EditText) editUsernamePopup.getContentView().findViewById(R.id.editText_username);
        String newName = usernameText.getText().toString();
        user.setName(newName);
        Button b = (Button) findViewById(R.id.username_btn);
        b.setText(newName);
        db.updateUserDetails(user, user.getId());
        editUsernamePopup.dismiss();
    }

    public void showEditWeightPopup(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editWeightPopup = new PopupWindow(
                inflater.inflate(R.layout.fragment_edit_weight_popup, null, false),
                1000,
                950,
                true);
        EditText usernameText = (EditText) editWeightPopup.getContentView().findViewById(R.id.editText_weight);
        usernameText.setText(user.getWeight() + "");
        editWeightPopup.showAtLocation(this.findViewById(R.id.user_profile_layout), Gravity.CENTER, 0, 0);
    }

    public void cancelEditWeightPopup(View v) {
        editWeightPopup.dismiss();
    }

    public void okEditWeightPopup(View v) {
        EditText weightText = (EditText) editWeightPopup.getContentView().findViewById(R.id.editText_weight);
        String weightStr = weightText.getText().toString();
        double weight = Double.parseDouble(weightStr);
        user.setWeight(weight);
        Button b = (Button) findViewById(R.id.weight_btn);
        b.setText(weightStr);
        db.updateUserDetails(user, user.getId());
        editWeightPopup.dismiss();
    }

    public void showEditHeightPopup(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editHeightPopup = new PopupWindow(
                inflater.inflate(R.layout.fragment_edit_height_popup, null, false),
                1000,
                950,
                true);
        EditText usernameText = (EditText) editHeightPopup.getContentView().findViewById(R.id.editText_height);
        usernameText.setText(user.getHeight() + "");
        editHeightPopup.showAtLocation(this.findViewById(R.id.user_profile_layout), Gravity.CENTER, 0, 0);
    }

    public void cancelEditHeightPopup(View v) {
        editHeightPopup.dismiss();
    }

    public void okEditHeightPopup(View v) {
        EditText weightText = (EditText) editHeightPopup.getContentView().findViewById(R.id.editText_height);
        String weightStr = weightText.getText().toString();
        double weight = Double.parseDouble(weightStr);
        user.setHeight(weight);
        Button b = (Button) findViewById(R.id.height_btn);
        b.setText(weightStr);
        db.updateUserDetails(user, user.getId());
        editHeightPopup.dismiss();
    }

    /******************************************************
     * Broadcast service code
     ******************************************************/
    ArrayList<MyBroadcastReceiver> broadcastReceivers;


    class MyBroadcastReceiver extends BroadcastReceiver {

        public MyBroadcastReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            switch (action) {
                case IntentFilterNames.WORKOUT_RECIEVED:
                    WorkoutDetails d = (WorkoutDetails) intent.getSerializableExtra(IntentFilterNames.WORKOUT_DATA);
                    setTotalWorkoutData(d);
                    setWeeklyWorkoutData(d);
                    break;

                default:
                    break;
            }
        }

    }

    private void unregisterReceiver() {
        for (MyBroadcastReceiver br : broadcastReceivers) {
            getApplicationContext().unregisterReceiver(br);
        }
        broadcastReceivers = null;
    }

    private void registerBroadCastReceivers() {
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.WORKOUT_RECIEVED);
    }

    private void createBroadcaseReceiver(String intentName) {
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }

    /*************************************************
     * REMOTESERVICE STUFF
     ************************************************/
    RemoteConnection remoteConnection;
    IWorkoutAidlInterface remoteService;

    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IWorkoutAidlInterface.Stub.asInterface((IBinder) service);
            Log.v("Userprofile page", "remote service connected");
//            Toast.makeText(MainActivity.this, "remote service connected", Toast.LENGTH_LONG).show();
            try {
                remoteService.sendCurrentWorkoutData();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            remoteService = null;
        }
    }

    private void bindService() {
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("com.dhiviyad.workoutapp", WorkoutRemoteService.class.getName());
        if (!getApplicationContext().bindService(intent, remoteConnection, BIND_AUTO_CREATE)) {
            Toast.makeText(this, "failed to bind remote service", Toast.LENGTH_LONG).show();
        }
    }
}
