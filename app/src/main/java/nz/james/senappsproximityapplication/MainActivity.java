package nz.james.senappsproximityapplication;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener, InformationFragment.OnFragmentInteractionListener {

    private static final int PERMISSION_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            startApp();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
    }


    @Override
    public void onWelcomeFragmentInteraction(String action) {
        switch (action){
            case "information":
                InformationFragment informationFragment = new InformationFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, informationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

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
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, welcomeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
