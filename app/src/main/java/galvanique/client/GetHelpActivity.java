package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;

public class GetHelpActivity extends Activity {

    /**
     * Various UI components
     */
    private Button getHelpButton;
    private TextView copingStratStr;

    /**
     * @see CopingStrategyDAO#getRandomCopingStrategy(int longTerm)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);

        // Give starting values
        getHelpButton = (Button) findViewById(R.id.GetHelpButton);
        getHelpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                CopingStrategyDAO dao = new CopingStrategyDAO(getApplicationContext());
                dao.openRead();
                CopingStrategy cs = dao.getRandomCopingStrategy();
                Log.d("random cs is", cs.name);
                dao.close();
                copingStratStr.setText(cs.name);
            }
        });
        copingStratStr = (TextView) findViewById(R.id.CopingStratStr);
    }


    /* TODO
        Mark the time, send a push notification asking the user to rate the strategy's effectiveness
        once enough time has passed. Update the database with the rating.
    */

}
