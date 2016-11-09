package com.dhiviyad.workoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiviyad.workoutapp.dataLayer.GraphDetails;
import com.dhiviyad.workoutapp.dataLayer.WorkoutDetails;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


import java.util.ArrayList;
import static android.content.Context.BIND_AUTO_CREATE;

public class RecordWorkoutHorizontalFragment extends Fragment {

//    bar chart => calories
//    line chart => distance
//    private BarChart caloriesBarChart;
//    private LineChart distanceLineChart;

    private CombinedChart mChart;
    View fragmentView;
    GraphDetails graphDetails;


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
        graphDetails = new GraphDetails();
        graphDetails.addCurrentWorkout(new WorkoutDetails());
        createSpeeds();
        createGraph();
        registerBroadCastReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
    }

    private void createSpeeds(){
        TextView speedView = (TextView) fragmentView.findViewById(R.id.avg_speed);
        float avgSpeed = 0.0f;
        if(graphDetails.getTime().size() > 0){
            avgSpeed = graphDetails.getSumSpeed()/graphDetails.getTime().size();
        }
        speedView.setText(StringUtils.getFormattedDistance(avgSpeed));

        float minSpeed = (graphDetails.getMinSpeed() == Integer.MAX_VALUE) ? 0 : graphDetails.getMinSpeed();
        speedView =(TextView) fragmentView.findViewById(R.id.min_speed);
        speedView.setText(StringUtils.getFormattedDistance(minSpeed));

        speedView = (TextView) fragmentView.findViewById(R.id.max_speed);
        speedView.setText(StringUtils.getFormattedDistance(graphDetails.getMaxSpeed()));
    }

//    private long[] mTime = new long[] {0};
//    private float[] mCalories = new float[]{0};
//    private float[] mDistance = new float[]{0};
//    private final int itemcount = 12;

    private void createGraph(){
        mChart = (CombinedChart) fragmentView.findViewById(R.id.chart);
        mChart.clear();
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE,
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        l.setDrawInside(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        final ArrayList<Long> timeData = graphDetails.getTime();
        if(timeData.size() > 0)
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (timeData.get((int) value % timeData.size() )/1000) + "";
            }
            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();

    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < graphDetails.getDistanceEveryFiveMins().size(); index++)
            entries.add(new Entry(index + 0.5f, graphDetails.getDistanceEveryFiveMins().get(index)));

        LineDataSet set = new LineDataSet(entries, "Distance");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        BarDataSet caloriesBarDataSet = getBarDataSet();
        float barWidth = 0.45f; // x2 dataset
        BarData d = new BarData(caloriesBarDataSet);
        d.setBarWidth(barWidth);
        return d;
    }

    @NonNull
    private BarDataSet getBarDataSet() {
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        for (int index = 0; index < graphDetails.getCaloriesEveryFiveMins().size(); index++) {
            entries1.add(new BarEntry(index + 0.5f,  graphDetails.getCaloriesEveryFiveMins().get(index)));
        }

        BarDataSet caloriesBarDataSet = new BarDataSet(entries1, "Calories");
        caloriesBarDataSet.setColor(Color.rgb(60, 220, 78));
        caloriesBarDataSet.setValueTextColor(Color.rgb(60, 220, 78));
        caloriesBarDataSet.setValueTextSize(10f);
        caloriesBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return caloriesBarDataSet;
    }

//    private void updateGraphData(){
//        BarData bd = mChart.getBarData();
////        bd.clearValues();
//        int count = bd.getEntryCount();
//        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
//        for (int index = count-1; index < graphDetails.getCaloriesEveryFiveMins().size(); index++) {
////            entries1.add(new BarEntry(index + 0.5f, graphDetails.getCaloriesEveryFiveMins().get(index)));
//           bd.addEntry((Entry) new BarEntry(index + 0.5f, graphDetails.getCaloriesEveryFiveMins().get(index)), 0);
//        }
//
//        BarDataSet caloriesBarDataSet = new BarDataSet(entries1, "Calories");
//        caloriesBarDataSet.setColor(Color.rgb(60, 220, 78));
//        caloriesBarDataSet.setValueTextColor(Color.rgb(60, 220, 78));
//        caloriesBarDataSet.setValueTextSize(10f);
//        caloriesBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        bd.addDataSet(caloriesBarDataSet);
////        bd.addDataSet(getBarDataSet());
//
////        LineData ld= mChart.getLineData();
////        ld.clearValues();
//
//        mChart.getData().notifyDataChanged();
//        mChart.notifyDataSetChanged();
//        mChart.invalidate();
//        mChart.moveViewToX(bd.getEntryCount());
//
//        Log.i("Profile page", "calling update data");
//    }


    /******************************************************
     * Broadcast service code
     ******************************************************/
    ArrayList<MyBroadcastReceiver> broadcastReceivers;
    class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch(action){
                case IntentFilterNames.GRAPH_DATA_RECEIVED:
                    graphDetails = (GraphDetails) intent.getSerializableExtra(IntentFilterNames.GRAPH_DATA);
                    Toast.makeText(context, "Intent detected => " + graphDetails.getCaloriesEveryFiveMins().size(), Toast.LENGTH_SHORT).show();
                    createSpeeds();
//                    updateGraphData();
                    createGraph();
                    break;
                default: break;
            }
        }
    }
    private void registerBroadCastReceivers(){
        broadcastReceivers = new ArrayList<MyBroadcastReceiver>();
        createBroadcaseReceiver(IntentFilterNames.GRAPH_DATA_RECEIVED);
    }
    private void createBroadcaseReceiver(String intentName){
        MyBroadcastReceiver r = new MyBroadcastReceiver();
        getActivity().getApplicationContext().registerReceiver(r, new IntentFilter(intentName));
        broadcastReceivers.add(r);
    }
    private void unregisterBroadcastReceivers() {
        for(MyBroadcastReceiver br : broadcastReceivers){
            getActivity().getApplicationContext().unregisterReceiver(br);
        }
        broadcastReceivers = null;
    }
}
