
package com.example.abdul.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    protected BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private int[] calories;
    private Typeface mTf;
    private int day=0;
    private long pid;
    private int year;
    private int month;
    private String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    private int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        Bundle extras=getIntent().getExtras();
        day=extras.getInt("day");
        pid=extras.getLong("pid");
        year=extras.getInt("year");
        month=extras.getInt("month");
        check=extras.getInt("check");
        JSONObject CList=new JSONObject();
        CList=Util.getJSONObject(extras.getString("graphlist"));
        calories=new int[day+1];
        for(int i=0;i<=day;i++)
        {
            calories[i]=0;
        }
        JSONArray CArray = (JSONArray) CList.get("consumptions");
        for (Object consumption : CArray) {
            JSONObject obj = (JSONObject) consumption;
            calories[Integer.parseInt(obj.get("date").toString())]=Integer.parseInt(obj.get("consumption").toString());
        }


        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarX.setMax(day-1);
        tvX.setText("1");
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(18f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        setData(day, 50);
        mChart.animateY(2500);
        // setting data
        mSeekBarY.setProgress(50);
        mSeekBarX.setProgress(1);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);
        // mChart.setDrawLegend(false);
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress()+1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress()+1, mSeekBarY.getProgress());
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            if(day>12)
                xVals.add(Integer.toString(i+1));
            else
                xVals.add(mMonths[i]);

        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            int val = calories[i+1];
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals1);
            mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            if(check==1)
                set1 = new BarDataSet(yVals1, "Consumption (Kcal)");
            else
                set1 = new BarDataSet(yVals1, "Kitchen Activity");

            set1.setBarSpacePercent(35f);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTf);

            mChart.setData(data);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        if(check==1) {
            if (calories[e.getXIndex() + 1] == 0)
                return;
            else {
                Intent intent = new Intent(BarChartActivity.this, LoadingScreenGraph2.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                intent.putExtra("pid", pid);
                intent.putExtra("check", check);
                intent.putExtra("position", e.getXIndex() + 1);
                startActivity(intent);
            }
        }
        RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, AxisDependency.LEFT);
        //Util.showAlert("warning",Integer.toString(e.getXIndex()),BarChartActivity.this);
        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleXIndex() + ", high: "
                        + mChart.getHighestVisibleXIndex());
    }

    public void onNothingSelected() {
    };

    public void onBackPressed() {
        Intent setIntent = new Intent(BarChartActivity.this,MainGraph.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        return;
    }
}
