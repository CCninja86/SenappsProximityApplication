package nz.james.senappsproximityapplication;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by james on 24/04/2017.
 */

public class InteractionHelper {
    private Response.Listener<JSONObject> jsonObjectListener;
    private Response.ErrorListener errorListener;
    private Gson gson;
    private RequestQueue requestQueue;
    private Context context;

    public InteractionHelper(Context context){
        this.context = context;
        gson = new Gson();
    }

    public PlaceBundle getPlaceBundle(String placeID, String apiKey) {
        PlaceBundle placeBundle = null;

        try {
            placeBundle = Ion.with(context)
                    .load("http://senapps.ddns.net/database_api.php?action=getPlaceBundle&id=" + placeID + "&key=" + apiKey)
                    .as(new TypeToken<PlaceBundle>(){}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return placeBundle;
    }
}
