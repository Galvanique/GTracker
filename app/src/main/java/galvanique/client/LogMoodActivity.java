package galvanique.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import galvanique.db.dao.CopingStrategyLogDefaultDAO;
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

    private State state;
    private boolean readyToWrite = false;
    // TODO-someone when is the table full enough to use user data?
    private final int THRESHOLD = 10;

    /**
     * MoodLog attributes
     */
    private String mood; // select from list
    private int magnitude; // slider
    private String trigger; // text input
    private String belief; // text input
    private String behavior; // text input
    /**
     * UI
     */
    Spinner dropdown;
    Button buttonNext, buttonBack;
    RangeSliderView slider;
    EditText editTextTrigger, editTextBelief, editTextBehavior;
    TextView textViewInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_mood);

        state = State.MOOD;

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
        // same as moods in galvanique.db.entities.MoodLog.Mood
        String[] items = new String[]{"", "Happy", "Sad", "Anxious", "Angry", "Guilt", "Shame",
                "Depressed", "Bored", "Tired", "Lonely", "Proud", "Hopeful",
                "Frustrated", "Disgust", "Numb", "Physical Pain", "Intrusive Thoughts", "Stressed",
                "Irritable", "Motivated", "Excited", "Grateful", "Joy", "Loved"};
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
                    CopingStrategyLogDAO logDB = new CopingStrategyLogDAO(getApplicationContext());
                    logDB.openRead();
                    if (logDB.getCountCopingStrategyLogs() > THRESHOLD) {
                        // TODO-tyler get suggestion from CopingStrategyLog table
                    } else {
                        // TODO-tyler get suggestion from CopingStrategyLogDefault table
                        CopingStrategyLogDefaultDAO defaultDB = new CopingStrategyLogDefaultDAO(getApplicationContext());
                    }
                    logDB.close();
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
                // Grab trigger text
                trigger = editTextTrigger.getText().toString();
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                break;
            case BELIEF:
                textViewInstructions.setText("What did feeling this way make you think or believe? (Optional)");
                editTextTrigger.setVisibility(View.GONE);
                editTextBehavior.setVisibility(View.GONE);
                editTextBelief.setVisibility(View.VISIBLE);
                // Grab belief text
                belief = editTextBelief.getText().toString();
                buttonNext.setText("Next");
                buttonBack.setText("Back");
                break;
            case BEHAVIOR:
                textViewInstructions.setText("How did feeling this way make you behave? (Optional)");
                editTextBelief.setVisibility(View.GONE);
                editTextBehavior.setVisibility(View.VISIBLE);
                // Grab behavior text
                behavior = editTextBehavior.getText().toString();
                buttonNext.setText("Submit");
                buttonBack.setText("Back");
                break;
            case STRATEGY:
                textViewInstructions.setText("Would you like a coping strategy suggestion?");
                buttonNext.setText("Yes");
                buttonBack.setText("No");
                editTextBehavior.setVisibility(View.GONE);
                if (readyToWrite) {
                    int[] ids = getIds(trigger, belief, behavior);
                    MoodLog insertion = new MoodLog(System.currentTimeMillis(), MoodLog.Mood.valueOf(mood), ids[0], ids[1], ids[2], magnitude, "");
                    MoodLogDAO db = new MoodLogDAO(getApplicationContext());
                    db.openWrite();
                    // Insert MoodLog using these trigger, belief, behavior IDs
                    db.insert(insertion);
                    db.close();
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
    public int[] getIds (String trigger, String belief, String behavior) {
        // ids of trigger, belief, behavior in tables
        int triggerID;
        int beliefID;
        int behaviorID;
        // Query trigger, belief, behavior tables to see if input strings exist in tables already
        TriggerDAO dbTrigger = new TriggerDAO(getApplicationContext());
        dbTrigger.openRead();
        Trigger[] triggerResult = dbTrigger.getTriggerByString(trigger);
        dbTrigger.close();
        BeliefDAO dbBelief = new BeliefDAO(getApplicationContext());
        dbBelief.openRead();
        Belief[] beliefResult = dbBelief.getBeliefByString(belief);
        dbBelief.close();
        BehaviorDAO dbBehavior = new BehaviorDAO(getApplicationContext());
        dbBehavior.openRead();
        Behavior[] behaviorResult = dbBehavior.getBehaviorByString(behavior);
        dbBehavior.close();
        // If strings exist, use their ids. If not, add and use new id
        if (triggerResult.length > 0) {
            triggerID = triggerResult[0].id;
        } else {
            Trigger insertion = new Trigger(trigger);
            dbTrigger.openWrite();
            triggerID = (int) dbTrigger.insert(insertion);
            dbTrigger.close();
        }
        if (beliefResult.length > 0) {
            beliefID = beliefResult[0].id;
        } else {
            Belief insertion = new Belief(belief);
            dbBelief.openWrite();
            beliefID = (int) dbBelief.insert(insertion);
            dbBelief.close();
        }
        if (behaviorResult.length > 0) {
            behaviorID = behaviorResult[0].id;
        } else {
            Behavior insertion = new Behavior(behavior);
            dbBehavior.openWrite();
            behaviorID = (int) dbBehavior.insert(insertion);
            dbBehavior.close();
        }
        int[] ids = {beliefID, triggerID, behaviorID};
        return ids;
    }

}
