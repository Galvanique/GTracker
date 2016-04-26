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
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;
import galvanique.db.entities.MoodLog;


public class GetHelpActivity extends AppCompatActivity {

    private enum State {
        IN_USE, NO_MOOD_LOGS, NOT_IN_USE, SELECT
    }

    // Declarations
    private State state;
    private Button buttonYes, buttonOkay;
    private TextView textViewInstructions, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime;
    private MoodLogDAO dbMoodLog;
    private CopingStrategyLogDAO dbStrategyLog;
    private Spinner dropdownStrategies;
    private RangeSliderView rsv;
    private int effectiveness;
    private String selectedStrategy;

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
        textViewMagnitude = (TextView) findViewById(R.id.textViewMagnitude);
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

        // Asks if user wants a coping strategy, if so redirects to dropdown of strategies
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
                    CopingStrategyDAO csDAO = new CopingStrategyDAO(getApplicationContext());
                    csDAO.openRead();
                    int copingStratID = (csDAO.getCopingStrategyByString(selectedStrategy)); // TODO this is wrong
                    csDAO.close();
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
        }
        dbMoodLog.close();
        dbStrategyLog.close();

        // Determine path of execution
        if (noMoodLogs()) { // There are no mood logs
            setUpLayout(State.NO_MOOD_LOGS);
        } else { // There are mood logs
            if (!(checkIfLastMoodHasLog())) {
                setUpLayout(State.NOT_IN_USE);
            } else { // There are mood logs and they have a CS
                if (checkIfInUse()) {
                    if (checkIfRated()) { // They have a mood log and it has a CS and it's rated
                        setUpLayout(State.NO_MOOD_LOGS);
                    } else { // They have a mood log and it has a CS, but it's not rated
                        setUpLayout(State.IN_USE);
                    }
                } else { // They have finished using it, ask them to log a new mood
                    setUpLayout(State.NO_MOOD_LOGS);
                }
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
                String mood = dbMoodLog.getMoodLogById(lastLog.getMoodLogID()).getMoodString();
                dbMoodLog.close();
                textViewInstructions.setText("You are already using the coping strategy " +
                        "\"" + strategy + "\" for mood " + "\"" + mood + "\". How is it going?");
                changeVisibility(View.VISIBLE, rsv);
                changeVisibility(View.GONE, dropdownStrategies, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime, buttonYes);
                break;
            case NO_MOOD_LOGS:
                textViewInstructions.setText("Please log a mood to get a coping strategy!");
                changeVisibility(View.GONE, buttonYes, buttonOkay, dropdownStrategies, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
                changeVisibility(View.INVISIBLE, rsv);
                break;
            case NOT_IN_USE:
                dbMoodLog.openRead();
                String lastMood = dbMoodLog.getMostRecentLog().getMoodString();
                dbMoodLog.close();
                if (lastMood.equals("Happy") || lastMood.equals("Hopeful") || lastMood.equals("Grateful") || lastMood.equals("Excited")) {
                    textViewInstructions.setText("You have a good mood -- enjoy it!");
                    changeVisibility(View.GONE, buttonOkay, buttonYes, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime, dropdownStrategies);
                    changeVisibility(View.INVISIBLE, rsv);
                } else {
                    changeVisibility(View.VISIBLE, buttonYes, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
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
                    textViewMagnitude.setText("Magnitude: " + mostRecentLog.getMagnitude());
                    textViewTrigger.setText("Trigger: " + trigger);
                    textViewBelief.setText("Belief: " + belief);
                    textViewBehavior.setText("Behavior: " + behavior);
                    String timeInDate = CopingStrategyLogDAO.getISOTimeString(mostRecentLog.getTimestamp());
                    textViewTime.setText("Time: " + timeInDate);
                }
                break;
            case SELECT:
                textViewInstructions.setText("Please select a coping strategy.");
                changeVisibility(View.VISIBLE, buttonOkay, dropdownStrategies);
                changeVisibility(View.GONE, buttonYes, textViewMood, textViewMagnitude, textViewTrigger, textViewBelief, textViewBehavior, textViewTime);
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
            Log.d("In use?", ((lastTime + lastDuration) > System.currentTimeMillis())+"");
            return ((lastTime + lastDuration) > System.currentTimeMillis());
        }
        Log.d("In use?", false+"");
        return false;
    }

    public boolean checkIfRated() {
        boolean check;
        dbStrategyLog.openRead();
        if (dbStrategyLog.getCountCopingStrategyLogs() > 0) {
            check = dbStrategyLog.getMostRecentLog().effectiveness > -1;
        } else {
            check = false;
        }
        dbStrategyLog.close();
        Log.d("check if rated returned", check+"");
        return check;
    }

    public boolean checkIfLastMoodHasLog() {
        boolean check = true;
        dbMoodLog.openRead();
        if (dbMoodLog.getCountMoodLogs() > 0) {
            MoodLog mostRecent = dbMoodLog.getMostRecentLog();
            dbStrategyLog.openRead();
            check = dbStrategyLog.checkIfMoodLogHasStrategy(mostRecent.getId());
            dbStrategyLog.close();
        }
        dbMoodLog.close();
        Log.d("check if mood has cs", check+"");
        return check;
    }

    public boolean noMoodLogs() {
        dbMoodLog.openRead();
        boolean check = dbMoodLog.getCountMoodLogs() == 0;
        dbMoodLog.close();
        Log.d("check no mood logs", check+"");
        return check;
    }

    public void doToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void changeVisibility(int visibility, View... elements) {
        for (View v : elements) {
            v.setVisibility(visibility);
        }
    }
}
