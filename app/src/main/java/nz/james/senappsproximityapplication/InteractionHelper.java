package nz.james.senappsproximityapplication;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

/**
 * Created by james on 24/04/2017.
 */

public class InteractionHelper {
    private Response.Listener<JSONObject> jsonObjectListener;
    private Response.ErrorListener errorListener;
    private Gson gson;
    private RequestQueue requestQueue;
    private Context context;
    private PlaceBundleCompleteListener placeBundleCompleteListener;

    public InteractionHelper(Context context, PlaceBundleCompleteListener placeBundleCompleteListener){
        this.context = context;
        gson = new Gson();
        this.placeBundleCompleteListener = placeBundleCompleteListener;
    }

    public void getPlaceBundle(String placeID, String apiKey) {
        Ion.with(context)
        .load("http://senapps.ddns.net/database_api.php?action=getPlaceBundle&id=" + placeID + "&key=" + apiKey)
        .as(new TypeToken<PlaceBundle>(){})
        .setCallback(new FutureCallback<PlaceBundle>() {
            @Override
            public void onCompleted(Exception e, PlaceBundle placeBundle) {
                placeBundleCompleteListener.placeBundleDownloadComplete(placeBundle);
            }
        });
    }
}
