package project.my222au.boostrunner.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.RegisterSectionInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.LocationData;
import project.my222au.boostrunner.model.WorkOutService;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * A placeholder fragment containing a simple view.
 * Uses Google api client to get the location services.
 */
@RuntimePermissions
public class RunningMapFragment extends Fragment {


    private static final String TAG = RunningMapFragment.class.getSimpleName();
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int REQUEST_LOCATION = 200;
    private static final String PREFS_NAME_WORKOUT = "workout";
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private GoogleMap mGooglemap;
    private TextView mDuration;
    private TextView mDistance;
    private TextView mCaloris;
    private Button mStartButton;
    private Button mStopButton;
    private Button mPauseButton;
    private WorkOutService mWorkOutService;

    private boolean isBound;
    private boolean isRegistered;
    private Location mStartLocation;
    private Location mEndLoction;
    List<LatLng> coords = new ArrayList<LatLng>();
    Polyline polyline;


    public static RunningMapFragment getInstance() {
        RunningMapFragment fragment = new RunningMapFragment();
        return fragment;

    }


    private BroadcastReceiver boradcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            UpdateUI(intent);

        }
    };


    private ServiceConnection connection = new ServiceConnection() {
        //@Override  // Called when connection is made
        public void onServiceConnected(ComponentName cName, IBinder binder) {
            mWorkOutService = ((WorkOutService.ServiceBinder) binder).getService();

        }

        //@Override   //
        public void onServiceDisconnected(ComponentName cName) {
            mWorkOutService = null;
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_running, null, false);
        bindtoService();
        registerBoradcast();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadGoogleMap(googleMap);
                }
            });
        }
        mDuration = (TextView) view.findViewById(R.id.stoptime_label);
        mCaloris = (TextView) view.findViewById(R.id.caloris_label);
        mDistance = (TextView) view.findViewById(R.id.distance_label);
        mStartButton = (Button) view.findViewById(R.id.start_button);
        mStopButton = (Button) view.findViewById(R.id.stop_button);
        mPauseButton = (Button) view.findViewById(R.id.pause_button);
        mStartButton.setOnClickListener(new OnStartClickListner());
        mPauseButton.setOnClickListener(new OnPauseClickListner());
        mStopButton.setOnClickListener(new onStopClickListner());
        mPauseButton.setVisibility(View.INVISIBLE);
        mStopButton.setVisibility(view.INVISIBLE);
        return view;

    }


    private void loadGoogleMap(GoogleMap googleMap) {
        Log.d(TAG, "loadGoogleMap: ");
        mGooglemap = googleMap;

        if (googleMap != null) {
            RunningMapFragmentPermissionsDispatcher.getCurrentLocationWithCheck(this);
        } else {
            Log.d(TAG, "loadGoogleMap: Error GoogleMap null");
        }


    }


    @SuppressWarnings("all")  //  must have this  otherwise error in set myLcoationEnabled
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: ");
        if (mGooglemap != null) {
            mGooglemap.setMyLocationEnabled(true);
            mGooglemap.setBuildingsEnabled(true);


        }
    }

    private void updateMap() {
        if (mWorkOutService != null) {

            Location location = mWorkOutService.getLastKnownLocation();
            if (location != null) {
                CameraUpdate center =
                        CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude()
                                , location.getLongitude()), 15);


                mGooglemap.moveCamera(center);
                mGooglemap.animateCamera(center);
            }
        }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        unBindSevice();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: called");
        if (!isRegistered) {
            Log.d(TAG, "onStart:  boradcast is regiserd");
            registerBoradcast();
        }
        super.onStart();
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        updateMap();
        NotificationManager manager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(WorkOutService.NOTIFICATION_ID);

        super.onResume();
    }


    public void bindtoService() {
        Intent serviceIntent = new Intent(getActivity(), WorkOutService.class);
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "Binding to SlowCountService");
        isBound = true;

    }

    public void unBindSevice() {
        if (isBound) {
            getActivity().unbindService(connection);
            isBound = false;
        }
    }


    private void startActivity() {
        if (mWorkOutService != null) {
            mWorkOutService.onStartTime();

        }
    }

    private void pauseActvity() {
        if (mWorkOutService != null) {

            mWorkOutService.onPauseTime();


        }

    }

    private void resumeActvity() {
        if (mWorkOutService != null) {
            mWorkOutService.onResumeTime();

        }
    }

    private void stopActivity() {
        if (mWorkOutService != null) {
            Intent intet = new Intent(getActivity(), SaveWorkoutActivity.class);
            intet.putExtra("distance", mWorkOutService.getdistanceString());
            intet.putExtra("time", mWorkOutService.getTime());
            intet.putExtra("caloris", mWorkOutService.getCaloirString());
            intet.putParcelableArrayListExtra("lat", (ArrayList<? extends Parcelable>) coords);
            mWorkOutService.onStopTime();
            unBindSevice();

                getActivity().unregisterReceiver(boradcastReciver);
            mWorkOutService.stopSelf();
            startActivity(intet);


        }
    }




    private  void registerBoradcast(){
        getActivity().registerReceiver(boradcastReciver,new IntentFilter(WorkOutService.BORADCAST_ACTION));
        isRegistered= true;
    }


    private void UpdateUI(Intent intent) {
        updateMap();
        setOnvisibility();
        drawRoute();
        mDuration.setText(intent.getStringExtra("time"));
        mDistance.setText(intent.getStringExtra("distance"));
        mCaloris.setText(intent.getStringExtra("caloris"));


    }


    private void drawRoute() {
        List<LatLng> drawCoords = new ArrayList<LatLng>();
        double latitude;
        double longitude;
        if (mWorkOutService != null) {

            if (mWorkOutService.getState() == WorkOutService.State.RUNNING) {
                for (int i = 0; i < mWorkOutService.getLocationsFromFile().size(); i++) {
                    latitude = mWorkOutService.getLocationsFromFile().get(i).getLatitude();
                    longitude = mWorkOutService.getLocationsFromFile().get(i).getLongitude();
                    drawCoords.add(new LatLng(latitude, longitude));
                    polyline = mGooglemap.addPolyline(new PolylineOptions().addAll(drawCoords).width(10).color(Color.GREEN));
                    coords = drawCoords;
                }
            }

//         else if (mWorkOutService.getState() == WorkOutService.State.PAUSED) {
//            Log.d(TAG, "drawRoute:  Activity paused ");
////        } else if (mWorkOutService.getState() == WorkOutService.State.STOPPED) {
////                polyline.remove();
////                drawCoords.clear();
////            }


        }
    }


    private void setOnvisibility() {
        if (mWorkOutService != null) {
            if (mWorkOutService.getState() == WorkOutService.State.RUNNING) {
                mStopButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
            } else if (mWorkOutService.getState() == WorkOutService.State.STOPPED) {
                mStopButton.setVisibility(View.INVISIBLE);
                mPauseButton.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.VISIBLE);
            }
        }
    }


    private class OnPauseClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mWorkOutService.getState() == WorkOutService.State.RUNNING) {
                pauseActvity();

            } else if (mWorkOutService.getState() == WorkOutService.State.PAUSED) {
                resumeActvity();

            }
            changeButton();
        }
    }


    private void changeButton() {
        if (mWorkOutService != null) {

            if (mWorkOutService.getState() == WorkOutService.State.PAUSED) {
                mPauseButton.setBackground(getResources().getDrawable(R.color.colorPrimary, null));
                mPauseButton.setTextColor(Color.WHITE);
                mPauseButton.setText("Resume");

            } else if (mWorkOutService.getState() == WorkOutService.State.RUNNING) {
                mPauseButton.setBackground(getResources().getDrawable(R.color.white, null));
                mPauseButton.setTextColor(Color.WHITE);
                mPauseButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPauseButton.setText("Pause");

            } else if (mWorkOutService.getState() == WorkOutService.State.STOPPED) {

            }

        }
    }

    private class onStopClickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (mWorkOutService.getState() == WorkOutService.State.RUNNING
                    || mWorkOutService.getState() == WorkOutService.State.PAUSED) {
                stopActivity();


            }
            setOnvisibility();

        }
    }


    private class OnStartClickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivity();
            setOnvisibility();
            if (!isBound) {
                bindtoService();
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RunningMapFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }






}


