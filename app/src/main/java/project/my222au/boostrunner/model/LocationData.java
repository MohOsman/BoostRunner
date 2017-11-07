package project.my222au.boostrunner.model;

import android.content.Context;
import android.location.Location;
import android.nfc.Tag;
import android.util.JsonWriter;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import project.my222au.boostrunner.R;

import static android.content.Context.CONTEXT_INCLUDE_CODE;
import static android.content.Context.MODE_PRIVATE;

//  NOT USING
public class LocationData implements Serializable {
    private static final String TAG = LocationData.class.getSimpleName() ;
    List<Location> listofLocaions = new ArrayList<Location>();
    private double lat;
    private BufferedReader mBufferReader;
    private FileOutputStream mFileOutputStream;
    private InputStream mInputStream;


    public double getLat() {
        for (int i = 0; i < listofLocaions.size(); i++) {
            lat = listofLocaions.get(i).getLatitude();

        }

        return lat;
    }

    public void saveLocations(Context context,Location loction) {
       listofLocaions.add(loction);


        Log.i("TAG", " IN SAVING  LOCATION DATA ");

        JSONObject actvity = new JSONObject();

        try {

            JSONArray array = new JSONArray();
            for (Location locitons : listofLocaions) {

                JSONObject locJSON = new JSONObject();
                locJSON.put("latitude", locitons.getLatitude());
                locJSON.put("longitude", locitons.getLongitude());
                array.put(locJSON);

            }

               actvity.put("locations", array);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("location.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(actvity.toString());
            outputStreamWriter.close();
        }
        catch (IOException exc) { exc.printStackTrace(); }
    }

    

   public String locationsFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("location.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


     public  void clearFile(Context context){

         try {
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("location.json", Context.MODE_PRIVATE));
             outputStreamWriter.write(" ");
             outputStreamWriter.close();
         } catch (IOException e) {
             e.printStackTrace();
         }

     }


    }
