package project.my222au.boostrunner.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.Runner;
import project.my222au.boostrunner.model.Validator;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final  String PREFS_NAME_RUNNER = "runnerprefs";
    private TextView mLogoTextview;
    private EditText mNameInput;
    private EditText mWeightInput;
    private EditText mHeightInput;
    private EditText mAgeInput;
    private Boolean isMale = false;
    private boolean isFemale = false;
    private Button mRegisterButton;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;

    String TestString;

    private Runner mRunner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLogoTextview = (TextView) findViewById(R.id.logoTextView);
        mLogoTextview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Audiowide_Regular.ttf"));

        /// properties for the registreation
        mNameInput = (EditText) findViewById(R.id.input_name);
        mWeightInput = (EditText) findViewById(R.id.input_weight);
        mHeightInput = (EditText) findViewById(R.id.input_height);
        mAgeInput =  (EditText) findViewById(R.id.input_age);


        mRegisterButton = (Button) findViewById(R.id.btn_signup);
        mRegisterButton.setOnClickListener(new OnButtonclick());

        mMaleRadio = (RadioButton) findViewById(R.id.radiobtn_male); // radiobutton for male choice


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }


    private String getNameInput() {
        String name = mNameInput.getText().toString();
        if (name != null) {
            return name;
        } else return "no name";
    }


    private String getHeightInput() {
        String height = mHeightInput.getText().toString();
        return height;

    }

    private String getWeightInput() {
        String height = mWeightInput.getText().toString();
        return height;
    }

    private int getAgeInput(){
        String age = mAgeInput.getText().toString();
        int  ageInt = Integer.parseInt(age);

        return ageInt;

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();


        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radiobtn_male:
                if (checked)
                    isMale = true;


                break;
            case R.id.radiobtn_female:
                if (checked)

                    isFemale = true;

                break;
        }


    }


    private double parseWeight() {
        double weightValue = 0;
        String weightString = mWeightInput.getText().toString();
        if (weightString.length() > 0) {

            weightValue = Double.parseDouble(weightString);
        }

        return weightValue;
    }

    private double parseHiget() {
        double lengthValue = 0;
        String lengthString = mHeightInput.getText().toString();
        if (lengthString.length() > 0) {

            lengthValue = Double.parseDouble(lengthString);
        }

        return lengthValue;
    }


    // configurs the alert to the user
    private AlertDialog setup_alert(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("ok", new DialogDone());  // Add button action
        return builder.create();
    }


    private class DialogDone implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();             // Close down dialog
        }
    }


    private class OnButtonclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Name: " + getNameInput());
            Log.i(TAG, "Weight: " + getWeightInput());
            Log.i(TAG, "height: " + getHeightInput());
            Log.i(TAG, "Gender: " + TestString);

            // Error handling
            if (!mNameInput.getText().toString().isEmpty() || !mHeightInput.getText().toString().isEmpty()
                    || !mWeightInput.getText().toString().isEmpty()) {

                if (Validator.checkWeight(parseWeight()) && Validator.checkHeight(parseHiget())) {
                    Log.i(TAG, Validator.checkHeight(parseHiget()) + "");
                    mRunner = new Runner(getNameInput(), parseWeight(), parseHiget(), getAgeInput());
                      // if both radiobuttons are unchecked
                    if (!isMale && !isFemale) {
                        setup_alert(R.string.error, R.string.male_female).show();
                        return;
                    } else if (isMale) {
                        mRunner.setGender(Runner.Gender.MALE);
                    } else if (isFemale) {
                        mRunner.setGender(Runner.Gender.FEMALE);


                    }

                    // Add the new user profile to the sharedprefrences
                    Log.i(TAG, "Name: " + mRunner.getName());
                    Log.i(TAG, "Weight: " + mRunner.getWeight());
                    Log.i(TAG, "height: " + mRunner.getHeight());
                    Log.i(TAG, "Gender: " + mRunner.getGender());

                    SharedPreferences  preferences = getSharedPreferences(PREFS_NAME_RUNNER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", mRunner.getName());
                    putDouble(editor,"weight",mRunner.getWeight());
                    putDouble(editor,"height",mRunner.getHeight());
                    editor.putString("gender",mRunner.getGender().toString());
                    editor.putInt("age",mRunner.getAge());
                    editor.commit();





                    Intent runningActivityIntent = new Intent(getApplicationContext(),MainActivity.class);
                     startActivity(runningActivityIntent);
                } else {
                    setup_alert(R.string.alert_title,R.string.alert_msg).show();

                }


            }



            if (mNameInput.getText().toString().isEmpty()) {
                mNameInput.setError("A name must be enterd");

            }
            if (mHeightInput.getText().toString().isEmpty()) {
                mHeightInput.setError("Height must be enterd ");
            }
            if (mWeightInput.getText().toString().isEmpty()) {
                mWeightInput.setError("Weight must be enterd ");
            }


        }

    }

 // http://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
   private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }


}