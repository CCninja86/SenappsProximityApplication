package nz.james.senappsproximityapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by james on 29/09/2017.
 */

public class PlaceVisit {

    private String androidID;
    private String placeName;
    private String timeEntered;
    private String timeExited;
    private long visitDurationSeconds;
    private Context context;


    public PlaceVisit(Context context, String androidID, String placeName, Date timeEntered, Date timeExited, long visitDurationSeconds){
        this.androidID = androidID;
        this.placeName = placeName;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddd HH:mm:ss");

        this.timeEntered = simpleDateFormat.format(timeEntered);
        this.timeExited = simpleDateFormat.format(timeExited);

        this.visitDurationSeconds = visitDurationSeconds;
        this.context = context;
    }

    private String urlEncode(String str){
        return str.replaceAll(" ", "%20");
    }

    public void uploadVisitData(){
        Ion.with(context)
                .load("http://senapps.ddns.net/database_api.php?action=uploadVisitData&androidID=" + androidID + "&placeName=" + placeName +
                "&timeEntered=" + urlEncode(timeEntered) + "&timeExited=" + urlEncode(timeExited) + "&visitDuration=" + visitDurationSeconds + "&key=8afb2533daebebd01d0df52117e8aa71")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(result.equals("missing values")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Visit Upload Failed");
                            alert.setMessage("Failed to upload anonymous visit data due to missing values. Please ensure all values are correct.");
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            alert.show();
                        } else if(result.equals("Update Failed")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Visit Upload Failed");
                            alert.setMessage("Failed to upload anonymous visit data despite all required values being present.");
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            alert.show();
                        } else if(result.equals("Update Successful")){
                            Toast.makeText(context, "Visit Data Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTimeEntered() {
        return timeEntered;
    }

    public void setTimeEntered(String timeEntered) {
        this.timeEntered = timeEntered;
    }

    public String getTimeExited() {
        return timeExited;
    }

    public void setTimeExited(String timeExited) {
        this.timeExited = timeExited;
    }

    public long getVisitDuration() {
        return visitDurationSeconds;
    }

    public void setVisitDuration(long visitDuration) {
        this.visitDurationSeconds = visitDuration;
    }
}
