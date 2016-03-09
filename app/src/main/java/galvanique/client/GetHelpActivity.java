package galvanique.client;

import android.app.Activity;
import android.os.Bundle;

public class GetHelpActivity extends Activity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
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

}
