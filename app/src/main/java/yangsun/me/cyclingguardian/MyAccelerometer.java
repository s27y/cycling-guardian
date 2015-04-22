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

    public float getMaxValue()
    {
        return maxValue;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        float alpha = 0.8F;
        float[] gravity = new float[3];
        float[] linear_acceleration = new float[3];

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        this.isCrash(linear_acceleration[0],linear_acceleration[1],linear_acceleration[2]);
    }


    public boolean isCrash(float x, float y, float z) {
        float currentValue = Float.parseFloat(Math.sqrt(x * x + y * y + z * z) + "");
        float threshold = 2;
        boolean flag = false;
        maxValue = currentValue;
        if (currentValue > threshold ) {
            maxValue = currentValue;
        }
        return flag;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

