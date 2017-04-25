package nz.james.senappsproximityapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by james on 24/04/2017.
 */

public class InteractionHelper {

    private InteractionBundle interactionBundle;
    private Response.Listener<JSONObject> jsonObjectListener;
    private Response.ErrorListener errorListener;
    private Gson gson;
    private RequestQueue requestQueue;
    private Context context;

    public InteractionHelper(Context context){
        this.context = context;
        gson = new Gson();

        jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.getMessage());
            }
        };
    }

    public InteractionBundle getInteractionBundle() {
        return interactionBundle;
    }

    public void setInteractionBundle(InteractionBundle interactionBundle) {
        this.interactionBundle = interactionBundle;
    }

    public InteractionBundle getInteractionBundle(String placeID) throws ExecutionException, InterruptedException {
        interactionBundle = new InteractionBundle();

        GimbalPlace place = Ion.with(context).load("https://manager.gimbal.com/api/v2/places/" + placeID)
                .setHeader("Content-Type", "application/json")
                .setHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                .as(new TypeToken<GimbalPlace>(){}).get();

        GimbalBeacon associatedBeacon = place.getBeacons()[0];
        String beaconFactoryID = associatedBeacon.getFactoryId();

        GimbalBeacon beacon = Ion.with(context)
                .load("https://manager.gimbal.com/api/beacons/" + beaconFactoryID)
                .setHeader("Content-Type", "application/json")
                .setHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                .as(new TypeToken<GimbalBeacon>(){}).get();

        int associatedInteractionID = Integer.parseInt(beacon.getAttributes().get("associated_interaction_ID"));

        Interaction interaction = Ion.with(context)
                .load("http://senapps.ddns.net/database_api.php?action=getInteraction&id=" + associatedInteractionID)
                .as(new TypeToken<Interaction>(){}).get();

        interactionBundle.setInteraction(interaction);
        int triggerID = Integer.parseInt(interaction.getTriggerID());
        final int actionID = Integer.parseInt(interaction.getContentID());

        Trigger trigger = Ion.with(context)
                .load("http://senapps.ddns.net/database_api.php?action=getTrigger&id=" + triggerID)
                .as(new TypeToken<Trigger>(){}).get();

        interactionBundle.setTrigger(trigger);

        Content content = Ion.with(context)
                .load("http://senapps.ddns.net/database_api.php?action=getContent&id=" + actionID)
                .as(new TypeToken<Content>(){}).get();

        interactionBundle.setContent(content);



        return interactionBundle;
    }
}
