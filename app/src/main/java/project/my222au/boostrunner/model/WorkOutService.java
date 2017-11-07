package project.my222au.boostrunner.model;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.UI.MainActivity;


public class WorkOutService extends android.app.Service implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final String TAG = WorkOutService.class.getSimpleName();
    private static final String PREFS_NAME_RUNNER = "runnerprefs";
    public static final String BORADCAST_ACTION = "project.my222au.boostrunner.custom.intent.action";
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private State mState = State.STOPPED;
    public static final int NOTIFICATION_ID = 12;
    private long mPauseoffset;
    private long minute;
    private long seconds;
    private long hours;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Workout mWorkout = new Workout();
    private IBinder binder = new ServiceBinder();
    private NotificationManager mNotificaonManager;
    private Location mLastKnownLocation;
    private Handler handler = new Handler();
    private LocationData data;

    Intent intent;
    private Notification mNotification;

    public Location getLastKnownLocation() {
        return mLastKnownLocation;
    }


    public void setLocation(Location location) {
        mLastKnownLocation = location;
    }


    public class ServiceBinder extends Binder {
        public WorkOutService getService() {
            return WorkOutService.this;
        }
    }

    public enum State {
        RUNNING,
        STOPPED,
        PAUSED

    }


    @Override
    public void onCreate() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


        }
        data = new LocationData();
