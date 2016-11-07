package com.dhiviyad.workoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

public class RecordWorkoutHorizontalFragment extends Fragment {

//    bar chart => calories
//    line chart => distance
    private BarChart caloriesBarChart;
    private LineChart distanceLineChart;
    View fragmentView;


    public RecordWorkoutHorizontalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_workout_horizontal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;
        distanceLineChart = new LineChart(getContext());
        LinearLayout graphArea = (LinearLayout) fragmentView.findViewById(R.id.graph_area);
        graphArea.addView(distanceLineChart); // add the programmatically created chart

//        //create data
//        YourData[] dataObjects = ...;
//        List<Entry> entries = new ArrayList<Entry>();
//        for (YourData data : dataObjects) {
//
//            // turn your data into Entry objects
//            entries.add(new Entry(data.getValueX(), data.getValueY()));
//        }
    }

}
