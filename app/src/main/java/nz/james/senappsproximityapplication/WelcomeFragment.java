package nz.james.senappsproximityapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment implements PlaceBundleCompleteListener {
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

        Gimbal.setApiKey(getActivity().getApplication(), "a283f434-d865-449d-9592-ee48e8f99125");

        if(!Gimbal.isStarted()){
            Gimbal.start();
            imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_online_32);
        } else {
            imageViewServiceStatus.setImageResource(R.drawable.gimbal_service_online_32);
        }

        monitorGimbalStatus();

        initView();
        monitorPlace();
        listenBeacon();
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

            @Override
            public void onVisitStart(Visit visit) {
                super.onVisitStart(visit);

                if(vibrator.hasVibrator()){
                    vibrator.vibrate(1000);
                }

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Getting Interaction Information...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                InteractionHelper interactionHelper = new InteractionHelper(getActivity(), placeBundleCompleteListener);
                interactionHelper.getPlaceBundle(visit.getPlace().getIdentifier(), "8afb2533daebebd01d0df52117e8aa71");


            }

            @Override
            public void onVisitEnd(Visit visit) {
                super.onVisitEnd(visit);

                if(vibrator.hasVibrator()){
                    vibrator.vibrate(1000);
                }

                Toast.makeText(getActivity(), "Visit ended", Toast.LENGTH_SHORT).show();

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

        final PlaceBundle placeBundleFinal = placeBundle;

        if(placeBundle != null){
            AlertDialog.Builder placeBundleAlertDialog = new AlertDialog.Builder(getActivity());
            placeBundleAlertDialog.setTitle("Place Bundle Retrieved");
            placeBundleAlertDialog.setMessage("Would you like to view the information for the entry interaction or exit interaction?");
            placeBundleAlertDialog.setNegativeButton("Entry Interaction", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(placeBundleFinal.getEntryInteraction() != null){
                        AlertDialog.Builder entryInteractionBundleAlertDialog = new AlertDialog.Builder(getActivity());
                        entryInteractionBundleAlertDialog.setTitle("Entry Interaction Information");
                        entryInteractionBundleAlertDialog.setMessage("Interaction ID: " + placeBundleFinal.getEntryInteraction().getInteraction().getID() + "\n" +
                                "Interaction Name: " + placeBundleFinal.getEntryInteraction().getInteraction().getInteractionName() + "\n" +
                                "Interaction Description: " + placeBundleFinal.getEntryInteraction().getInteraction().getInteractionDescription() + "\n" +
                                "ActionType: " + placeBundleFinal.getEntryInteraction().getInteraction().getActionType() + "\n" +
                                "NotificationMessage (if applicable): " + placeBundleFinal.getEntryInteraction().getInteraction().getNotificationMessage() + "\n" +
                                "Trigger ID: " + placeBundleFinal.getEntryInteraction().getTrigger().getID() + "\n" +
                                "Trigger Name: " + placeBundleFinal.getEntryInteraction().getTrigger().getName() + "\n" +
                                "Trigger Type: " + placeBundleFinal.getEntryInteraction().getTrigger().getType() + "\n" +
                                "Trigger Linger Time (0 = instantly): " + placeBundleFinal.getEntryInteraction().getTrigger().getTime() + "\n" +
                                "Content ID: " + placeBundleFinal.getEntryInteraction().getContent().getID() + "\n" +
                                "Content Name: " + placeBundleFinal.getEntryInteraction().getContent().getName() + "\n" +
                                "Content Filepath: " + placeBundleFinal.getEntryInteraction().getContent().getFilepath() + "\n" +
                                "Content Type: " + placeBundleFinal.getEntryInteraction().getContent().getType());
                        entryInteractionBundleAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        entryInteractionBundleAlertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "No Entry Interaction Set", Toast.LENGTH_LONG).show();
                    }

                }
            });
            placeBundleAlertDialog.setPositiveButton("Exit Interaction", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(placeBundleFinal.getExitInteraction() != null){
                        AlertDialog.Builder exitInteractionBundleAlertDialog = new AlertDialog.Builder(getActivity());
                        exitInteractionBundleAlertDialog.setTitle("Exit Interaction Information");
                        exitInteractionBundleAlertDialog.setMessage("Interaction ID: " + placeBundleFinal.getExitInteraction().getInteraction().getID() + "\n" +
                                "Interaction Name: " + placeBundleFinal.getExitInteraction().getInteraction().getInteractionName() + "\n" +
                                "Interaction Description: " + placeBundleFinal.getExitInteraction().getInteraction().getInteractionDescription() + "\n" +
                                "ActionType: " + placeBundleFinal.getExitInteraction().getInteraction().getActionType() + "\n" +
                                "NotificationMessage (if applicable): " + placeBundleFinal.getExitInteraction().getInteraction().getNotificationMessage() + "\n" +
                                "Trigger ID: " + placeBundleFinal.getExitInteraction().getTrigger().getID() + "\n" +
                                "Trigger Name: " + placeBundleFinal.getExitInteraction().getTrigger().getName() + "\n" +
                                "Trigger Type: " + placeBundleFinal.getExitInteraction().getTrigger().getType() + "\n" +
                                "Trigger Linger Time (0 = instantly): " + placeBundleFinal.getExitInteraction().getTrigger().getTime() + "\n" +
                                "Content ID: " + placeBundleFinal.getExitInteraction().getContent().getID() + "\n" +
                                "Content Name: " + placeBundleFinal.getExitInteraction().getContent().getName() + "\n" +
                                "Content Filepath: " + placeBundleFinal.getExitInteraction().getContent().getFilepath() + "\n" +
                                "Content Type: " + placeBundleFinal.getExitInteraction().getContent().getType());
                        exitInteractionBundleAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        exitInteractionBundleAlertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "No Exit Interaction Set", Toast.LENGTH_LONG).show();
                    }

                }
            });

            placeBundleAlertDialog.show();
        } else {
            Toast.makeText(getActivity(), "No Place Bundle found", Toast.LENGTH_LONG).show();
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
}
