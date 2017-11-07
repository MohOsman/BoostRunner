package project.my222au.boostrunner.model.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkoutDataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BoostRunner.db";
    public static final String TABLE_NAME = "workoutTabel";
    public static  final String COL_ID = "_ID";
    public static  final String COL_DISTANCE = "DISTANCE";
    public  static final String COL_CALORIS = "CALORIS";
    public  static final String COL_DURATION = "DURATION";



    public WorkoutDataHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table "+ TABLE_NAME +" (" + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_DISTANCE +" TEXT, "+ COL_CALORIS +" TEXT, "+ COL_DURATION+ " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABEL IF EXISTS"+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
