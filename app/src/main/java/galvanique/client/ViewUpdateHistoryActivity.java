package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;

import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.MoodLog;

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
    }
}
