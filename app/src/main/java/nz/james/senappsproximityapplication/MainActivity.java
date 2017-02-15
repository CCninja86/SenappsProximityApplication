package nz.james.senappsproximityapplication;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Gimbal.setApiKey(this.getApplication(), "867417f2-3c2e-4d3f-913f-a7de828d72f6");

            if(!Gimbal.isStarted()){
                Gimbal.start();
            }


            GimbalDebugger.enableBeaconSightingsLogging();

            textViewDetected = (TextView) findViewById(R.id.textViewDetected);
            textViewRSSI = (TextView) findViewById(R.id.textViewRSSI);

            placeManager = PlaceManager.getInstance();
            placeManager.addListener(new PlaceEventListener() {
                @Override
                public void onVisitStart(Visit visit) {
                    super.onVisitStart(visit);
                    Log.d("Place Manager", "Place entered: " + visit.getPlace().getName());
                    textViewDetected.setText("Place entered");
                }

                @Override
                public void onVisitStartWithDelay(Visit visit, int i) {
                    super.onVisitStartWithDelay(visit, i);
                }

                @Override
                public void onVisitEnd(Visit visit) {
                    super.onVisitEnd(visit);
                    Log.d("Place Manager", "Place exited: " + visit.getPlace().getName());
                    textViewDetected.setText("Place exited");
                    Toast.makeText(getApplicationContext(), "Place exited", Toast.LENGTH_LONG);
                }

                @Override
                public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
                }

                @Override
                public void locationDetected(Location location) {
                    super.locationDetected(location);
                }
            });

            placeManager.startMonitoring();


        }

    private void setBeaconInfo(BeaconSighting beaconSighting){
        this.beaconSighting = beaconSighting;
    }
}
