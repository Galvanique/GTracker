package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.entities.CopingStrategyLog;

public class GetHelpActivity extends Activity {

    /**
     * Various UI components
     */
    private Button getSTHelpButton;
    private Button getLTHelpButton;
    private TextView copingStratStr;

    /**
     * @see CopingStrategyLogDAO#getRandomCopingStrategy(int longTerm)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);

        // Give starting values
        getSTHelpButton = (Button) findViewById(R.id.GetSTHelpButton);
        getSTHelpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                CopingStrategyLogDAO dao = new CopingStrategyLogDAO(getApplicationContext());
                dao.openRead();
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
                CopingStrategyLogDAO dao = new CopingStrategyLogDAO(getApplicationContext());
                dao.openRead();
                CopingStrategyLog cs = dao.getRandomCopingStrategy(1); // 1 for long term
                Log.d("random cs is", cs.getCopingStrategy());
                dao.close();
                copingStratStr.setText(cs.getCopingStrategy());
            }
        });
        copingStratStr = (TextView) findViewById(R.id.CopingStratStr);
    }


    /* TODO
        Mark the time, send a push notification asking the user to rate the strategy's effectiveness
        once enough time has passed. Update the database with the rating.
    */

}
