package org.abondar.experimental.uibasicsdemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GravityActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager manager;
    private Sensor accelerometer;
    private TextView text;
    private float[] gravity = new float[3];
    private float[] motion = new float[3];
    private double ratio;
    private double angle;
    private int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);

        manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        text = (TextView) this.findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        manager.unregisterListener(this, accelerometer);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        for (int i = 0; i < 3; i++) {
            gravity[i] = (float) (0.1 * sensorEvent.values[i] + 0.9 * gravity[i]);
            motion[i] = sensorEvent.values[i] - gravity[i];
        }
        ratio = gravity[1] / SensorManager.GRAVITY_EARTH;
        if (ratio > 1.0) {
            ratio = 1.0;
        }

        if (ratio < -1.0) {
            ratio = -1.0;
        }

        angle = Math.toDegrees(Math.acos(ratio));
        if(gravity[2]<0){
            angle = -angle;
        }

        if (counter++ % 10 == 0){
            String msg = String.format(
                    "Raw values\nX: %8.4f\nY: %8.4f\nZ: %8.4f\n" +
                            "Gravity\nX: %8.4f\nY: %8.4f\nZ: %8.4f\n" +
                            "Motion\nX: %8.4f\nY: %8.4f\nZ: %8.4f\nAngle: %8.1f",
                    sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],
                    gravity[0],gravity[1],gravity[2],
                    motion[0],motion[1],motion[2],angle);
            text.setText(msg);
            text.invalidate();
            counter=1;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
