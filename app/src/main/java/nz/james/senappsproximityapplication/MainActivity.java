package nz.james.senappsproximityapplication;

import android.Manifest;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
    public void onWelcomeFragmentInteraction(String type, String filepath) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        switch (type){
            case "Image":
                bundle.putString("Image", filepath);

                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.container, imageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case "Link":
                bundle.putString("URL", filepath);

                WebViewFragment webViewFragment = new WebViewFragment();
                webViewFragment.setArguments(bundle);

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, webViewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case "Text":



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
