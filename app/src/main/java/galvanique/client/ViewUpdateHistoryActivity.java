package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;

import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.MoodLog;
import android.widget.EditText;
import android.widget.Button;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ViewUpdateHistoryActivity extends Activity {
    TableLayout table;

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


        // TODO-dave,clark display this array as a table with the information we mention in the SDS
        //Outer Loop
        for (int i = 0; i < moodLogs.length; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            addRowElement(Integer.toString(moodLogs[i].getId()), row);
            addRowElement(Integer.toString(moodLogs[i].getMagnitude()), row);
            addRowElement(Integer.toString(moodLogs[i].getMagnitude()), row);
            addRowElement(Integer.toString(moodLogs[i].getTrigger()), row);
            addRowElement(Integer.toString(moodLogs[i].getBelief()), row);
            addRowElement(Integer.toString(moodLogs[i].getBehavior()), row);
            addRowElement(Long.toString(moodLogs[i].getTimestamp()), row);


            table.addView(row);
        }

    }

    public void addRowElement (String s, TableRow r) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        //tv.setBackgroundResource(R.drawable.cell_shape);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(18);
        tv.setPadding(0, 5, 0, 5);
        tv.setText(s);
        r.addView(tv);
    }
}

