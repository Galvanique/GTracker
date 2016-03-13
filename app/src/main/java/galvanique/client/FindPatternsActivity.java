package galvanique.client;


import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.androidplot.LineRegion;
import com.androidplot.ui.*;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import galvanique.db.MoodDAO;
import galvanique.db.MoodLog;

// TODO http://androidplot.com/

public class FindPatternsActivity extends Activity {

    private MoodDAO db;

    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;

    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
    private SeekBar sbFixedWidth, sbVariableWidth;

    private XYSeries series1, series2, series3;

    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers, series2Numbers, series3Numbers;

    private MyBarFormatter formatter1, formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        // TODO Update this with the real moods
        db = new MoodDAO(getApplicationContext());
        db.openRead();
        // moodOne
        series1Numbers = new Number[]{db.getMoodByType(0).length};
        // moodTwo
        series2Numbers = new Number[]{db.getMoodByType(1).length};
        // moodThree
        series3Numbers = new Number[]{db.getMoodByType(2).length};
        db.close();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patterns);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        formatter1 = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        // TODO you can use this to define new colors for different moods
        formatter2 = new MyBarFormatter(Color.argb(200, 100, 100, 150), Color.LTGRAY);
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new Size(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, XLayoutStyle.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();


        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        plot.getGraphWidget().getGridBox().setPadding(30, 10, 30, 0);

        plot.setTicksPerDomainLabel(2);

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

        spRenderStyle = (Spinner) findViewById(R.id.spRenderStyle);
        ArrayAdapter <BarRenderer.BarRenderStyle> adapter = new ArrayAdapter <BarRenderer.BarRenderStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarRenderStyle.values() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRenderStyle.setAdapter(adapter);
        spRenderStyle.setSelection(BarRenderer.BarRenderStyle.OVERLAID.ordinal());
        spRenderStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                updatePlot();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spWidthStyle = (Spinner) findViewById(R.id.spWidthStyle);
        ArrayAdapter <BarRenderer.BarWidthStyle> adapter1 = new ArrayAdapter <BarRenderer.BarWidthStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarWidthStyle.values() );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWidthStyle.setAdapter(adapter1);
        spWidthStyle.setSelection(BarRenderer.BarWidthStyle.FIXED_WIDTH.ordinal());
        spWidthStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if (BarRenderer.BarWidthStyle.FIXED_WIDTH.equals(spWidthStyle.getSelectedItem())) {
                    sbFixedWidth.setVisibility(View.VISIBLE);
                    sbVariableWidth.setVisibility(View.INVISIBLE);
                } else {
                    sbFixedWidth.setVisibility(View.INVISIBLE);
                    sbVariableWidth.setVisibility(View.VISIBLE);
                }
                updatePlot();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        sbFixedWidth = (SeekBar) findViewById(R.id.sbFixed);
        sbFixedWidth.setProgress(50);
        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {updatePlot();}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        sbVariableWidth = (SeekBar) findViewById(R.id.sbVariable);
        sbVariableWidth.setProgress(1);
        sbVariableWidth.setVisibility(View.INVISIBLE);
        sbVariableWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {updatePlot();}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                int year = (int) (value + 0.5d) / 12;
                int month = (int) ((value + 0.5d) % 12);
                return new StringBuffer(DateFormatSymbols.getInstance().getShortMonths()[month] + " '0" + year);
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
        updatePlot();

    }

    private void updatePlot() {

        // Remove all current series from each plot
        plot.clear();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "moodOne");
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "moodTwo");
        series3 = new SimpleXYSeries(Arrays.asList(series3Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "moodThree");

        // add a new series' to the xyplot:
        plot.addSeries(series1, formatter1);
        plot.addSeries(series2, formatter1);
        plot.addSeries(series3, formatter1);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer)plot.getRenderer(MyBarRenderer.class));
        renderer.setBarRenderStyle((BarRenderer.BarRenderStyle)spRenderStyle.getSelectedItem());
        renderer.setBarWidthStyle((BarRenderer.BarWidthStyle)spWidthStyle.getSelectedItem());
        renderer.setBarWidth(sbFixedWidth.getProgress());
        renderer.setBarGap(sbVariableWidth.getProgress());

        if (BarRenderer.BarRenderStyle.STACKED.equals(spRenderStyle.getSelectedItem())) {
            plot.setRangeTopMin(15);
        } else {
            plot.setRangeTopMin(0);
        }

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.getGraphWidget().getGridDimensions().marginatedRect.contains(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;


            // find the closest value to the selection:
            for (SeriesAndFormatter<XYSeries, ? extends XYSeriesFormatter> sfPair : plot.getSeriesRegistry()) {
                XYSeries series = sfPair.getSeries();
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if(selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }

    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection getFormatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if(selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }

}
