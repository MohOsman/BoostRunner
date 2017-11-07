package project.my222au.boostrunner.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import project.my222au.boostrunner.R;

public class StarterActivity extends AppCompatActivity {
    private TextView mLogoTextview;
    private Button mStartButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        mLogoTextview = (TextView) findViewById(R.id.logo_TextView);
        mStartButton = (Button) findViewById(R.id.register_Button);
        mLogoTextview.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Audiowide_Regular.ttf"));

        mStartButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Audiowide_Regular.ttf"));


        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerIntent);
            }
        });






    }
}