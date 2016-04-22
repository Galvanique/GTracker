package galvanique.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import galvanique.db.dao.BehaviorDAO;
import galvanique.db.dao.BeliefDAO;
import galvanique.db.dao.MoodDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.Behavior;
import galvanique.db.entities.Belief;
import galvanique.db.entities.MoodLog;
import galvanique.db.entities.Trigger;

public class ViewUpdateHistoryActivity extends AppCompatActivity {
    TableLayout table;

    private Spinner dropdown;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_update_history);

        // DB
        MoodLogDAO dbMoodLog = new MoodLogDAO(getApplicationContext());
        MoodDAO dbMood = new MoodDAO(getApplicationContext());
        TriggerDAO dbTrigger = new TriggerDAO(getApplicationContext());
        BeliefDAO dbBelief = new BeliefDAO(getApplicationContext());
        BehaviorDAO dbBehavior = new BehaviorDAO(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        table = (TableLayout) findViewById(R.id.tableLayout);

        // Read all rows of MoodLog table into an array of MoodLog objects
        dbMoodLog.openRead();
        MoodLog[] moodLogs = dbMoodLog.getAllMoods();
        dbMoodLog.close();


        //create the dropdown for the different logs
        dropdown = (Spinner) findViewById(R.id.spinner);
        //pull all of the timestamps from the moodlog array and put them in a new array
        String[] timeStamps = new String[moodLogs.length];
        for (int i = 0; i < timeStamps.length; i++) {
            timeStamps[i] = MoodLogDAO.getISOTimeString(moodLogs[i].getTimestamp());
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

            dbMood.openRead();
            dbTrigger.openRead();
            dbBelief.openRead();
            dbBehavior.openRead();
            Trigger trigger = dbTrigger.getTriggerById(moodLogs[i].getTrigger());
            Belief belief = dbBelief.getBeliefById(moodLogs[i].getBelief());
            Behavior behavior = dbBehavior.getBehaviorById(moodLogs[i].getBehavior());
            addRowElement(moodLogs[i].getMoodString(), row);
            addRowElement(Integer.toString(moodLogs[i].getMagnitude()), row);
            addRowElement(trigger.name, row);
            addRowElement(belief.name, row);
            addRowElement(behavior.name, row);
//            addRowElement(moodLogs[i].getComments(), row);
            addRowElement(MoodLogDAO.getISOTimeString(moodLogs[i].getTimestamp()), row);
            dbMood.close();
            dbTrigger.close();
            dbBelief.close();
            dbBehavior.close();

            Log.d(Integer.toString(i), Integer.toString(moodLogs[i].getId()));

            table.addView(row);
        }

    }

    public void addRowElement(String s, TableRow r) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        tv.setBackgroundResource(R.drawable.cell_shape);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(11);
        tv.setPadding(0, 5, 0, 5);
        tv.setText(s);
        r.addView(tv);
    }
}

