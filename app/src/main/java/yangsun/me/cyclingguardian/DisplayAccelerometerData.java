package yangsun.me.cyclingguardian;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class DisplayAccelerometerData extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{


    private SensorManager mSensorManager;
    TextView phoneNumberTextView;
    MyAccelerometer MyAccelerometerClass;
    Handler mHandler = new Handler();
    boolean mStopHandler = false;
    TextView curAccelerateTextView;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_accelerometer_data);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity2Activity.PHONE_NUMBER_MESSAGE);


        curAccelerateTextView =
                (TextView) findViewById(R.id.cur_max_value_textView);

        phoneNumberTextView = (TextView) findViewById(R.id.your_kin_phone_number);
        phoneNumberTextView.setText(message);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        MyAccelerometerClass = new MyAccelerometer(mSensorManager, mHandler);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                curAccelerateTextView.setText(MyAccelerometerClass.getMaxValue() + "");
                if (!mStopHandler) {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        // start it with:
        mHandler.post(runnable);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_accelerometer_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(MyAccelerometerClass, MyAccelerometerClass.getAccelerometer(), SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(MyAccelerometerClass);
    }


    public void sendSMSMessage(View view) {
        Log.i("Send SMS", "");

        String phoneNo = phoneNumberTextView.getText().toString();
        String message = "TEST";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getLocation(View v)
        {
         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            String msg = "Current Location: " +
                  Double.toString(mLastLocation.getLatitude()) + "," +
                  Double.toString(mLastLocation.getLongitude());


        }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    TextView mLatitudeText = (TextView)findViewById(R.id.mLatitudeText);
                    TextView mLongitudeText = (TextView)findViewById(R.id.mLongitudeText);
                    mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                    mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
