package galvanique.client;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;
import java.util.*;

import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.MoodLog;

public class TrackProgressActivity extends AppCompatActivity {
    private XYPlot mySimpleXYPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MoodLogDAO dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbMoodLog.openRead();

        MoodLog[] moodLogArray = dbMoodLog.getMoodsOverTime();
        dbMoodLog.close();

        //Create a list of lists of MoodLogs and sort them into categories based on mood
        ArrayList<ArrayList<MoodLog>> MoodsList = new ArrayList<ArrayList<MoodLog>>();

        ArrayList<MoodLog> moodLogs = new ArrayList<>(Arrays.asList(moodLogArray));
        int idx = 0;
        int windowStart;
        ArrayList<MoodLog> currentWindow;
        ArrayList<ArrayList<MoodLog>> windows = new ArrayList<>();
        String currentMood;
        while (idx < moodLogArray.length) {
            currentMood = moodLogArray[idx].getMoodString();
            windowStart = idx;
            while (idx < moodLogArray.length && moodLogArray[idx].getMoodString().equals(currentMood)) {
                idx++;
            }
            currentWindow = new ArrayList<>(moodLogs.subList(windowStart, idx));
            windows.add(currentWindow);
        }

        // initialize our XYPlot reference:
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        //clean up axes
        mySimpleXYPlot.getGraphWidget().setHeight(70);
        mySimpleXYPlot.getGraphWidget().setMarginBottom(100);
        // Create a couple arrays of y-values to plot:
        //int[][] Series = new int[MoodsList.size()][];
        //Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        //Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

        //Create Series based on magnitudes and add them to the plot
        for(int i = 0; i<MoodsList.size(); i++){
            ArrayList<MoodLog> tempMood = MoodsList.get(i);
            Number[] tempMagnitudes = new Number[tempMood.size()];
            long[] tempTimestamps = new long[tempMood.size()];

            for(int k = 0; k < tempMood.size(); k++){
                tempMagnitudes[k] = tempMood.get(k).getMagnitude();
                tempTimestamps[k] = tempMood.get(k).getTimestamp();
            }
            XYSeries tempSeries = new SimpleXYSeries(Arrays.asList(tempMagnitudes),
                                                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                                                    tempMood.get(0).getMoodString());

            mySimpleXYPlot.addSeries(tempSeries, new LineAndPointFormatter(
                    Color.rgb(0, 0, 200),
                    Color.rgb(0, 0, 100),
                    null,
                    new PointLabelFormatter(Color.WHITE)));
        }

        // Turn the above arrays into XYSeries':
        //XYSeries series1 = new SimpleXYSeries(
          //      Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
            //    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
              //  "Series1");                             // Set the display title of the series

        // same as above
        //XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");



        // Create a formatter to use for drawing a series using LineAndPointRenderer:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter(
//                Color.rgb(0, 200, 0),                   // line color
//                Color.rgb(0, 100, 0),                   // point color
//                null,                                   // fill color (none)
//                new PointLabelFormatter(Color.WHITE));                           // text color
//
//        // add a new series' to the xyplot:
//        mySimpleXYPlot.addSeries(series1, series1Format);
//
//        // same as above:
//        mySimpleXYPlot.addSeries(series2,
//                new LineAndPointFormatter(
//                        Color.rgb(0, 0, 200),
//                        Color.rgb(0, 0, 100),
//                        null,
//                        new PointLabelFormatter(Color.WHITE)));

        // reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);
    }
}
