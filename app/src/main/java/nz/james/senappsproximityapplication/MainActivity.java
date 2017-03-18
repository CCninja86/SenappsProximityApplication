package nz.james.senappsproximityapplication;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Gimbal;
import com.gimbal.android.GimbalDebugger;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        placeManager = PlaceManager.getInstance();
        placeManager.addListener(new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                super.onVisitStart(visit);
                Log.d("Place Manager", "Place entered: " + visit.getPlace().getName());
                Log.d("SenappsProximityApp", "Getting Place Information...");

                Gson gson = new Gson();
                String placeID = visit.getPlace().getIdentifier();
                String jsonResponse = null;
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71");

                try {
                    jsonResponse = Unirest.get("https://manager.gimbal.com/api/v2/places/" + placeID)
                            .headers(headers)
                            .asJson().getBody().toString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                if(jsonResponse != null){
                    Log.d("SenappsProximityApp", "Got place information. Retrieving Beacon information...");
                    GimbalPlace gimbalPlace = gson.fromJson(jsonResponse, GimbalPlace.class);
                    GimbalBeacon[] beacons = gimbalPlace.getBeacons();
                    GimbalBeacon beacon = beacons[0];
                    String beaconFactoryID = beacon.getFactoryId();

                    jsonResponse = null;

                    try {
                        jsonResponse = Unirest.get("https://manager.gimbal.com/api/beacons/" + beaconFactoryID)
                                .headers(headers)
                                .asJson().getBody().toString();
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }

                    if(jsonResponse != null){
                        GimbalBeacon gimbalBeacon = gson.fromJson(jsonResponse, GimbalBeacon.class);
                        HashMap<String, String> beaconAttributes = gimbalBeacon.getAttributes();
                        boolean beaconActive = Boolean.parseBoolean(beaconAttributes.get("active"));
                        boolean hasAssociatedInteraction = Boolean.parseBoolean(beaconAttributes.get("has_associated_interaction"));

                        if(beaconActive && hasAssociatedInteraction){
                            Interaction interaction = new Interaction();

                            int associatedInteractionID = Integer.parseInt(beaconAttributes.get("associated_interaction_ID"));
                            String associatedInteractionName = beaconAttributes.get("associated_interaction_name");
                            String associatedInteractionDescription = beaconAttributes.get("associated_interaction_description");
                            String associatedInteractionTrigger = beaconAttributes.get("associated_interaction_trigger");
                            String associatedInteractionActionType = beaconAttributes.get("associated_interaction_actionType");
                            String associatedInteractionContentName = beaconAttributes.get("associated_interaction_contentName");

                            interaction.setId(associatedInteractionID);
                            interaction.setName(associatedInteractionName);
                            interaction.setDescription(associatedInteractionDescription);
                            interaction.setTrigger(associatedInteractionTrigger);
                            interaction.setActionType(associatedInteractionActionType);
                            interaction.setContentName(associatedInteractionContentName);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("Associated Interaction Information:\n\n" +
                                    "ID: " + interaction.getId() + "\n" +
                                    "Name: " + interaction.getName() + "\n" +
                                    "Description: " + interaction.getDescription() + "\n" +
                                    "Trigger: " + interaction.getTrigger() + "\n" +
                                    "Action Type: " + interaction.getActionType() + "\n" +
                                    "Content Name: " + interaction.getContentName());
                        }
                    }
                }

//                if(jsonResponse != null){
//                    Log.d("SenappsProximitiyApp", "Place information successfully retrieved. Getting Beacon Information...");
//
//                    JSONObject jsonObject = jsonResponse.getBody().getObject();
//                    JSONArray beaconsArray = null;
//
//                    try {
//                        beaconsArray = jsonObject.getJSONArray("beacons");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    if(beaconsArray != null){
//                        JSONObject beacon = null;
//
//                        try {
//                            beacon = beaconsArray.getJSONObject(0);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if(beacon != null){
//                            String beaconFactoryID = "";
//
//                            try {
//                                beaconFactoryID = beacon.getString("id");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if(!beaconFactoryID.isEmpty()){
//                                HttpResponse<JsonNode> jsonResponseBeacon = null;
//
//                                try {
//                                    jsonResponseBeacon = Unirest.get("https://manager.gimbal.com/api/beacons/" + beaconFactoryID)
//                                            .headers(headers)
//                                            .asJson();
//                                } catch (UnirestException e) {
//                                    e.printStackTrace();
//                                }
//
//                                if(jsonResponseBeacon != null){
//                                    JSONObject object = jsonResponseBeacon.getBody().getObject();
//                                    JSONObject beaconAttributes = null;
//
//                                    try {
//                                        beaconAttributes = object.getJSONObject("beacon_attributes");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if(beaconAttributes != null){
//                                        boolean beaconActive = false;
//                                        boolean hasAssociatedInteraction = false;
//                                        int associatedInteractionID = 0;
//                                        String associatedInteractionName = "";
//                                        String associatedInteractionDescription = "";
//                                        String associatedInteractionTrigger = "";
//                                        String associatedInteractionActionType = "";
//                                        String associatedInteractionContentName = "";
//
//                                        try {
//                                            beaconActive = Boolean.parseBoolean(beaconAttributes.getString("active"));
//                                            hasAssociatedInteraction = Boolean.parseBoolean(beaconAttributes.getString("has_associated_interaction"));
//                                            associatedInteractionID = Integer.parseInt(beaconAttributes.getString("associated_interaction_ID"));
//                                            associatedInteractionName =
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onVisitStartWithDelay(Visit visit, int i) {
                super.onVisitStartWithDelay(visit, i);
            }

            @Override
            public void onVisitEnd(Visit visit) {
                super.onVisitEnd(visit);
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
