package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;

import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.MoodLog;
import android.widget.EditText;
import android.widget.Button;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Spinner;

public class ViewUpdateHistoryActivity extends Activity {
    TableLayout table;

    private Spinner dropdown;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_update_history);

        table = (TableLayout) findViewById(R.id.tableLayout);

        // Read all rows of MoodLog table into an array of MoodLog objects
        MoodLogDAO dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbMoodLog.openRead();
        MoodLog[] moodLogs = dbMoodLog.getAllMoods();
        dbMoodLog.close();


        //create the dropdown for the different logs
        dropdown = (Spinner) findViewById(R.id.spinner);
        //pull all of the timestamps from the moodlog array and put them in a new array
        String[] timeStamps = new String[moodLogs.length];
        for (int i = 0; i < timeStamps.length; i++) {
            timeStamps[i] = Long.toString(moodLogs[i].getTimestamp());
        }
        //use this new array to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeStamps);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                if (item instanceof String) {
                    timestamp = (String) item;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // TODO-dave,clark display this array as a table with the information we mention in the SDS
        //Outer Loop
        TableRow topRow = new TableRow(this);
        topRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addRowElement("Mood Id", topRow);
        addRowElement("Magnitude", topRow);
        addRowElement("Trigger", topRow);
        addRowElement("Belief", topRow);
        addRowElement("Behavior", topRow);
//        addRowElement("Comments", topRow);
        addRowElement("Timestamp", topRow);

        table.addView(topRow);

        for (int i = 0; i < moodLogs.length; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            addRowElement(Integer.toString(moodLogs[i].getId()), row);
            addRowElement(Integer.toString(moodLogs[i].getMagnitude()), row);
            addRowElement(Integer.toString(moodLogs[i].getTrigger()), row);
            addRowElement(Integer.toString(moodLogs[i].getBelief()), row);
            addRowElement(Integer.toString(moodLogs[i].getBehavior()), row);
//            addRowElement(moodLogs[i].getComments(), row);
            addRowElement(Long.toString(moodLogs[i].getTimestamp()), row);

            Log.d(Integer.toString(i), Integer.toString(moodLogs[i].getId()));

            table.addView(row);
        }

    }

    public void addRowElement (String s, TableRow r) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        tv.setBackgroundResource(R.drawable.cell_shape);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setPadding(0, 5, 0, 5);
        tv.setText(s);
        r.addView(tv);
    }
}

