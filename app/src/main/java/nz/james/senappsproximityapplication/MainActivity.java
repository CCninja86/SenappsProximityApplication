package nz.james.senappsproximityapplication;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener, InformationFragment.OnFragmentInteractionListener,
        UserDataCompleteListener, WebViewFragment.OnFragmentInteractionListener, ImageFragment.OnFragmentInteractionListener, TextFileFragment.OnFragmentInteractionListener {

    private static final int PERMISSION_FINE_LOCATION = 1;
    private Bundle userDataBundle;

    private UserDataCompleteListener userDataCompleteListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDataCompleteListener = this;

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            startApp();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }

    public void setUserData(String imageURL, String customHint){
        userDataBundle.putString("ImageURL", imageURL);
        userDataBundle.putString("CustomHint", customHint);
    }


    @Override
    public void onWelcomeFragmentInteraction(String action) {
        switch (action){
            case "information":
                InformationFragment informationFragment = new InformationFragment();
                informationFragment.setArguments(userDataBundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, informationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
        }
    }

    @Override
    public void onWelcomeFragmentInteraction(String type, String content) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Content", content);



        switch (type){
            case "Image":
                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.container, imageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case "Link":
                WebViewFragment webViewFragment = new WebViewFragment();
                webViewFragment.setArguments(bundle);

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, webViewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case "Text":
                TextFileFragment textFileFragment = new TextFileFragment();
                textFileFragment.setArguments(bundle);

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, textFileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case "Notification":
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.information_icon)
                        .setContentTitle("Senapps Proximity Application")
                        .setContentText(content)
                        .setSound(soundUri);

                Intent resultIntent = new Intent(this, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());




                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startApp();
                } else {

                }
        }
    }

    private void startApp(){
        userDataBundle = new Bundle();

        final UserData userData;


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Custom Settings...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        Ion.with(this)
                .load("http://senapps.ddns.net/database_api.php?action=getUserData&key=8afb2533daebebd01d0df52117e8aa71")
                .as(new TypeToken<UserData>(){})
                .setCallback(new FutureCallback<UserData>() {
                    @Override
                    public void onCompleted(Exception e, UserData result) {
                        userDataCompleteListener.userDataDownloadComplete(result);
                    }
                });




    }

    @Override
    public void userDataDownloadComplete(UserData userData) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        if(userData != null){
            setUserData(userData.getSplashImage(), userData.getCustomHint());

            WelcomeFragment welcomeFragment = new WelcomeFragment();
            welcomeFragment.setArguments(userDataBundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, welcomeFragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(this, "Failed to get custom settings", Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
