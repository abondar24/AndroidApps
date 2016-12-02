package org.abondar.experimental.uibasicsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Formatter;

public class TimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        TextView dateCurrent = (TextView) findViewById(R.id.dateCurrent);
        TextView timeCurrent = (TextView) findViewById(R.id.timeCurrent);

        DatePicker dp = (DatePicker)findViewById(R.id.datePicker);
        dateCurrent.setText("Current date: "+ dp.getDayOfMonth()+"/"+dp.getMonth()+"/"+dp.getYear());

        TimePicker tp = (TimePicker)findViewById(R.id.timePicker);

        Formatter tf = new Formatter();
        tf.format("Current Time: %d:%02d",tp.getCurrentHour(),tp.getCurrentMinute());
       timeCurrent.setText(tf.toString());

       tp.setIs24HourView(true);


    }
}
