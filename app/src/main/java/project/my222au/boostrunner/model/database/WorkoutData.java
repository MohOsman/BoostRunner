package project.my222au.boostrunner.model.database;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class WorkoutData implements Serializable {
    private String mDistance;
    private String mCaloris;
    private String mDuration;
    private String Date;
    private long mID;


    public WorkoutData()  {
    }

    public WorkoutData(String distance, String caloris, String duration) {
        mDistance = distance;
        mCaloris = caloris;
        mDuration = duration;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getCaloris() {
        return mCaloris;
    }

    public void setCaloris(String caloris) {
        mCaloris = caloris;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public long getID() {
        return mID;
    }

    public void setID(long ID) {
        mID = ID;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(mDistance);
//        parcel.writeString(mCaloris);
//        parcel.writeString(mCaloris);
//
//
//    }
//
//    private WorkoutData(Parcel in ){
//        mDistance = in.readString();
//        mCaloris= in.readString();
//        mCaloris = in.readString();
//
//    }
//
//
//    public static final Creator<WorkoutData> CREATOR = new Creator<WorkoutData>() {
//        @Override
//        public WorkoutData createFromParcel(Parcel parcel) {
//            return new WorkoutData(parcel);
//        }
//
//        @Override
//        public WorkoutData[] newArray(int i) {
//            return new WorkoutData[i];
//        }
//
//    };


}


