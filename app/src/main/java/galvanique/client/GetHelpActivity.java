package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.CopingStrategyLogDefaultDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;

public class GetHelpActivity extends Activity {

    /**
     * Various UI components
     */
    private Button buttonYes;
    private TextView textViewInstructions;
    private boolean alreadyUsingStrategy = false;
    private MoodLogDAO dbMoodLog;
    private CopingStrategyDAO dbStrategy;
    private CopingStrategyLogDAO dbStrategyLog;
    private CopingStrategyLogDefaultDAO dbStrategyLogDefault;
    private String moodName, csName, selectedStrategy;
    private Spinner dropdownStrategies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbStrategy = new CopingStrategyDAO(getApplicationContext());
        dbStrategyLog = new CopingStrategyLogDAO(getApplicationContext());
        dbStrategyLogDefault = new CopingStrategyLogDefaultDAO(getApplicationContext());

        dropdownStrategies = (Spinner) findViewById(R.id.spinner);
        dbStrategyLog.openRead();
        dbMoodLog.openRead();
        String[] strategies = dbStrategyLog.getBestCopingStrategyNamesByMood(dbMoodLog.getMostRecentLog().getMoodID());
        dbMoodLog.close();
        dbStrategyLog.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strategies);
        dropdownStrategies.setAdapter(adapter);
        dropdownStrategies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                if (item instanceof String) {
                    selectedStrategy = (String) item;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Check if coping strategy is in use already (last log timestamp + its duration < currentTime)
        dbStrategyLog.openRead();
        // If the copingStrategyLogs table is empty, you're not using a strategy already
        if (dbStrategyLog.getCountCopingStrategyLogs() > 0) {
            CopingStrategyLog lastLog = dbStrategyLog.getMostRecentLog();
            long lastTime = lastLog.getTimestamp();
            int lastStrategyID = lastLog.getCopingStrategyID();
            dbStrategy.openRead();
            CopingStrategy lastStrategy = dbStrategy.getCopingStrategyById(lastStrategyID);
            dbStrategy.close();
            long lastDuration = lastStrategy.duration;
            if ((lastTime + lastDuration) < System.currentTimeMillis()) {
                alreadyUsingStrategy = true;
                csName = lastStrategy.name;
            }
        }
        dbStrategyLog.close();

        if (alreadyUsingStrategy) {
            dbMoodLog.openRead();
            moodName = dbMoodLog.getMostRecentLog().getMoodString();
            dbMoodLog.close();
            textViewInstructions.setText("You are already using the coping strategy " +
                    "\"" + csName + "\" for mood " + moodName + ". How is it going?");
        } else {
            buttonYes = (Button) findViewById(R.id.buttonYes);
            buttonYes.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onClick(View v) {
                    // TODO-tyler display list of strategies, create new CopingStrategyLog based on choice
                }
            });
        }
    }

}
