package galvanique.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreferencesActivity extends Activity {

    private Button buttonBackground, buttonGSR, buttonNotifications;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        /* Background */
        buttonBackground = (Button) findViewById(R.id.buttonBackground);
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        /* GSR */
        buttonGSR = (Button) findViewById(R.id.buttonGSR);
        buttonGSR.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        /* Notifications */
        buttonNotifications = (Button) findViewById(R.id.buttonNotifications);
        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }
}
