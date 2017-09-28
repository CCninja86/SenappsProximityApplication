package nz.james.senappsproximityapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.GimbalDebugger;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;
import com.gimbal.logging.GimbalLogConfig;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends android.support.v4.app.Fragment implements PlaceBundleCompleteListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private PlaceManager placeManager;
    private PlaceEventListener placeEventListener;
    private PlaceBundleCompleteListener placeBundleCompleteListener;
    private String TAG = "beacon";

    private Map<String, String> headers;

    private ImageView imageViewServiceStatus;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;
    private Bundle userDataBundle;
    private InteractionHelper interactionHelper;
    private String interactionType;
    private Stopwatch stopwatch;

    private Globals g;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Visit Data Values

//        String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//        String interactionName = "ImageInteraction";
//        Date timeEntered = new Date();
//        Date timeExited = new Date();
//
//        long diffInMs = timeExited.getTime() - timeEntered.getTime();
//        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
//
//        InteractionVisit interactionVisit = new InteractionVisit(getActivity(), androidID, interactionName, timeEntered, timeExited, diffInSec);
//        interactionVisit.uploadVisitData();

        // Visit Data Values

        g = Globals.getInstance();

        userDataBundle = getArguments();
        String splashImageURL = userDataBundle.getString("ImageURL");
        ImageView imageViewBackground = (ImageView) view.findViewById(R.id.imageViewBackground);

        if(g.getSplashImageBitmap() == null){
            new SplashImageLoadTask(splashImageURL, imageViewBackground).execute();
        } else {
            imageViewBackground.setImageBitmap(g.getSplashImageBitmap());
        }


        placeBundleCompleteListener = this;
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        ImageView imageViewInformation = (ImageView) view.findViewById(R.id.imageViewInformation);
        imageViewInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWelcomeFragmentInteraction("information");
            }
        });

        imageViewServiceStatus = (ImageView) view.findViewById(R.id.imageViewServiceStatus);
        imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_offline_32);

        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71");

        Gimbal.setApiKey(getActivity().getApplication(), "8286e575-da0e-46ad-9b7a-ab8f77a70ef9");

        if(!Gimbal.isStarted()){
            Gimbal.start();
            imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_online_32);
        } else {
            imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_online_32);
        }

        BeaconManager beaconManager = new BeaconManager();

        BeaconEventListener beaconEventListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {
                super.onBeaconSighting(beaconSighting);
                Toast.makeText(getActivity(), "Sighted", Toast.LENGTH_SHORT).show();
            }
        };

        beaconManager.addListener(beaconEventListener);
        beaconManager.startListening();

        monitorGimbalStatus();

        initView();
        monitorPlace();
        CommunicationManager.getInstance().startReceivingCommunications();

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String action) {
        if (mListener != null) {
            mListener.onWelcomeFragmentInteraction(action);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onWelcomeFragmentInteraction(String action);
        void onWelcomeFragmentInteraction(String type, String filepath);
    }

    private void monitorPlace() {
        placeEventListener = getPlaceEventListener();
        placeManager = PlaceManager.getInstance();
        placeManager.addListener(placeEventListener);
        placeManager.startMonitoring();

        Toast.makeText(getActivity(), "Is Monitoring: " + placeManager.getInstance().isMonitoring(), Toast.LENGTH_LONG).show();
    }

    private void initView() {
        GimbalLogConfig.enableUncaughtExceptionLogging();
        GimbalDebugger.enableBeaconSightingsLogging();
        GimbalDebugger.enablePlaceLogging();
        GimbalDebugger.enableStatausLogging();
    }

    private PlaceEventListener getPlaceEventListener() {

        PlaceEventListener obj = new PlaceEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sight, List<Visit> visit) {
                super.onBeaconSighting(sight, visit);

            }

            @Override
            public void onVisitStart(Visit visit) {
                super.onVisitStart(visit);

                String entryTimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());


                final Visit visitFinal = visit;

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Getting Beacon data...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Ion.with(getActivity())
                        .load("https://manager.gimbal.com/api/v2/places/" + visit.getPlace().getIdentifier())
                        .addHeader("Content-Type", "application/json")
                        .addHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                        .as(new TypeToken<GimbalPlace>(){})
                        .setCallback(new FutureCallback<GimbalPlace>() {
                            @Override
                            public void onCompleted(Exception e, GimbalPlace place) {
                                final String beaconFactoryID = place.getBeacons()[0].getFactoryId();

                                Ion.with(getActivity())
                                        .load("https://manager.gimbal.com/api/beacons/" + beaconFactoryID)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                                        .as(new TypeToken<GimbalBeacon>(){})
                                        .setCallback(new FutureCallback<GimbalBeacon>() {
                                            @Override
                                            public void onCompleted(Exception e, GimbalBeacon beacon) {
                                                if(progressDialog != null && progressDialog.isShowing()){
                                                    progressDialog.dismiss();
                                                    progressDialog = null;
                                                }

                                                boolean active = Boolean.parseBoolean(beacon.getAttributes().get("active"));

                                                if(active && progressDialog == null){
                                                    interactionType = "entry";

                                                    progressDialog = new ProgressDialog(getActivity());
                                                    progressDialog.setMessage("Getting Interaction Information...");
                                                    progressDialog.setIndeterminate(true);
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();

                                                    interactionHelper = new InteractionHelper(getActivity(), placeBundleCompleteListener);
                                                    interactionHelper.getPlaceBundle(visitFinal.getPlace().getIdentifier(), "8afb2533daebebd01d0df52117e8aa71");
                                                } else {
                                                    Toast.makeText(getActivity(), "This Beacon is not active. Enable the beacon in the admin panel first.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        });

            }

            @Override
            public void onVisitEnd(Visit visit) {
                super.onVisitEnd(visit);

                final Visit visitFinal = visit;

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Getting Beacon data...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Ion.with(getActivity())
                        .load("https://manager.gimbal.com/api/v2/places/" + visit.getPlace().getIdentifier())
                        .addHeader("Content-Type", "application/json")
                        .addHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                        .as(new TypeToken<GimbalPlace>(){})
                        .setCallback(new FutureCallback<GimbalPlace>() {
                            @Override
                            public void onCompleted(Exception e, GimbalPlace place) {
                                final String beaconFactoryID = place.getBeacons()[0].getFactoryId();

                                Ion.with(getActivity())
                                        .load("https://manager.gimbal.com/api/beacons/" + beaconFactoryID)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71")
                                        .as(new TypeToken<GimbalBeacon>(){})
                                        .setCallback(new FutureCallback<GimbalBeacon>() {
                                            @Override
                                            public void onCompleted(Exception e, GimbalBeacon beacon) {
                                                if(progressDialog != null && progressDialog.isShowing()){
                                                    progressDialog.dismiss();
                                                    progressDialog = null;
                                                }

                                                boolean active = Boolean.parseBoolean(beacon.getAttributes().get("active"));

                                                if(active && progressDialog == null){
                                                    interactionType = "exit";

                                                    progressDialog = new ProgressDialog(getActivity());
                                                    progressDialog.setMessage("Getting Interaction Information...");
                                                    progressDialog.setIndeterminate(true);
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();

                                                    interactionHelper = new InteractionHelper(getActivity(), placeBundleCompleteListener);
                                                    interactionHelper.getPlaceBundle(visitFinal.getPlace().getIdentifier(), "8afb2533daebebd01d0df52117e8aa71");
                                                } else {
                                                    Toast.makeText(getActivity(), "This Beacon is not active. Enable the beacon in the admin panel first.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        });

            }

        };


        return obj;
    }

    @Override
    public void placeBundleDownloadComplete(PlaceBundle placeBundle) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        InteractionBundle interactionBundle = null;

        if(interactionType.equals("entry")){
            interactionBundle = placeBundle.getEntryInteraction();
        } else if(interactionType.equals("exit")){
            interactionBundle = placeBundle.getExitInteraction();
        } else {
            Toast.makeText(getActivity(), "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        String triggerType = interactionBundle.getTrigger().getType();

        if(triggerType.equals("onEnter") || triggerType.equals("onLeave")){
            if(vibrator.hasVibrator()){
                vibrator.vibrate(1000);
            }

            interactionHelper.processInteraction(mListener, interactionBundle);
        } else if(triggerType.equals("onLinger")){
            int lingerTime = Integer.parseInt(interactionBundle.getTrigger().getTime());
            new DelayedInteractionTask(lingerTime, interactionBundle).execute();
        }
    }


    private void monitorGimbalStatus(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private long time = 0;

            @Override
            public void run() {
                if(Gimbal.isStarted()){
                    imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_online_32);
                } else {
                    imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_offline_32);
                    Toast.makeText(getActivity(), "Gimbal Service went offline", Toast.LENGTH_LONG).show();
                }


                time += 5000;
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private class SplashImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public SplashImageLoadTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Splash Image...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            g.setSplashImageBitmap(result);
        }
    }

    private class DelayedInteractionTask extends AsyncTask<Void, Void, Void> {

        private InteractionBundle interactionBundle;
        private Stopwatch stopwatch;
        private int delay;

        public DelayedInteractionTask(int delay, InteractionBundle interactionBundle){
            this.delay = delay;
            this.interactionBundle = interactionBundle;
        }

        @Override
        protected void onPreExecute(){
            stopwatch = new Stopwatch();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(stopwatch.getElapsedTime().getElapsedRealtimeMillis() < delay * 1000){
                // timer loop
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(stopwatch != null){
                stopwatch = null;
            }

            if(vibrator.hasVibrator()){
                vibrator.vibrate(1000);
            }

            interactionHelper.processInteraction(mListener, interactionBundle);
        }
    }

}
