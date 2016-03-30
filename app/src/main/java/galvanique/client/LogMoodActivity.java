package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.channguyen.rsv.RangeSliderView;

import galvanique.db.dao.MoodLogDAO;
import galvanique.db.entities.MoodLog;

// TODO back button
public class LogMoodActivity extends Activity {

    private enum State {
        MOOD, MAGNITUDE, TRIGGER, BELIEF, BEHAVIOR
    }

    private State state;

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
    Button buttonNext;
    RangeSliderView slider;
    EditText editTextTrigger, editTextBelief, editTextBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_mood);

        state = State.MOOD;

        // Edit texts
        editTextTrigger = (EditText) findViewById(R.id.editTextTrigger);
        editTextBelief = (EditText) findViewById(R.id.editTextBelief);
        editTextBehavior = (EditText) findViewById(R.id.editTextBehavior);

        // Dropdown
        dropdown = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"", "moodOne", "moodTwo", "moodThree"}; // TODO same as moods in galvanique.db.entities.MoodLog.Mood
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                if (item instanceof String && !item.equals("")) {
                    mood = (String) item;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Slider https://github.com/channguyen/range-slider-view
        slider = (RangeSliderView) findViewById(R.id.rsv_small);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                magnitude = index;
            }
        };

        // Hide elements for other states
        slider.setVisibility(View.INVISIBLE);
        // TODO set slider index range to 10
        editTextTrigger.setVisibility(View.GONE);
        editTextBelief.setVisibility(View.GONE);
        editTextBehavior.setVisibility(View.GONE);

        // Next button
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                setUpLayout(state);
            }
        });
    }

    /**
     * Alters visibility of UI elements according to what part of the logging process the user is on
     *
     * @param s
     */
    public void setUpLayout(State s) {
        // TODO check for invalid inputs
        switch (state) {
            case MOOD: // currently
                // Set up MAGNITUDE UI elements
                dropdown.setVisibility(View.GONE);
                slider.setVisibility(View.VISIBLE);
                state = State.MAGNITUDE; // will be
                break;
            case MAGNITUDE:
                // Set up TRIGGER UI elements
                slider.setVisibility(View.GONE);
                editTextTrigger.setVisibility(View.VISIBLE);
                state = State.TRIGGER;
                break;
            case TRIGGER:
                // Grab trigger text
                trigger = editTextTrigger.getText().toString();
                // Set up BELIEF UI elements
                editTextTrigger.setVisibility(View.GONE);
                editTextBelief.setVisibility(View.VISIBLE);
                state = State.BELIEF;
                break;
            case BELIEF:
                // Grab belief text
                belief = editTextBelief.getText().toString();
                // Set up BEHAVIOR UI elements
                editTextBelief.setVisibility(View.GONE);
                editTextBehavior.setVisibility(View.VISIBLE);
                state = State.BEHAVIOR;
                buttonNext.setText("Submit");
                break;
            case BEHAVIOR:
                // Grab behavior text
                behavior = editTextBehavior.getText().toString();
                // Set up MOOD UI elements
                editTextBehavior.setVisibility(View.GONE);
                dropdown.setSelection(0);
                dropdown.setVisibility(View.VISIBLE);
                editTextTrigger.setText("");
                editTextBelief.setText("");
                editTextBehavior.setText("");
                // TODO Display confirmation of successful logging (toast notification)?
                MoodLog insertion = new MoodLog(System.currentTimeMillis(), MoodLog.Mood.valueOf(mood), belief, trigger, behavior, magnitude);
                MoodLogDAO db = new MoodLogDAO(getApplicationContext());
                db.openWrite();
                db.insert(insertion);
                db.close();
                state = State.MOOD;
                buttonNext.setText("Next");
                break;
            default:
                throw new RuntimeException("Invalid mood entry state");
        }
    }

}
