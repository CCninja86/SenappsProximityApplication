package nz.james.senappsproximityapplication;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetectionFragment extends Fragment {
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
    private BeaconEventListener beaconEventListener;
    private BeaconManager beaconManager;
    private String TAG = "beacon";

    public ArrayAdapter<String> listAdapter;
    public ListView listView;

    private Map<String, String> headers;

    RequestQueue queue;
    private Response.Listener<GimbalPlace> gimbalPlaceListener;

    public DetectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetectionFragment newInstance(String param1, String param2) {
        DetectionFragment fragment = new DetectionFragment();
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
        View view = inflater.inflate(R.layout.fragment_detection, container, false);

        queue = Volley.newRequestQueue(getActivity());
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("AUTHORIZATION", "Token token=8afb2533daebebd01d0df52117e8aa71");

        Gimbal.setApiKey(getActivity().getApplication(), "cdbdec5b-91c6-464b-89b4-80f5ec559720");

        if(!Gimbal.isStarted()){
            Gimbal.start();
        }

        initView();
        monitorPlace();
        listenBeacon();
        CommunicationManager.getInstance().startReceivingCommunications();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    private void listenBeacon() {
        BeaconEventListener beaconEventListener = getBeaconEventListener();
        BeaconManager beaconManager = new BeaconManager();
        beaconManager.addListener(beaconEventListener);
        beaconManager.startListening();
    }

    private void monitorPlace() {
        placeEventListener = getPlaceEventListener();
        placeManager = PlaceManager.getInstance();
        placeManager.addListener(placeEventListener);
        placeManager.startMonitoring();
    }

    private void initView() {
        GimbalLogConfig.enableUncaughtExceptionLogging();
        Toast.makeText(getActivity(), "Gimbal API Key Successfully Set", Toast.LENGTH_SHORT).show();
        GimbalDebugger.enableBeaconSightingsLogging();
    }

    private BeaconEventListener getBeaconEventListener() {
        Log.i(TAG, "BeaconEventListener started sucessfully...");
        BeaconEventListener beaconSightingListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting beaconSighting) {
                super.onBeaconSighting(beaconSighting);

            }
        };



        return beaconSightingListener;
    }

    private PlaceEventListener getPlaceEventListener() {

        PlaceEventListener obj = new PlaceEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sight, List<Visit> visit) {
                super.onBeaconSighting(sight, visit);

            }

            // @Override
            public void onVisitStart(Visit visit) {
                super.onVisitStart(visit);

                GetPlaceTask getPlaceTask = new GetPlaceTask(visit.getPlace().getIdentifier());
                getPlaceTask.execute();


            }

            @Override
            public void onVisitEnd(Visit visit) {
                // TODO Auto-generated method stub
                super.onVisitEnd(visit);

                Toast.makeText(getActivity(), "Visit ended", Toast.LENGTH_LONG).show();

            }

        };


        return obj;
    }

    private class GetPlaceTask extends AsyncTask<Void, Void, Void> {

        private String placeID;
        private Response.ErrorListener errorListener;


        public GetPlaceTask(String placeID){
            this.placeID = placeID;

            gimbalPlaceListener = new Response.Listener<GimbalPlace>() {
                @Override
                public void onResponse(GimbalPlace response) {
                    GimbalBeacon beacon = response.getBeacons()[0];
                    String beaconFactoryID = beacon.getFactoryId();

                    Toast.makeText(getActivity(), "The Factory ID for the Beacon associated with this place is " + beaconFactoryID, Toast.LENGTH_LONG).show();

                    GetInteractionIDTask getInteractionIDTask = new GetInteractionIDTask(beaconFactoryID);
                    getInteractionIDTask.execute();
                }
            };

            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError", error.getMessage());
                }
            };
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://manager.gimbal.com/api/v2/places/" + placeID;

            GsonRequest<GimbalPlace> gsonRequest = new GsonRequest<>(url, GimbalPlace.class, headers, gimbalPlaceListener, errorListener);
            queue.add(gsonRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }

    private class GetInteractionIDTask extends AsyncTask<Void, Void, Void> {

        private String beaconFactoryID;
        private Response.ErrorListener errorListener;
        private Response.Listener<GimbalBeacon> gimbalBeaconListener;

        public GetInteractionIDTask(String beaconFactoryID){
            this.beaconFactoryID = beaconFactoryID;

            gimbalBeaconListener = new Response.Listener<GimbalBeacon>() {
                @Override
                public void onResponse(GimbalBeacon response) {
                    int associatedInteractionID = Integer.parseInt(response.getAttributes().get("associated_interaction_ID"));
                    Toast.makeText(getActivity(), "The ID of the Interaction associated with the Beacon is " + associatedInteractionID, Toast.LENGTH_LONG).show();
                }
            };

            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VolleyError", error.getMessage());
                }
            };

        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://manager.gimbal.com/api/beacons/" + beaconFactoryID;

            GsonRequest<GimbalBeacon> gsonRequest = new GsonRequest<>(url, GimbalBeacon.class, headers, gimbalBeaconListener, errorListener);
            queue.add(gsonRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }
}
