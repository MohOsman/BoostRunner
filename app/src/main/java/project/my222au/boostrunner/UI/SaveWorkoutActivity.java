package project.my222au.boostrunner.UI;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.database.WorkoutData;
import project.my222au.boostrunner.model.database.WorkoutDataBase;

public class SaveWorkoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = SaveWorkoutActivity.class.getSimpleName();
    public static final String ACTION_FRAGMENT = "project.my222au.boostrunner.UI.action.fragment;";
    private TextView mTotalDistance;
    private TextView mTotalDuration;
    private TextView mTotalCaloris;
    private FloatingActionButton mFabButton;
    private WorkoutDataBase mWorkoutDataBase;
    private WorkoutData mWorkoutData;

    private GoogleMap mGoogleMap;
    List<LatLng> mCoords;

    private int command=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_workout_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_save);
        mapFragment.getMapAsync(this);
        final Intent intent = getIntent();
        final String distance = intent.getStringExtra("distance");
        final String totalTime = intent.getStringExtra("time");
        final String caloris = intent.getStringExtra("caloris");
        mCoords = intent.getParcelableArrayListExtra("lat");
        for (LatLng lats : mCoords) {
            Log.d(TAG, "onCreate: " + lats.latitude);

        }


        mWorkoutData = new WorkoutData(distance, caloris, totalTime);


        mTotalDistance = (TextView) findViewById(R.id.total_distance);
        mTotalDuration = (TextView) findViewById(R.id.total_duration);
        mTotalCaloris = (TextView) findViewById(R.id.total_caloris);
        mFabButton = (FloatingActionButton) findViewById(R.id.fab);


        mTotalDistance.setText(distance + " km ");
        mTotalDuration.setText(totalTime + " ");
        mTotalCaloris.setText(caloris + " Kcal");
        try {
            mWorkoutDataBase = new WorkoutDataBase(getApplicationContext());

            mWorkoutDataBase.open();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        mFabButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {



                        WorkoutData data = mWorkoutDataBase.createWorkout(mWorkoutData);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_txt) + " distance  I run " + distance);
                        sendIntent.setType("text/plain");
                        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                         mFabButton.setVisibility(View.INVISIBLE);
                }
                ;




        });



    }



    @Override
    public void onBackPressed() {

       setup_alert(R.string.dissmiss,R.string.dissmiss_message).show();


    }

    @Override
    protected void onResume() {
        try {
            mWorkoutDataBase.open();
        } catch (SQLException e) {
            e.printStackTrace();
            command =1;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWorkoutDataBase.Close();
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Predefined icon ID
                // app icon in action bar clicked ==>  go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // configurs the alert to the user
    private AlertDialog setup_alert(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setNegativeButton("NO",new OnNegativClick());
        builder.setPositiveButton("YES", new DialogDone());  // Add button action
        return builder.create();
    }


    private class DialogDone implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            Intent  intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }


    private class OnNegativClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
           dialog.dismiss();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (!mCoords.isEmpty()) {
            LatLng activityPosistion = mCoords.get(0);
            LatLng stopPostion = mCoords.get(mCoords.size() - 1);
            List<LatLng> coodrs = new ArrayList<LatLng>();


            CameraUpdate camupdate = CameraUpdateFactory.newLatLngZoom(activityPosistion, 14);
            mGoogleMap.animateCamera(camupdate);
            mGoogleMap.addMarker(new MarkerOptions().position(activityPosistion).title("StartPostion"));
            for (int i = 0; i < mCoords.size(); i++) {
                coodrs.add(new LatLng(mCoords.get(i).latitude, mCoords.get(i).longitude));


            }
            mGoogleMap.addPolyline(new PolylineOptions().addAll(coodrs).width(12).color(Color.BLUE));
            mGoogleMap.addMarker(new MarkerOptions().position(stopPostion).title("End Postion"));
        }
    }


}
