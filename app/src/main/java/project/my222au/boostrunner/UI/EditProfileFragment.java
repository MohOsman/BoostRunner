package project.my222au.boostrunner.UI;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import project.my222au.boostrunner.R;
import project.my222au.boostrunner.model.Runner;
import project.my222au.boostrunner.model.Validator;

import static android.content.Context.MODE_PRIVATE;

public class EditProfileFragment extends Fragment {

    private static final String PREFS_NAME_RUNNER = "runnerprefs";
    private static final String TAG = EditProfileFragment.class.getSimpleName();
    private EditText mNameInput;
    private EditText mWeightInput;
    private EditText mHeightInput;
    private EditText mAgeInput;
    private RadioGroup mRadiogroup;
    private Button mSaveButton;
    private String mName;
    private String mWeight;
    private String mHeight;
    private String mAge;
    private boolean isMale;
    private boolean isFemale;

    private Runner mRunner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_layout, null, false);
        mNameInput = (EditText) view.findViewById(R.id.input_name_edit);
        mWeightInput = (EditText) view.findViewById(R.id.input_weight_edit);
        mHeightInput = (EditText) view.findViewById(R.id.input_height_edit);
        mAgeInput = (EditText) view.findViewById(R.id.input_age_edit);
        mRadiogroup = (RadioGroup) view.findViewById(R.id.radibtn_group_edit);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
        getDatafromSharedPref();
        mSaveButton.setOnClickListener(new OnSaveButtonClicked());
        mRadiogroup.setOnCheckedChangeListener(new onCheckListner());


        return view;
    }


    private void getDatafromSharedPref() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME_RUNNER, MODE_PRIVATE);
        String name = preferences.getString("name", "");
        double weight = getDouble(preferences, "weight", 0);
        double height = getDouble(preferences, "height", 0);
        int age = preferences.getInt("age", 0);
        String gender = preferences.getString("gender", "");
        Log.d(TAG, "getDatafromSharedPref: "+ gender);


        if (name != null) {
            Log.d(TAG, "getDatafromSharedPref:" + name);
            mNameInput.setText(name);
        } else {
            Log.d(TAG, "getDatafromSharedPref: null");
        }

        mWeightInput.setText(weight + "");
        mHeightInput.setText(height + "");
        mAgeInput.setText(age + "");


        if (checkGender(gender)) { // if true MALE
            mRadiogroup.check(R.id.radiobtn_male_edit);
        } else if (!checkGender(gender)) { // else Female
            mRadiogroup.check(R.id.radiobtn_female_edit);
        }
    }

    private boolean checkGender(String gender) {
        if (gender.equalsIgnoreCase("male")) {
          return true;
        }
        return false;
    }

    // http://stackoverflow.com/a/18098090/6924616M
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }


    private String getNameInput() {
        mName = mNameInput.getText().toString();
        if (mName != null) {
            return mName;
        } else return "no name";

    }


    private String getHeightInput() {
        mHeight = mHeightInput.getText().toString();
        return mHeight;

    }

    private String getWeightInput() {
        mWeight = mWeightInput.getText().toString();
        return mWeight;
    }

    private int getAgeInput() {
        mAge = mAgeInput.getText().toString();
        int ageInt = Integer.parseInt(mAge);

        return ageInt;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private class onCheckListner implements RadioGroup.OnCheckedChangeListener {


        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case  R.id.radiobtn_male_edit:
                    isMale =true;

                    Log.d(TAG, "onCheckedChanged: male");
                    break;
                case R.id.radiobtn_female_edit:
                    Log.d(TAG, "onCheckedChanged: female");
                    isFemale=true;

            }
        }
    }

    private class OnSaveButtonClicked implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            Log.i(TAG, "Name: " + getNameInput());
            Log.i(TAG, "Weight: " + getWeightInput());
            Log.i(TAG, "height: " + getHeightInput());

            // Error handling
            if (!mNameInput.getText().toString().isEmpty() || !mHeightInput.getText().toString().isEmpty()
                    || !mWeightInput.getText().toString().isEmpty()) {

                if (Validator.checkWeight(parseWeight()) && Validator.checkHeight(parseHiget())) {
                    mRunner = new Runner(getNameInput(), parseWeight(), parseHiget(), getAgeInput());
                    // if both radiobuttons are unchecked
                    if (!isMale && !isFemale) {
                        setup_alert(R.string.error, R.string.male_female).show();
                        return;
                    } else if (isMale) {
                        Log.d(TAG, "onClick: isMale");
                        mRunner.setGender(Runner.Gender.MALE);
                        isMale = false;
                    } if (isFemale) {
                        Log.d(TAG, "onClick: isFemale");
                        mRunner.setGender(Runner.Gender.FEMALE);


                    }

                    // Add the new user profile to the sharedprefrences
                    Log.i(TAG, "Name: " + mRunner.getName());
                    Log.i(TAG, "Weight: " + mRunner.getWeight());
                    Log.i(TAG, "height: " + mRunner.getHeight());
                    Log.i(TAG, "Gender: " + mRunner.getGender().toString());

                    SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME_RUNNER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", mRunner.getName());
                    putDouble(editor, "weight", mRunner.getWeight());
                    putDouble(editor, "height", mRunner.getHeight());
                    editor.putString("gender", mRunner.getGender().toString());
                    editor.putInt("age", mRunner.getAge());
                    editor.commit();

                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);

                } else {
                    setup_alert(R.string.alert_title, R.string.alert_msg).show();


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


        // http://stackoverflow.com/questions/16319237/cant-put-double-sharedpreferences
        private SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
            return edit.putLong(key, Double.doubleToRawLongBits(value));
        }

    }



}

