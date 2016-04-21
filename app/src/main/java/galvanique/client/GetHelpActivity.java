package galvanique.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.channguyen.rsv.RangeSliderView;
import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;
import galvanique.db.entities.MoodLog;

public class GetHelpActivity extends AppCompatActivity {

    /**
     * Declarations
     */
    private Button buttonYes, buttonOkay;
    private TextView textViewInstructions, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime;
    private boolean alreadyUsingStrategy = false;
    private MoodLogDAO dbMoodLog;
    private CopingStrategyLogDAO dbStrategyLog;
    private String moodName, csName, selectedStrategy;
    private Spinner dropdownStrategies;
    private RangeSliderView rsv;
    private int effectiveness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewInstructions = (TextView) findViewById(R.id.instructions);
        textViewMood = (TextView) findViewById(R.id.textViewMood);
        textViewTrigger = (TextView) findViewById(R.id.textViewTrigger);
        textViewBelief = (TextView) findViewById(R.id.textViewBelief);
        textViewBehavior = (TextView) findViewById(R.id.textViewBehavior);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        dropdownStrategies = (Spinner) findViewById(R.id.spinner);

        rsv = (RangeSliderView) findViewById(R.id.rsv_large);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                effectiveness = index;
            }
        };
        rsv.setOnSlideListener(listener);
        //rsv.setRangeCount(10);

        buttonYes = (Button) findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                textViewInstructions.setText("Please select a coping strategy.");
                makeEverythingInvisible();
                dropdownStrategies.setVisibility(View.VISIBLE);
                buttonYes.setVisibility(View.GONE);
                buttonOkay.setVisibility(View.VISIBLE);
            }
        });

        buttonOkay = (Button) findViewById(R.id.buttonOkay);
        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // When user is finished selecting a coping strategy, they press okay to confirm
                int copingStratID = dropdownStrategies.getSelectedItemPosition();
                dbMoodLog.openRead();
                MoodLog mostRecent = dbMoodLog.getMostRecentLog();
                dbMoodLog.close();
                CopingStrategyLog insertion = new CopingStrategyLog(mostRecent.getId(), copingStratID, -1, System.currentTimeMillis());
                dbStrategyLog.openWrite();
                dbStrategyLog.insert(insertion);
                dbStrategyLog.close();
                textViewInstructions.setText("You are already using the coping strategy " +
                        "\"" + dropdownStrategies.getSelectedItem() + "\" for mood " + "\"" + mostRecent.getMoodString() + "\". How is it going?");
                makeEverythingInvisible();
                rsv.setVisibility(View.VISIBLE);
            }
        });

        dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbStrategyLog = new CopingStrategyLogDAO(getApplicationContext());

        dbMoodLog.openRead();
        if (dbMoodLog.getCountMoodLogs() > 0) {
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
            dropdownStrategies.setVisibility(View.GONE);
        }

        // Check if coping strategy is in use already (last log timestamp + its duration > currentTime)
        dbStrategyLog.openRead();
        // If the copingStrategyLogs table is empty, you're not using a strategy already
        if (dbStrategyLog.getCountCopingStrategyLogs() > 0) {
            CopingStrategyLog lastLog = dbStrategyLog.getMostRecentLog();
            long lastTime = lastLog.getTimestamp();
            int lastStrategyID = lastLog.getCopingStrategyID();
            CopingStrategyDAO dbStrategy = new CopingStrategyDAO(getApplicationContext());
            dbStrategy.openRead();
            CopingStrategy lastStrategy = dbStrategy.getCopingStrategyById(lastStrategyID);
            dbStrategy.close();
            long lastDuration = lastStrategy.duration;
            csName = lastStrategy.name;
            if ((lastTime + lastDuration) > System.currentTimeMillis()) {
                alreadyUsingStrategy = true;
            }
        }
        dbStrategyLog.close();

        makeEverythingInvisible();

        // If there is already a coping strategy, ask user to rate effectiveness, update log entry
        if (alreadyUsingStrategy) {
            dbMoodLog.openRead();
            moodName = dbMoodLog.getMostRecentLog().getMoodString();
            dbMoodLog.close();
            textViewInstructions.setText("You are already using the coping strategy " +
                    "\"" + csName + "\" for mood " + "\"" + moodName + "\". How is it going?");
            rsv.setVisibility(View.VISIBLE);
        }
        // Otherwise they have no strategy
        else {
            // Check if there are any mood logs yet; if so, display most recent and ask if they want a strategy
            dbMoodLog.open();
            if (dbMoodLog.getCountMoodLogs() > 0) {
                textViewInstructions.setText("This is your last mood log. Would you like a coping strategy for this mood?");
                final MoodLog mostRecent = dbMoodLog.getMostRecentLog();
                // TODO-tyler replace ids with text
                textViewMood.setVisibility(View.VISIBLE);
                textViewTrigger.setVisibility(View.VISIBLE);
                textViewBelief.setVisibility(View.VISIBLE);
                textViewBehavior.setVisibility(View.VISIBLE);
                textViewTime.setVisibility(View.VISIBLE);
                buttonYes.setVisibility(View.VISIBLE);
                textViewMood.setText("Mood: " + mostRecent.getMoodString());
                textViewTrigger.setText("Trigger: " + mostRecent.getTrigger());
                textViewBelief.setText("Belief: " + mostRecent.getBelief());
                textViewBehavior.setText("Behavior: " + mostRecent.getBehavior());
                String timeInDate = CopingStrategyLogDAO.getISOTimeString(mostRecent.getTimestamp());
                textViewTime.setText("Time: " + timeInDate);
            }
            // If there are no mood logs yet, tell them they need to log a mood to get a strategy
            else {
                textViewInstructions.setText("Please log a mood to get a coping strategy!");
            }
            dbMoodLog.close();
        }
    }

    public void makeEverythingInvisible() {
        textViewMood.setVisibility(View.GONE);
        textViewTrigger.setVisibility(View.GONE);
        textViewBelief.setVisibility(View.GONE);
        textViewBehavior.setVisibility(View.GONE);
        textViewTime.setVisibility(View.GONE);
        buttonOkay.setVisibility(View.GONE);
        buttonYes.setVisibility(View.GONE);
        dropdownStrategies.setVisibility(View.GONE);
    }

}
