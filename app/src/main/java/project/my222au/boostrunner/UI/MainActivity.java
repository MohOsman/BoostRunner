package project.my222au.boostrunner.UI;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.LocationData;
import project.my222au.boostrunner.model.WorkOutService;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int PERM_REQUEST_CODE_DRAW_OVERLAYS = 212;
    private DrawerLayout mDrawerLayout;
    private Fragment mFragment = null;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        displayView(0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        http://stackoverflow.com/questions/35453759/android-screen-overlay-detected-message-if-user-is-trying-to-grant-a-permissio
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,PERM_REQUEST_CODE_DRAW_OVERLAYS);
                return;
            }
        }



        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator =
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);



        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override

                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.activity_nav:
                                displayView(0);
                                break;
                            case R.id.edit_profile_nav:
                                displayView(1);
                                break;
                            case R.id.workouts_nav:
                                displayView(2);
                                break;
                        }

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


    }


    private void displayView(int display) {

        String title = getString(R.string.app_name);
        switch (display) {
            case 0:
                mFragment = new RunningMapFragment();

                title = getString(R.string.title_activity_running);

                break;

            case 1:
                mFragment = new EditProfileFragment();
                getSupportActionBar().setHomeButtonEnabled(true);
                title = getString(R.string.edit_profile_label);

                break;
            case 2:
                mFragment = new WorkoutActivitesFragment();
                title = getString(R.string.title_Workouts);
                break;

            default:
                break;
        }

        if (mFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.holder, mFragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);

        }
    }


    //http://stackoverflow.com/questions/35453759/android-screen-overlay-detected-message-if-user-is-trying-to-grant-a-permissio

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                }
                else {
                    Log.d("ScreenOverLay", "Screen overlay detected");
                    finish();
                    Intent mStartActivity = new Intent(this, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}



