package project.my222au.boostrunner.model;


import java.util.Date;

public class Workout {

    private long mStartTime;
    private long mStoptime;
    private double mCaloris;
    private float mDistance;




    public long getStoptime() {
        return mStoptime;
    }

    public void setStoptime(long stoptime) {
        mStoptime = stoptime;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public double getCaloris() {

        return mCaloris;
    }

    public void setCaloris(double caloris) {
        mCaloris = caloris;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }





}
