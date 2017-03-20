package nz.james.senappsproximityapplication;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Gimbal;
import com.gimbal.android.GimbalDebugger;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PlaceManager placeManager;
    private TextView textViewDetected;
    private TextView textViewRSSI;
    private BeaconSighting beaconSighting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gimbal.setApiKey(this.getApplication(), "49a988b9-2435-4829-8486-bd9d03e31938");

        if(!Gimbal.isStarted()){
            Gimbal.start();
        }

        GimbalDebugger.enableBeaconSightingsLogging();

        textViewDetected = (TextView) findViewById(R.id.textViewDetected);
        textViewRSSI = (TextView) findViewById(R.id.textViewRSSI);

        BeaconManager beaconManager = new BeaconManager();
        beaconManager.addListener(new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {
                super.onBeaconSighting(beaconSighting);
                String beaconID = beaconSighting.getBeacon().getIdentifier();
                Log.d("BeaconManager", "Sighted Beacon with ID: " + beaconID);
                textViewDetected.setText("Sighted Beacon with ID: " + beaconID);
                textViewRSSI.setText("RSSI: " + beaconSighting.getRSSI());
            }
        });

        placeManager = PlaceManager.getInstance();
        placeManager.addListener(new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                super.onVisitStart(visit);
                String placeID = visit.getPlace().getIdentifier();
                Log.d("PlaceManager", "Place entered: " + placeID);
                textViewDetected.setText("Place entered: " + placeID);
            }

            @Override
            public void onVisitStartWithDelay(Visit visit, int i) {
                super.onVisitStartWithDelay(visit, i);
            }

            @Override
            public void onVisitEnd(Visit visit) {
                super.onVisitEnd(visit);
                String placeID = visit.getPlace().getIdentifier();
                Log.d("PlaceManager", "Place left: " + placeID);
                textViewDetected.setText("Place left: " + placeID);
            }

            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
                super.onBeaconSighting(beaconSighting, list);
                String beaconID = beaconSighting.getBeacon().getIdentifier();
                Log.d("PlaceManager", "Sighted Beacon with ID within current visit: " + beaconID);
                textViewDetected.setText("Sighted Beacon with ID within current visit: " + beaconID);
                textViewRSSI.setText("RSSI: " + beaconSighting.getRSSI());
            }

            @Override
            public void locationDetected(Location location) {
                super.locationDetected(location);
            }
        });

        placeManager.startMonitoring();

    }
}
