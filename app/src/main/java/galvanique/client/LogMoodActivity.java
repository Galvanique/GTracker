package galvanique.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.channguyen.rsv.RangeSliderView;

import galvanique.db.dao.BehaviorDAO;
import galvanique.db.dao.BeliefDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.MoodDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.Behavior;
import galvanique.db.entities.Belief;
import galvanique.db.entities.MoodLog;
import galvanique.db.entities.Trigger;

public class LogMoodActivity extends AppCompatActivity {

    private enum State {
        MOOD, MAGNITUDE, TRIGGER, BELIEF, BEHAVIOR, STRATEGY;
        private static State[] vals = values();

        public State next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }

        public State previous() {
            if (this.ordinal() != 0)
                return vals[(this.ordinal() - 1) % vals.length];
            else
                return MOOD;
        }
    }

    // DB
    private TriggerDAO dbTrigger;
    private BeliefDAO dbBelief;
    private BehaviorDAO dbBehavior;
    private MoodDAO dbMood;
    private MoodLogDAO dbMoodLog;

    private State state;
    private boolean readyToWrite = false;

    /**
     * MoodLog attributes
     */
    private String mood, selectedStrategy; // select from list
    private int magnitude; // slider
    private String trigger, belief, behavior; // text input

    /**
     * UI
     */
    private Spinner dropdown;
    private Spinner dropdownStrategies;
    private Button buttonNext, buttonBack;
    private RangeSliderView slider;
    private EditText editTextTrigger, editTextBelief, editTextBehavior;
    private TextView textViewInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_mood);

        state = State.MOOD;

        // DB
        dbTrigger = new TriggerDAO(getApplicationContext());
        dbBelief = new BeliefDAO(getApplicationContext());
        dbBehavior = new BehaviorDAO(getApplicationContext());
        dbMoodLog = new MoodLogDAO(getApplicationContext());
        dbMood = new MoodDAO(getApplicationContext());

        // Text views
        textViewInstructions = (TextView) findViewById(R.id.textViewInstructions);
        textViewInstructions.setText("Please select a mood.");
        textViewInstructions.setVisibility(View.VISIBLE);

        // Edit texts
        editTextTrigger = (EditText) findViewById(R.id.editTextTrigger);
        editTextBelief = (EditText) findViewById(R.id.editTextBelief);
        editTextBehavior = (EditText) findViewById(R.id.editTextBehavior);

        // Dropdown
        dropdown = (Spinner) findViewById(R.id.spinner);
        dbMood.openRead();
        String[] items = dbMood.getAllMoodNames();
        dbMood.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                if (item instanceof String) {
                    mood = (String) item;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dropdownStrategies = (Spinner) findViewById(R.id.spinnerStrategy);
        CopingStrategyLogDAO logDB = new CopingStrategyLogDAO(getApplicationContext());
        logDB.openRead();
        dbMoodLog.openRead();
        String[] strategies = logDB.getBestCopingStrategyNamesByMood(dbMoodLog.getMostRecentLog().getMoodID());
        dbMoodLog.close();
        logDB.close();
        ArrayAdapter<String> strategyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strategies);
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


        // Slider https://github.com/channguyen/range-slider-view
        slider = (RangeSliderView) findViewById(R.id.rsv_large);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                magnitude = index;
            }
        };
        slider.setOnSlideListener(listener);
        slider.setRangeCount(10);

        // Hide elements for other states
        slider.setVisibility(View.INVISIBLE);
        editTextTrigger.setVisibility(View.GONE);
        editTextBelief.setVisibility(View.GONE);
        editTextBehavior.setVisibility(View.GONE);

        // Next button
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // If we click "Submit" without filling out mood field
                if (state == State.BEHAVIOR && (mood == null || mood.equals(""))) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Mood not logged: Please select required mood value.",
                            Toast.LENGTH_LONG
                    ).show();
                    state = State.MOOD;
                }
                // If we click "Submit" after filling out at least mood field
                else if (state == State.BEHAVIOR && !(mood == null || mood.equals(""))) {
                    readyToWrite = true;
                    state = state.next();
                }
                // If state is STRATEGY, buttonNext has text "Yes" to accept a suggestion
                else if (state == State.STRATEGY) {
                    // TODO-tyler display list of strategies, create new CopingStrategyLog based on choice
                }
                // Normal behavior
                else state = state.next();
                setUpLayout(state);
            }
        });

        // Back button
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // If state is STRATEGY, buttonBack has text "No," should just return user to MOOD state
                if (state == State.STRATEGY) {
                    state = State.MOOD;
                }
                // Normal behavior
                else state = state.previous();
                setUpLayout(state);
            }
        });
        buttonBack.setVisibility(View.GONE);
    }

    /**
     * Alters visibility of UI elements according to what part of the logging process the user is on
     *
     * @param s desired UI state
     */
    public void setUpLayout(State s) {
        // TODO-tyler check for invalid inputs to edit text fields?
        switch (s) {
            case MOOD:
                // Set up MOOD UI elements
                textViewInstructions.setVisibility(View.VISIBLE);
                textViewInstructions.setText("Please select a mood.");
                editTextBehavior.setVisibility(View.GONE);
                slider.setVisibility(View.GONE);
                dropdown.setVisibility(View.VISIBLE);
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                buttonBack.setVisibility(View.GONE);
                break;
            case MAGNITUDE:
                textViewInstructions.setText("How intense was this feeling?");
                dropdown.setVisibility(View.GONE);
                editTextTrigger.setVisibility(View.GONE);
                slider.setVisibility(View.VISIBLE);
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                buttonBack.setVisibility(View.VISIBLE);
                break;
            case TRIGGER:
                textViewInstructions.setText("What triggered this mood? (Optional)");
                slider.setVisibility(View.GONE);
                editTextBelief.setVisibility(View.GONE);
                editTextTrigger.setVisibility(View.VISIBLE);
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                break;
            case BELIEF:
                textViewInstructions.setText("What did feeling this way make you think or believe? (Optional)");
                editTextTrigger.setVisibility(View.GONE);
                editTextBehavior.setVisibility(View.GONE);
                editTextBelief.setVisibility(View.VISIBLE);
                // Grab trigger text
                trigger = editTextTrigger.getText().toString();
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                break;
            case BEHAVIOR:
                textViewInstructions.setText("How did feeling this way make you behave? (Optional)");
                editTextBelief.setVisibility(View.GONE);
                editTextBehavior.setVisibility(View.VISIBLE);
                // Grab belief text
                belief = editTextBelief.getText().toString();
                buttonNext.setText("Submit");
                buttonBack.setText("Back");
                break;
            case STRATEGY:
                textViewInstructions.setText("Would you like a coping strategy suggestion?");
                buttonNext.setText("Yes");
                buttonBack.setText("No");
                editTextBehavior.setVisibility(View.GONE);
                // Grab behavior text
                behavior = editTextBehavior.getText().toString();
                if (readyToWrite) {
                    int[] ids = getIds(trigger, belief, behavior);
                    MoodLog insertion = new MoodLog(System.currentTimeMillis(), MoodLog.Mood.valueOf(mood), ids[0], ids[1], ids[2], magnitude, "");
                    dbMoodLog = new MoodLogDAO(getApplicationContext());
                    dbMoodLog.openWrite();
                    // Insert MoodLog using these trigger, belief, behavior IDs
                    dbMoodLog.insert(insertion);
                    dbMoodLog.close();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mood \"" + mood + "\" successfully logged.",
                            Toast.LENGTH_LONG
                    ).show();
                    // Reset UI and inputs
                    dropdown.setSelection(0);
                    slider.setInitialIndex(0);
                    editTextTrigger.setText("");
                    editTextBelief.setText("");
                    editTextBehavior.setText("");
                    mood = null;
                    trigger = null;
                    belief = null;
                    behavior = null;
                    magnitude = 0;
                }
                readyToWrite = false;
                break;
            default:
                throw new RuntimeException("Invalid mood entry state");
        }
    }

    // Queries trigger, belief, behavior tables and gets ids of rows containing the String parameters.
    // If those rows don't exist, a new row is inserted and its id is returned.
    public int[] getIds(String trigger, String belief, String behavior) {
        // ids of trigger, belief, behavior in tables
        int triggerID, beliefID, behaviorID;
        // Query trigger, belief, behavior tables to see if input strings exist in tables already
        dbTrigger.openRead();
        Trigger[] triggerResult = dbTrigger.getTriggerByString(trigger);
        dbTrigger.close();
        dbBelief.openRead();
        Belief[] beliefResult = dbBelief.getBeliefByString(belief);
        dbBelief.close();
        dbBehavior.openRead();
        Behavior[] behaviorResult = dbBehavior.getBehaviorByString(behavior);
        dbBehavior.close();
        // If strings exist, use their ids. If not, add and use new id
        if (triggerResult.length > 0) {
            triggerID = triggerResult[0].id;
            Log.d("trigger existing id", Integer.toString(triggerID));
        } else {
            Trigger insertion = new Trigger(trigger);
            dbTrigger.openWrite();
            triggerID = (int) dbTrigger.insert(insertion);
            Log.d("trigger new id", Integer.toString(triggerID));
            dbTrigger.close();
        }
        if (beliefResult.length > 0) {
            beliefID = beliefResult[0].id;
            Log.d("belief existing id", Integer.toString(beliefID));
        } else {
            Belief insertion = new Belief(belief);
            dbBelief.openWrite();
            beliefID = (int) dbBelief.insert(insertion);
            Log.d("belief new id", Integer.toString(beliefID));
            dbBelief.close();
        }
        if (behaviorResult.length > 0) {
            behaviorID = behaviorResult[0].id;
            Log.d("behavior existing id", Integer.toString(behaviorID));
        } else {
            Behavior insertion = new Behavior(behavior);
            dbBehavior.openWrite();
            behaviorID = (int) dbBehavior.insert(insertion);
            Log.d("behavior new id", Integer.toString(behaviorID));
            dbBehavior.close();
        }
        int[] ids = {beliefID, triggerID, behaviorID};
        return ids;
    }

}
