package galvanique.client;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;

import galvanique.db.dao.GsrDAO;
import galvanique.db.entities.GsrLog;

public class PreferencesActivity extends Activity {

    private Button buttonBackground, buttonGSR, buttonNotifications;
    private TextView txtViewGSR;
    private BandClient client = null;
    private boolean gsrStarted;
    private GsrDAO db;

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent event) {
            if (event != null && gsrStarted) {
                appendToUI(String.format("GSR conductivity = %d\n", event.getResistance()));
                db.openWrite();
                db.insert(new GsrLog(event.getTimestamp(), event.getResistance()));
                db.close();
            }
        }
    };

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private class GsrSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        client.getSensorManager().registerGsrEventListener(mGsrEventListener);
                    } else {
                        appendToUI("The GSR sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage = "";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occurred: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);
            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        /* Background */
        // TODO http://developer.android.com/training/basics/data-storage/shared-preferences.html
        buttonBackground = (Button) findViewById(R.id.buttonBackground);
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        /* TODO GSR -- we need to put the MS band code in a Service for it to work outside preferences */
        gsrStarted = false;
        db = new GsrDAO(getApplicationContext());
        txtViewGSR = (TextView) findViewById(R.id.txtViewGSR);
        buttonGSR = (Button) findViewById(R.id.buttonGSR);
        buttonGSR.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                if (!gsrStarted) {
                    gsrStarted = !gsrStarted;
                    buttonGSR.setText("Stop GSR Collection");
                    txtViewGSR.setText("");
                    new GsrSubscriptionTask().execute();
                } else {
                    gsrStarted = !gsrStarted;
                    buttonGSR.setText("Begin GSR Collection");
                    txtViewGSR.setText("");
                }
            }
        });

        /* Notifications */
        // TODO http://developer.android.com/training/basics/data-storage/shared-preferences.html
        buttonNotifications = (Button) findViewById(R.id.buttonNotifications);
        buttonNotifications.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtViewGSR.setText(string);
            }
        });
    }

}
