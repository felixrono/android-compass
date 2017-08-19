package com.example.felix.androidcompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Float azimuthAngle;
    private SensorManager compassSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    private float current_degree = 0f;

    TextView tv_Degree;
    ImageView iv_Compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    public void onResume(){
        super.onResume();
        compassSensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_UI);

    }
    public void onPause(){
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    float[] accel_read;
    float[] magnet_read;
    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_Degree = (TextView) findViewById(R.id.tv_degrees);
        iv_Compass = (ImageView) findViewById(R.id.iv_compass);

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accel_read = event.values;
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magnet_read = event.values;
        }
         if(accel_read != null && magnet_read != null){
             float R[] =  new float[9];
             float I[] = new float[9];
             boolean successful_read = SensorManager.getRotationMatrix(R,I,accel_read,magnet_read);

             if(successful_read){
                 float orientation[] = new float[3];
                 SensorManager.getOrientation(R,orientation);
                 azimuthAngle = orientation[0];
                 float degrees = ((azimuthAngle * 180f)/3.14f);
                 int degreesInt = Math.round(degrees);
                 tv_Degree.setText(Integer.toString(degreesInt)+(char) 0x00B0+""+"to absolute North");
                 RotateAnimation rotate =new RotateAnimation(current_degree,-degreesInt, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                 rotate.setDuration(100);
                 rotate.setFillAfter(true);

                 iv_Compass.startAnimation(rotate);
                 current_degree = -degreesInt;
             }
         }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
