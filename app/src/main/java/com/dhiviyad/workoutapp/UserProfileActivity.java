package com.dhiviyad.workoutapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.dhiviyad.workoutapp.dataLayer.UserDetails;
import com.dhiviyad.workoutapp.database.DatabaseHelper;

public class UserProfileActivity extends AppCompatActivity {

    UserDetails user;
    PopupWindow editUsernamePopup = null;
    PopupWindow editWeightPopup = null;
    PopupWindow editHeightPopup = null;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = new DatabaseHelper(getApplicationContext());
        user = db.fetchUserDetails();
        Button b = (Button) findViewById(R.id.username_btn);
        b.setText(user.getName());
        b = (Button) findViewById(R.id.weight_btn);
        b.setText(user.getWeight() + "");
        b = (Button) findViewById(R.id.height_btn);
        b.setText(user.getHeight() + "");
        RadioGroup rg = (RadioGroup) findViewById(R.id.gender_radio_gp);
        if(user.getGender().equals("M")){
            rg.check(R.id.male_radio);
        } else {
            rg.check(R.id.female_radio);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(id == R.id.male_radio){
                    user.setGender("M");
                } else user.setGender("F");
                db.updateUserDetails(user, user.getId());
            }
        });
    }

    public void showUsernamePopup(View v){
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

    public void closeEditUsernameCancel(View view){
//        Log.v("My tag", "Clicked OKKKKKKK");
        editUsernamePopup.dismiss();
    }

    public void okEditUsernamePopup(View v){
        EditText usernameText = (EditText) editUsernamePopup.getContentView().findViewById(R.id.editText_username);
        String newName = usernameText.getText().toString();
        user.setName(newName);
        Button b = (Button) findViewById(R.id.username_btn);
        b.setText(newName);
        db.updateUserDetails(user, user.getId());
        editUsernamePopup.dismiss();
    }

    public void showEditWeightPopup(View v){
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

    public void cancelEditWeightPopup(View v){
        editWeightPopup.dismiss();
    }

    public void okEditWeightPopup(View v){
        EditText weightText = (EditText) editWeightPopup.getContentView().findViewById(R.id.editText_weight);
        String weightStr = weightText.getText().toString();
        double weight = Double.parseDouble(weightStr);
        user.setWeight(weight);
        Button b = (Button) findViewById(R.id.weight_btn);
        b.setText(weightStr);
        db.updateUserDetails(user, user.getId());
        editWeightPopup.dismiss();
    }

    public void showEditHeightPopup(View v){
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

    public void cancelEditHeightPopup(View v){
        editHeightPopup.dismiss();
    }

    public void okEditHeightPopup(View v){
        EditText weightText = (EditText) editHeightPopup.getContentView().findViewById(R.id.editText_height);
        String weightStr = weightText.getText().toString();
        double weight = Double.parseDouble(weightStr);
        user.setHeight(weight);
        Button b = (Button) findViewById(R.id.height_btn);
        b.setText(weightStr);
        db.updateUserDetails(user, user.getId());
        editHeightPopup.dismiss();
    }
}
