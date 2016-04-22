package galvanique.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.github.channguyen.rsv.RangeSliderView;
import galvanique.db.dao.BehaviorDAO;
import galvanique.db.dao.BeliefDAO;
import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.MoodDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;
import galvanique.db.entities.MoodLog;

public class GetHelpActivity extends AppCompatActivity {

    private enum State {
        IN_USE, NO_MOOD_LOGS, NOT_IN_USE, SELECT;
    }

    // Declarations
    private State state;
    private Button buttonYes, buttonOkay;
    private TextView textViewInstructions, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime;
    private MoodLogDAO dbMoodLog;
    private CopingStrategyLogDAO dbStrategyLog;
    private Spinner dropdownStrategies;
    private RangeSliderView rsv;
    private int effectiveness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
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
        buttonYes = (Button) findViewById(R.id.buttonYes);
        buttonOkay = (Button) findViewById(R.id.buttonOkay);

        // DB
        dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbStrategyLog = new CopingStrategyLogDAO(getApplicationContext());

        // Listeners
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                effectiveness = index;
            }
        };
        rsv.setOnSlideListener(listener);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                setUpLayout(State.SELECT);
            }
        });

        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                if (state == State.SELECT) {
                    // When user is finished selecting a coping strategy, they press okay to confirm
                    int copingStratID = dropdownStrategies.getSelectedItemPosition();
                    dbMoodLog.openRead();
                    MoodLog mostRecent = dbMoodLog.getMostRecentLog();
                    dbMoodLog.close();
                    CopingStrategyLog insertion = new CopingStrategyLog(mostRecent.getId(), copingStratID, -1, System.currentTimeMillis());
                    dbStrategyLog.openWrite();
                    dbStrategyLog.insert(insertion);
                    dbStrategyLog.close();
                    doToast("Coping strategy successfully logged.");
                    setUpLayout(State.IN_USE);
                } else if (state == State.IN_USE) {
                    dbStrategyLog.open();
                    CopingStrategyLog mostRecent = dbStrategyLog.getMostRecentLog();
                    dbStrategyLog.updateEffectiveness(mostRecent, effectiveness);
                    dbStrategyLog.close();
                    doToast("Effectiveness successfully updated.");
                    setUpLayout(State.NO_MOOD_LOGS);
                }
            }
        });

        dbMoodLog.openRead();
        dbStrategyLog.openRead();
        // Determine if in use
        if (dbMoodLog.getCountMoodLogs() > 0) {
            String[] strategies = dbStrategyLog.getBestCopingStrategyNamesByMood(dbMoodLog.getMostRecentLog().getMoodID());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strategies);
            dropdownStrategies.setAdapter(adapter);
        }
        dbMoodLog.close();
        dbStrategyLog.close();

        // Determine path of execution
        if (checkIfInUse() || noMoodLogs()) {
            setUpLayout(State.NO_MOOD_LOGS);
        } else {
            if (checkIfRated()) {
                setUpLayout(State.NO_MOOD_LOGS);
            } else {
                setUpLayout(State.NOT_IN_USE);
            }
        }
    }

    public void setUpLayout(State s) {
        state = s;
        switch (s) {
            case IN_USE:
                CopingStrategyDAO dbStrategy = new CopingStrategyDAO(getApplicationContext());
                dbStrategyLog.openRead();
                CopingStrategyLog lastLog = dbStrategyLog.getMostRecentLog();
                dbStrategyLog.close();
                dbStrategy.openRead();
                String strategy = dbStrategy.getCopingStrategyById(lastLog.getCopingStrategyID()).name;
                dbStrategy.close();
                dbMoodLog.openRead();
                String mood = dbMoodLog.getMoodById(lastLog.getMoodLogID()).getMoodString();
                dbMoodLog.close();
                textViewInstructions.setText("You are already using the coping strategy " +
                        "\"" + strategy + "\" for mood " + "\"" + mood + "\". How is it going?");
                changeVisibility(View.VISIBLE, rsv);
                changeVisibility(View.GONE, dropdownStrategies, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime, buttonYes);
                break;
            case NO_MOOD_LOGS:
                textViewInstructions.setText("Please log a mood to get a coping strategy!");
                changeVisibility(View.GONE, buttonYes, buttonOkay, dropdownStrategies, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
                changeVisibility(View.INVISIBLE, rsv);
                break;
            case NOT_IN_USE:
                changeVisibility(View.VISIBLE, buttonYes, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
                changeVisibility(View.GONE, buttonOkay, dropdownStrategies);
                changeVisibility(View.INVISIBLE, rsv);
                textViewInstructions.setText("This is your last mood log. Would you like a coping strategy for this mood?");
                dbMoodLog.openRead();
                MoodLog mostRecentLog = dbMoodLog.getMostRecentLog();
                dbMoodLog.close();
                TriggerDAO dbTrigger = new TriggerDAO(getApplicationContext());
                BeliefDAO dbBelief = new BeliefDAO(getApplicationContext());
                BehaviorDAO dbBehavior = new BehaviorDAO(getApplicationContext());
                dbTrigger.openRead();
                dbBelief.openRead();
                dbBehavior.openRead();
                String trigger = dbTrigger.getTriggerById(mostRecentLog.getTrigger()).name;
                String belief = dbBelief.getBeliefById(mostRecentLog.getBelief()).name;
                String behavior = dbBehavior.getBehaviorById(mostRecentLog.getBehavior()).name;
                dbTrigger.close();
                dbBelief.close();
                dbBehavior.close();
                textViewMood.setText("Mood: " + mostRecentLog.getMoodString());
                textViewTrigger.setText("Trigger: " + trigger);
                textViewBelief.setText("Belief: " + belief);
                textViewBehavior.setText("Behavior: " + behavior);
                String timeInDate = CopingStrategyLogDAO.getISOTimeString(mostRecentLog.getTimestamp());
                textViewTime.setText("Time: " + timeInDate);
                break;
            case SELECT:
                textViewInstructions.setText("Please select a coping strategy.");
                changeVisibility(View.VISIBLE, buttonOkay, dropdownStrategies);
                changeVisibility(View.GONE, buttonYes, textViewMood, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
                changeVisibility(View.INVISIBLE, rsv);
                break;
            default:
                throw new RuntimeException("Invalid get help state");
        }
    }

    public boolean checkIfInUse() {
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
            dbStrategyLog.close();
            return ((lastTime + lastDuration) < System.currentTimeMillis());
        }
        return false;
    }

    public boolean checkIfRated() {
        boolean check;
        dbStrategyLog.openRead();
        if (dbStrategyLog.getCountCopingStrategyLogs() > 0) {
            check = dbStrategyLog.getMostRecentLog().effectiveness > -1;
        }
        else {
            check = false;
        }
        dbStrategyLog.close();
        return check;
    }

    public boolean noMoodLogs() {
        dbMoodLog.openRead();
        boolean check = dbMoodLog.getCountMoodLogs() == 0;
        dbMoodLog.close();
        return check;
    }

    public void doToast(String s) {
        Toast.makeText (getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void changeVisibility(int visibility, View... elements) {
        for (View v : elements) { v.setVisibility(visibility); }
    }
}
