package nz.james.senappsproximityapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    public static final int REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gimbal.setApiKey(this.getApplication(), "49a988b9-2435-4829-8486-bd9d03e31938");

        if(!Gimbal.isStarted()){
            Gimbal.start();
        }

        GimbalDebugger.enableBeaconSightingsLogging();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("This app needs access to your location in order to function. Please grant access when prompted.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            }




        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case REQUEST_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startApp();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("This app needs access to your location in order to work. Please allow access when prompted.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }

                return;
            }
        }
    }

    private void startApp(){
        textViewDetected = (TextView) findViewById(R.id.textViewDetected);


        textViewRSSI = (TextView) findViewById(R.id.textViewRSSI);

        BeaconManager beaconManager = new BeaconManager();
        beaconManager.addListener(new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {
                super.onBeaconSighting(beaconSighting);
                String beaconID = beaconSighting.getBeacon().getIdentifier();
                Log.d("SenappsProximityApp", "Sighted Beacon with ID: " + beaconID);
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
                Log.d("SenappsProximityApp", "Place entered: " + placeID);
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
                Log.d("SenappsProximityApp", "Place left: " + placeID);
                textViewDetected.setText("Place left: " + placeID);
            }

            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
                super.onBeaconSighting(beaconSighting, list);
                String beaconID = beaconSighting.getBeacon().getIdentifier();
                Log.d("SenappsProximityApp", "Sighted Beacon with ID within current visit: " + beaconID);
                textViewDetected.setText("Sighted Beacon with ID within current visit: " + beaconID);
                textViewRSSI.setText("RSSI: " + beaconSighting.getRSSI());
            }
        });

        placeManager.startMonitoring();
        beaconManager.startListening();
    }
}
