package galvanique.client;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

public class GetHelpActivity extends Activity {

    /**
     * Various UI components
     */
    private Button getSTHelpButton;
    private Button getLTHelpButton;
    private TextView copingStratStr;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);

        // Give starting values
        getSTHelpButton= (Button) findViewById(R.id.GetSTHelpButton);
        getLTHelpButton= (Button) findViewById(R.id.GetLTHelpButton);
        copingStratStr= (TextView) findViewById(R.id.CopingStratStr);
    }

    public String getRandomCopingStrategy (boolean longTerm) {
        return "";
    }
    // TODO create a simple layout for get help per the UI diagram
    // 1. A button for regulate distress
        // Pressing this button (onClick) fires a method that pulls a random coping strategy from our
        // (currently nonexistent) coping strategy database and displays it in an alert box with an
        // "okay" button. You could also just make a text value in the layout and keep it blank until
        // the user asks for a coping strategy.
    // 2. A button for adapt behavior
        // Same thing as above, except this one only does long term and that one only does short term.
    // 3. Mark the time, send a push notification asking the user to rate the strategy's effectiveness
        // once enough time has passed. Update the database with the rating.

    // see CopingStrategyDAO.getRandomCopingStrategy() -- it won't work if you try to test it because
    // the database doesn't exist yet / it's initialized to be empty anyway, but it's the method you
    // would use

}
