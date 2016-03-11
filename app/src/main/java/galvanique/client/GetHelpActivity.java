package galvanique.client;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import galvanique.db.CopingStrategyDAO;
import galvanique.db.CopingStrategyLog;

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
        getSTHelpButton = (Button) findViewById(R.id.GetSTHelpButton);
        getSTHelpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                CopingStrategyDAO dao = new CopingStrategyDAO(getApplicationContext());
                dao.openRead();
                // TODO query db, display strategy
                CopingStrategyLog cs = dao.getRandomCopingStrategy(0); // 0 for short term -- sqlite doesn't have booleans
                dao.close();
                copingStratStr.setText(cs.getCopingStrategy());
            }
        });
        getLTHelpButton = (Button) findViewById(R.id.GetLTHelpButton);
        getLTHelpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                CopingStrategyDAO dao = new CopingStrategyDAO(getApplicationContext());
                dao.openRead();
                // TODO query db, display strategy
                CopingStrategyLog cs = dao.getRandomCopingStrategy(1); // 1 for long term
                Log.d("random cs is", cs.getCopingStrategy());
                dao.close();
                copingStratStr.setText(cs.getCopingStrategy());
            }
        });
        copingStratStr = (TextView) findViewById(R.id.CopingStratStr);
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