//        checksttings();
        googleConnet();

        createLocationRequest();
        intent = new Intent(BORADCAST_ACTION);

        super.onCreate();
    }

    public void googleConnet() {
        Log.d(TAG, "googleConnet: ");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Log.d(TAG, "googleConnet: null");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendupdates);
        handler.postDelayed(sendupdates, 1000);


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        startService(intent);
        return binder;
    }


    @Override
    @SuppressWarnings("all")
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        startLocationUpdates();

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "googleApiClient has been suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "googleApiClient  connection falid");

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged: ");
        if (location != null) {
            {
                setLocation(location);
                Log.d(TAG, "onLocationChanged: " + location.getLatitude() + "-" + location.getLongitude());
                if (mState == State.RUNNING)
                    if (data != null) {

                        data.saveLocations(getApplicationContext(), location);
                        countDistance();

                    }


            }
        }
    }


    protected void startLocationUpdates() {
        if (mLocationRequest != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);


            Log.d(TAG, "Location update started ..............: ");
        }
    }


    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    public void onStartTime() {
        setNotification(getBaseContext());
        googleConnet();
        data.clearFile(getApplicationContext());
        mWorkout.setStartTime(System.currentTimeMillis());
        mWorkout.setStoptime(0);
        Thread thr = new Thread(null, work, "Slow Counting");
        thr.start();
        mState = State.RUNNING;

    }

    public void onPauseTime() {
        mWorkout.setStoptime(System.currentTimeMillis());
        mState = State.PAUSED;
    }


    public void onResumeTime() {
        mPauseoffset = getElapsedTime();
        mWorkout.setStartTime(System.currentTimeMillis());
        mWorkout.setStoptime(0);

        mState = State.RUNNING;
    }


    public void onStopTime() {
        stopLocationUpdates();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        data.clearFile(getApplicationContext());
        mWorkout.setStartTime(0);
        mWorkout.setStoptime(0);
        mPauseoffset = 0;
        handler.removeCallbacks(sendupdates);
        mWorkout.setDistance(0);
        mWorkout.setCaloris(0);

        mState = State.STOPPED;

    }


    public double getDistance() {
        return mWorkout.getDistance();
    }

    public void setDistance(float distance) {
        mWorkout.setDistance(distance);
    }

    public void setCaloris(double caloris) {
        mWorkout.setCaloris(caloris);
    }

    public double getCaloirs() {
        return mWorkout.getCaloris();
    }


    public State getState() {
        return mState;
    }

    public String getdistanceString() {
        DecimalFormat df = new DecimalFormat("0.00");
        String distanceString = df.format(getDistance());
        return distanceString;

    }

    public String getCaloirString() {
        DecimalFormat df = new DecimalFormat("0");
        String caloristring = df.format(getCaloirs());

        return caloristring;
    }

    public String getTime() {
        return formatedTime(getElapsedTime());

    }

    /**
     * formats the elapsed time to  string. uses modulo 60 to start from 0
     *
     * @param time
     * @return
     */
    public String formatedTime(long time) {
        StringBuilder builder = new StringBuilder();
        hours = (time / 3600000);
        minute = ((time / 60000) % 60);
        seconds = ((time / 1000) % 60);
        builder.append(formatdiget(hours)).append(":");
        builder.append(formatdiget(minute)).append(":");
        builder.append(formatdiget(seconds));
        return builder.toString();

    }


    private String formatdiget(long time) {
        if (time > 9) {
            return String.valueOf(time);
        } else return "0" + String.valueOf(time);
    }

    /***
     * counts the duration of   workout activity since it uses currentTimeMillis it increses by it self;
     *
     * @return elapsedtime of duration in milliseconds
     */
    public long getElapsedTime() {
        if (mState == WorkOutService.State.PAUSED) {
            return (mWorkout.getStoptime() - mWorkout.getStartTime()) + mPauseoffset;

        } else if (mState == State.STOPPED) {
            return (0 - mWorkout.getStartTime()) + mPauseoffset;
        }
        return (System.currentTimeMillis() - mWorkout.getStartTime()) + mPauseoffset;

    }



    /**
     * Counts the distance the user  moved by  getting locations form getLocationfile and  getting the distance betweeen pre location
     * currnt location. it the saves those disatance to Resultlist;
     */
    public void countDistance() {
        float distance = 0;
        float resultList[] = new float[1];
        if (mState == State.RUNNING) {
            if (!getLocationsFromFile().isEmpty()) {
                for (int i = 0; i < getLocationsFromFile().size() - 1; i++) {
                    Location.distanceBetween(getLocationsFromFile().get(i).getLatitude(), getLocationsFromFile().get(i).getLongitude(),
                            getLocationsFromFile().get(i + 1).getLatitude(), getLocationsFromFile().get(i + 1).getLongitude(), resultList);

                    distance = distance + resultList[0];
                }
            }

        } else if (mState == State.STOPPED) {
            distance = 0;
            Log.d(TAG, "countDistance: ");
        }

        setDistance(distance / 1000);


    }


    /**
     * Gets Locations form a json file and adds it to  an arraylist.
     *
     * @return list of locations
     */
    public List<Location> getLocationsFromFile() {
        List<Location> locations = new ArrayList<Location>();
        LocationData data = new LocationData();
        try {

            String jsondata = data.locationsFromFile(getApplicationContext());
            if (!jsondata.isEmpty()) {


                JSONObject obj = new JSONObject(jsondata);

                JSONArray result = obj.getJSONArray("locations");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject r = result.getJSONObject(i);
                    double latitude = r.getDouble("latitude");
                    double longitude = r.getDouble("longitude");
                    Location location = new Location("loc");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    locations.add(location);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }


    public void countCaloris() {
        double metValue = 7.0;    // the avrage rate of running met value  since i only focus on running:
        double caloris = 0;
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME_RUNNER, MODE_PRIVATE);
        double weight = getDouble(preferences, "weight", 0);
        double secondsCaloris = (double) ((getElapsedTime() / 1000)) / 3600; // count caloris per hour
        if (getState() == State.RUNNING) {
            caloris += weight * secondsCaloris * metValue;
        } else if (getState() == State.PAUSED) {
            caloris = weight * secondsCaloris * metValue;
        } else if (getState() == State.STOPPED) {
            caloris = 0;
        }

        Log.d(TAG, "countCaloris: "+ caloris);

        setCaloris(caloris);
    }


    public void setNotification(Context context) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotification= new Notification.Builder(this)
                .setContentText(getTime())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_directions_run_white_48dp)
                .setContentIntent(contentIntent).build();
        mNotificaonManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotificaonManager.notify(NOTIFICATION_ID, mNotification);
        startForeground(NOTIFICATION_ID,mNotification);


    }



    // http://stackoverflow.com/a/18098090/6924616M
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    Runnable work = new Runnable() {
        public void run() {
            while (getState() == State.RUNNING) {  // 300 is upper limit
                countCaloris();


            }

        }
    };


  private Runnable  sendupdates = new Runnable() {
      @Override
      public void run() {
          displayUpdates();
          handler.postDelayed(sendupdates,0);

      }
  };

    private void displayUpdates() {
        intent.putExtra("time", getTime());
        intent.putExtra("distance", getdistanceString());
        intent.putExtra("caloris", getCaloirString());
        sendBroadcast(intent);

    }
}