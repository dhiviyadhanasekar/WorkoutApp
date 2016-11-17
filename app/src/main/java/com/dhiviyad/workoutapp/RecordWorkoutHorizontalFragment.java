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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

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
    float barWidth = 0.25f; // x2 dataset


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

        registerBroadCastReceivers();

        graphDetails = new GraphDetails();
        graphDetails.addCurrentWorkout(new WorkoutDetails());
        createSpeeds();
        createGraph();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceivers();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadCastReceivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
        if(graphDetails ==  null) return;
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
//        rightAxis.setEnabled(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
//        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        final ArrayList<Long> timeData = graphDetails.getTime();
        xAxis.setLabelCount(timeData.size()-1);
        if(timeData.size() > 0)
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long s = (long) timeData.get((int)value % timeData.size());
                if(s > 60) return ((s/60) + "m");
                return (int)s + "s";
            }
            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax());// + 0.25f);

        int count = graphDetails.getCaloriesEveryFiveMins().size();
        float width = graphDetails.getCaloriesEveryFiveMins().size()*300 + barWidth*count;
        if(width < 2400) width = 2400;
        mChart.setMinimumWidth((int)width);
        mChart.setData(data);
        mChart.moveViewToX(count-barWidth/2);//mChart.getBarData().getEntryCount()+ + barWidth*graphDetails.getCaloriesEveryFiveMins().size());
//        mChart.setFitBars(true);
        mChart.getXAxis().setAxisMinimum(barWidth/2);
        mChart.getXAxis().setAxisMaximum(count-barWidth/2);
        mChart.invalidate();

//        HorizontalScrollView s = (HorizontalScrollView) fragmentView.findViewById(R.id.scroll_view);
//        s.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < graphDetails.getDistanceEveryFiveMins().size(); index++) {
            float shift = (index);// + 0.12f);
//            float shift = (index == 0) ? (index) : (index + 0.5f);
            entries.add(new Entry(shift, graphDetails.getDistanceEveryFiveMins().get(index)));
        }

        int blue = Color.rgb(0,0,255);//Color.rgb(240, 238, 70)
        LineDataSet set = new LineDataSet(entries, "Distance");
        set.setColor(blue);
        set.setLineWidth(2.5f);
        set.setCircleColor(blue);
        set.setCircleRadius(5f);
        set.setFillColor(blue);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(blue);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        BarDataSet caloriesBarDataSet = getBarDataSet();
        BarData d = new BarData(caloriesBarDataSet);
        d.setBarWidth(barWidth);
        return d;
    }

    @NonNull
    private BarDataSet getBarDataSet() {
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        for (int index = 0; index < graphDetails.getCaloriesEveryFiveMins().size(); index++) {
//            float shift = (index == 0) ? (index) : (index + 0.5f);
            float shift = (index); //+ //0.25f);
            entries1.add(new BarEntry(shift,  graphDetails.getCaloriesEveryFiveMins().get(index)));
        }

        int green = Color.rgb(0, 100, 0);//Color.rgb(60, 220, 78);
        int green_light = Color.rgb(60, 220, 78);

        BarDataSet caloriesBarDataSet = new BarDataSet(entries1, "Calories");
        caloriesBarDataSet.setColor(green_light);
        caloriesBarDataSet.setValueTextColor(green);
        caloriesBarDataSet.setValueTextSize(10f);
        caloriesBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return caloriesBarDataSet;
    }


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
//                    Toast.makeText(context, "Intent detected => " + graphDetails.getTime().get((graphDetails.getTime().size()-1)), Toast.LENGTH_SHORT).show();
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
        if(broadcastReceivers != null) {
            for (MyBroadcastReceiver br : broadcastReceivers) {
                getActivity().getApplicationContext().unregisterReceiver(br);
            }
            broadcastReceivers = null;
        }
    }
}
