package galvanique.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import galvanique.db.dao.GsrDAO;
import galvanique.db.entities.GsrLog;

public class GsrActivity extends AppCompatActivity {

    private Button buttonGSR;
    private TextView txtViewGSR;
    private BandClient client = null;
    private boolean gsrStarted;
    private FileOutputStream fOutGsr;
    File directory;

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent event) {
            if (event != null && gsrStarted) {
                GsrDAO db = new GsrDAO(getApplicationContext());
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
                String exceptionMessage;
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
        // Set up base UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up GSR UI
        gsrStarted = false;
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

        Button buttonExport = (Button) findViewById(R.id.buttonExport);
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                exportGsr(fOutGsr);
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

    private void exportGsr(FileOutputStream out) {
        GsrDAO dbGsr = new GsrDAO(getApplicationContext());
        dbGsr.openRead();
        if (dbGsr.getCountGsrLogs() > 0) {
            // Set up file I/O
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            df.setTimeZone(TimeZone.getDefault());
            String date = df.format(Calendar.getInstance().getTime());
            File sdCard = Environment.getExternalStorageDirectory();
            directory = new File(sdCard.getAbsolutePath() + "/gTrackerData/" + date);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String gsrFileName = "gsrValues.csv";
            File gsrFile = new File(directory, gsrFileName);
            try {
                fOutGsr = new FileOutputStream(gsrFile, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            GsrLog[] gsrLogs = dbGsr.getAllGsr();
            for (GsrLog l : gsrLogs) {
                try {
                    out.write((l.getTimestamp() + "," + l.getConductivity() + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(
                    getApplicationContext(),
                    "GSR data exported to " + directory,
                    Toast.LENGTH_LONG
            ).show();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "No GSR logs to export.",
                    Toast.LENGTH_LONG
            ).show();
        }
        dbGsr.close();
    }

}
