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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class DisplayAccelerometerData extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener
{


    public final static String REQUESTING_LOCATION_UPDATES_KEY = "me.yangsun.cyclingguardian.PHONE_NUMBER_MESSAGE";
    public final static String LOCATION_KEY = "me.yangsun.cyclingguardian.LOCATION_KEY";
    public final static String LAST_UPDATED_TIME_STRING_KEY = "me.yangsun.cyclingguardian.LAST_UPDATED_TIME_STRING_KEY";

    public final static String CURRENT_ACCELERATE_STRING_KEY = "me.yangsun.cyclingguardian.CURRENT_ACCELERATE_STRING_KEY";

    private SensorManager mSensorManager;

    TextView phoneNumberTextView;

    MyAccelerometer MyAccelerometerClass;
    Handler mHandler = new Handler();
    String mCurrentAccelerate;
    boolean mStopHandler = false;
    TextView curAccelerateTextView;
    Button mTurnLocationOnOffBtn;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    String mLastUpdateTime;
    boolean mRequestingLocationUpdates = false;

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

        mTurnLocationOnOffBtn = (Button)findViewById(R.id.turnLocationOnOff_button);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        MyAccelerometerClass = new MyAccelerometer(mSensorManager, mHandler);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCurrentAccelerate = MyAccelerometerClass.getMaxValue() + "";
                curAccelerateTextView.setText(MyAccelerometerClass.getMaxValue() + "");
                Log.i("accelerate", mCurrentAccelerate);
                if (!mStopHandler) {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        // start it with:
        mHandler.post(runnable);

        buildGoogleApiClient();
        mGoogleApiClient.connect();


        updateValuesFromBundle(savedInstanceState);
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

    //old
    public void getLocation(View v)
        {
         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            String msg = "Current Location: " +
                  Double.toString(mLastLocation.getLatitude()) + "," +
                  Double.toString(mLastLocation.getLongitude());
        }



    // called by startLocationUpdate(View v)
    protected void startLocationUpdates() {


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    private void updateUI() {
        TextView mLatitudeTextView = (TextView)(this.findViewById(R.id.latitude_textView));
        TextView mLongitudeTextView = (TextView)(this.findViewById(R.id.longitude_textView));
        TextView mLastUpdateTimeTextView = (TextView)(this.findViewById(R.id.lastUpdateTime_textView));

        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        //savedInstanceState.putString(CURRENT_ACCELERATE_STRING_KEY, mCurrentAccelerate);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("location", "Location Connected");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("location", "Location Connection Failed");
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                checkLocationButton();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            if (savedInstanceState.keySet().contains(CURRENT_ACCELERATE_STRING_KEY)) {
                mCurrentAccelerate = savedInstanceState.getString(
                        CURRENT_ACCELERATE_STRING_KEY);
            }
            updateUI();
        }
    }
    private void checkLocationButton()
    {
        if (mRequestingLocationUpdates == false)
        {
            this.mRequestingLocationUpdates = true;
            mTurnLocationOnOffBtn.setText("Turn OFF location");
            createLocationRequest();
            startLocationUpdates();
        }
        else
        {
            this.mRequestingLocationUpdates = false;
            mTurnLocationOnOffBtn.setText("Turn ON location");
            stopLocationUpdates();
        }

    }
    public void setButtonsEnabledState(View v) {
        checkLocationButton();
    }
}
