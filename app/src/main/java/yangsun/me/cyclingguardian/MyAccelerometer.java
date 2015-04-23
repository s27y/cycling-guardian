package yangsun.me.cyclingguardian;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;


/**
 * Created by yangsun on 21/04/15.
 */
public class MyAccelerometer implements SensorEventListener {

     SensorManager mSensorManager;
     Sensor mAccelerometer;
     Handler mHandler;
    private float maxValue;
    private float averageValue;
    private float totalValue;
    private float numberOfSample;


    public MyAccelerometer(SensorManager sm,Handler handler) {
        mSensorManager = sm;
        mHandler = handler;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        maxValue = 0F;

    }

    public Sensor getAccelerometer()
    {
        return mAccelerometer;
    }

    public float getAverageValue()
    {
        float returnVal = totalValue/numberOfSample;
        numberOfSample = 0;
        totalValue = 0;

        return returnVal;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        analysisData(event.values[0],event.values[1],event.values[2]);
    }


    public boolean analysisData(float x, float y, float z) {
        float currentValue = Float.parseFloat(Math.sqrt(x * x + y * y + z * z) + "");
        totalValue += currentValue;
        numberOfSample++;

        return true;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

