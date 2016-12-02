package org.abondar.experimental.uibasicsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;


public class MultiControlActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {

    Switch sw;

    private static final String TAG = "RG ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_control);
        CheckBox burgerCB = (CheckBox) this.findViewById(R.id.burgerCB);

        if (burgerCB.isChecked()) {
            burgerCB.toggle();
        }

        burgerCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.v("Checkbox", "Burger is selected");
            }
        });

        sw = (Switch) this.findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(this);

        RadioGroup rg = (RadioGroup) findViewById(R.id.rg1);

        int checkedRbId = rg.getCheckedRadioButtonId();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case -1:
                        Log.v(TAG, "Cleared");

                    case R.id.rb1:
                        Log.v(TAG, "Juice");

                    case R.id.rb2:
                        Log.v(TAG, "Beer");

                    case R.id.rb3:
                        Log.v(TAG, "Vodka");


                }
            }
        });

    }

    public void saloCLickListener(View view) {
        switch (view.getId()) {
            case R.id.saloCB:
                Log.v("Checkbox", "Salo is selected");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            sw.setText("Switch is on");
        } else {
            sw.setText("Switch is off");
        }
    }


}
