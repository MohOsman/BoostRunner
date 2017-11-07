package project.my222au.boostrunner.model.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.my222au.boostrunner.model.Runner;
import project.my222au.boostrunner.model.Workout;

public class WorkoutDataBase implements Serializable{

    private static final String TAG =  WorkoutDataBase.class.getSimpleName();
    private SQLiteDatabase mSQLiteDatabase;
    private WorkoutDataHelper mDatabasedbHelper;
    private String[] allColumns = {
            mDatabasedbHelper.COL_ID,
            mDatabasedbHelper.COL_DISTANCE,
            mDatabasedbHelper.COL_CALORIS,
            mDatabasedbHelper.COL_DURATION,


    };

    public WorkoutDataBase(Context context) {
        mDatabasedbHelper = new WorkoutDataHelper(context);
    }


    public void open() throws SQLException {
        mSQLiteDatabase = mDatabasedbHelper.getWritableDatabase();

    }

    public void Close() {
        mDatabasedbHelper.close();

    }


    public WorkoutData createWorkout(WorkoutData workoutData){
        ContentValues cvalues = new ContentValues();
        cvalues.put(mDatabasedbHelper.COL_DISTANCE,workoutData.getDistance());
        cvalues.put(mDatabasedbHelper.COL_DURATION,workoutData.getDuration());
        cvalues.put(mDatabasedbHelper.COL_CALORIS,workoutData.getCaloris());
        long insertID= mSQLiteDatabase.insert(mDatabasedbHelper.TABLE_NAME,null,cvalues);

        Cursor curosr = mSQLiteDatabase.query(mDatabasedbHelper.TABLE_NAME,
                allColumns,mDatabasedbHelper.COL_ID+" = "+ insertID,
                null,null,null,null);
        curosr.moveToFirst();
        WorkoutData newWorkoutData = cursorToData(curosr);
        curosr.close();
        return newWorkoutData;

    }




 public  WorkoutData cursorToData(Cursor cursor) {
      WorkoutData workoutData = new WorkoutData();
        workoutData.setID(cursor.getLong(0));
        workoutData.setDistance(cursor.getString(cursor.getColumnIndex("DISTANCE")));
        workoutData.setCaloris(cursor.getString(cursor.getColumnIndex("CALORIS")));
        workoutData.setDuration(cursor.getString(cursor.getColumnIndex("DURATION")));
       // set the gender value
        return workoutData;
    }


    public void deleteWorkout(Runner runner) {
        long id = runner.getID();
        Log.v(TAG,"Runner profile  deleted with id: " + id);
      mSQLiteDatabase.delete(mDatabasedbHelper.TABLE_NAME, mDatabasedbHelper.COL_ID
                + " = " + id, null);
    }


    public List<WorkoutData>  getAllWorkoutdata(){
       List<WorkoutData>  workoutDataList = new ArrayList<WorkoutData>();
        Cursor cursor = mSQLiteDatabase.query(mDatabasedbHelper.TABLE_NAME,allColumns,null,null
        ,null,null,null,null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            WorkoutData workoutData = cursorToData(cursor);
            workoutDataList.add(workoutData);
            cursor.moveToNext();
        }
          cursor.close();
          return workoutDataList;


    }

}
