package yangsun.me.cyclingguardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Date;

public class DisplayData extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView phoneNumberTextView;
    TextView mUserNameTextView;
    TextView curAccelerateTextView;
    TextView currentAddressTextView;
    TextView mCurrentSpeedTextView;
    TextView mLatitudeTextView;
    TextView mLongitudeTextView;
    TextView mLastUpdateTimeTextView;
    TextView mTripTimTextView;
    ImageView mStartOrStopImageView;

    TextView mAverageSpeedTextView;
    TextView mTripDistanceTextView;
    Button mSendSmsBtn;
    Button mPlaySoundBtn;
    Button mFetchLocationAddressBtn;
    Button mTurnLocationOnOffBtn;
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;
    GoogleApiClient mGoogleApiClient;

    MyAccelerometer mAccelerometer;
    Handler mHandler = new Handler();
    String mCurrentAccelerate;
    boolean mStopHandler = false;

    MediaPlayer mMediaPlayer;
    Location mLastLocation;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    String mLastUpdateTime;
    boolean mIsTripStarted = false;
    boolean mAddressRequested = false;
    private AddressResultReceiver mResultReceiver;
    private ArrayList<String> mSpeedArray = new ArrayList<>();
    private ArrayList<String> mDistanceArray = new ArrayList<>();
    private double mTotalDistance = 0D;
    private double mAverageSpeed = 0;
    private Time mStartTime = new Time();
    private Time mFinishTime = new Time();
    private double mCurrentSpeed;
    private String mKinPhoneNumber;
    private String mUsername;
    CrashDetector mCrashDetector;
    boolean alertPlaying = false;
    int countAfterCrash = 0;
    private String mCurrentLocationAddress = "unknown";
    private double mNumberOfUpdate = 0;
    private int mTimeElapsed = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        String phoneNumberStr = intent.getStringExtra(Constants.PHONE_NUMBER_MESSAGE);
        String userNameStr = intent.getStringExtra(Constants.USER_NAME_MESSAGE);
        mKinPhoneNumber = phoneNumberStr;
        mUsername = userNameStr;

        curAccelerateTextView =
                (TextView) findViewById(R.id.cur_max_value_textView);

        currentAddressTextView = (TextView) findViewById(R.id.locationAddress_textView);
        mTurnLocationOnOffBtn = (Button) findViewById(R.id.turnLocationOnOff_button);
        phoneNumberTextView = (TextView) findViewById(R.id.your_kin_phone_number);
        mUserNameTextView = (TextView) findViewById(R.id.userName_textView);
        mCurrentSpeedTextView = (TextView) findViewById(R.id.currentSpeed_textView);

        mAverageSpeedTextView = (TextView) findViewById(R.id.averageSpeed_textView);
        mTripDistanceTextView = (TextView) findViewById(R.id.tripDistance_textView);
        mTripTimTextView = (TextView) findViewById(R.id.tripTime_textView);
        mLatitudeTextView = (TextView) (this.findViewById(R.id.latitude_textView));
        mLongitudeTextView = (TextView) (this.findViewById(R.id.longitude_textView));
        mLastUpdateTimeTextView = (TextView) (this.findViewById(R.id.lastUpdateTime_textView));

        mStartOrStopImageView = (ImageView)findViewById(R.id.startOrStop_imageView);


        mSendSmsBtn = (Button) findViewById(R.id.send_sms_button);
        mPlaySoundBtn = (Button) findViewById(R.id.playSound_button);
        mFetchLocationAddressBtn = (Button) findViewById(R.id.fetchLoactionAddress_button);


        if (!Constants.IS_DEBUG) {
            mSendSmsBtn.setVisibility(View.GONE);
            mPlaySoundBtn.setVisibility(View.GONE);
            mFetchLocationAddressBtn.setVisibility(View.GONE);
            curAccelerateTextView.setVisibility(View.GONE);
            mTurnLocationOnOffBtn.setVisibility(View.GONE);
        }

        phoneNumberTextView.setText(phoneNumberStr);
        mUserNameTextView.setText(userNameStr);
        Typeface font = Typeface.createFromAsset(getAssets(), "RADIOLAND.ttf");
        mCurrentSpeedTextView.setTypeface(font);
        mAverageSpeedTextView.setTypeface(font);
        mTripDistanceTextView.setTypeface(font);
        mTripTimTextView.setTypeface(font);
        mLatitudeTextView.setTypeface(font);
        mLongitudeTextView.setTypeface(font);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = new MyAccelerometer(mSensorManager, mHandler);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mCrashDetector = new CrashDetector(10.5, 13);
        final Activity a = this;


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (mIsTripStarted) {
                    Time currentTime = new Time();
                    currentTime.setToNow();
                    long millis = currentTime.toMillis(false) - mStartTime.toMillis(false);
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;

                    mTripTimTextView.setText(String.format("%d:%02d", minutes, seconds));
                }


                float aF = mAccelerometer.getAverageValue();
                mCurrentAccelerate = aF + "";
                if (aF > 13) {
                    Log.i("accelerate", "*************" + mCurrentAccelerate);
                    playSound(R.raw.soft_chime_beep);
                }

                curAccelerateTextView.setText(mCurrentAccelerate);
                if (mIsTripStarted) {
                    ;
                    if (mCrashDetector.addAccerationToList(aF) == true) {
                        //sendSMS(phoneNumberTextView.getText().toString(),"Impact");
                        if (alertPlaying == false) {
                            alertPlaying = true;
                            playSound(R.raw.alarm_fire_detector_smoke_alarm_domestic);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(a);
                            builder.setMessage("Are you alright?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                            stopSound();
                                            alertPlaying = false;
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.show();
                            //dismiss after 5sec if no one press i am fine btn
                            // a sms will be send out
                            new CountDownTimer(5000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onFinish() {
                                    // TODO Auto-generated method stub
                                    if (alert.isShowing()) {
                                        alert.dismiss();
                                        sendSMS(mKinPhoneNumber, "Your friend " + mUsername +
                                                " might have an accident at " + mCurrentLocationAddress +
                                                " (Lat " + mCurrentLocation.getLatitude() + " Long " + mCurrentLocation.getLongitude() + ")");
                                    }
                                }
                            }.start();
                        } else {
                            countAfterCrash++;
                            if (countAfterCrash > 5) {
                                //alert.dismiss();
                            }
                        }

                    }
                }

                Log.i("accelerate", mCurrentAccelerate);
                if (!mStopHandler) {
                    mHandler.postDelayed(this, 500);
                }
            }
        };
        // start it with:
        mHandler.post(runnable);


        buildGoogleApiClient();
        mGoogleApiClient.connect();

        updateValuesFromBundle(savedInstanceState);
    }

    public void playSound(View v) {

        mMediaPlayer = MediaPlayer.create(this, R.raw.soft_chime_beep);

        mMediaPlayer.start();
    }

    public void stopSound() {
        mMediaPlayer.stop();
    }

    public void playSound(int id) {
        mMediaPlayer = MediaPlayer.create(this, id);
        //R.raw.soft_chime_beep
        //R.raw.alarm_fire_detector_smoke_alarm_domestic
        mMediaPlayer.start();
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
        //mSensorManager.registerListener(mAccelerometer, mAccelerometer.getAccelerometer(), SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(mAccelerometer);
    }

    private void sendSMS(String phoneNumber, String msg) {
        Log.i("Send SMS", "");


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
    public void getLocation(View v) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        String msg = "Current Location: " +
                Double.toString(mLastLocation.getLatitude()) + "," +
                Double.toString(mLastLocation.getLongitude());
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private String formatFloatOrDouble(float f) {
        String s = String.format("%.2f", f);
        return s;
    }

    private String formatFloatOrDouble(double D) {
        String s = String.format("%.2f", D);
        return s;
    }

    private void updateUI() {
        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);

        mTripDistanceTextView.setText(formatFloatOrDouble(mTotalDistance));
        mAverageSpeedTextView.setText(formatFloatOrDouble(mAverageSpeed));
        mCurrentSpeedTextView.setText(formatFloatOrDouble(mCurrentSpeed));

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(Constants.REQUESTING_LOCATION_UPDATES_KEY,
                mIsTripStarted);
        savedInstanceState.putParcelable(Constants.LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(Constants.LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        savedInstanceState.putString(Constants.CURRENT_ACCELERATE_STRING_KEY, mCurrentAccelerate);
        savedInstanceState.putStringArrayList(Constants.SPEED_ARRAY_KEY, mSpeedArray);
        savedInstanceState.putStringArrayList(Constants.DISTANCE_ARRAY_KEY, mDistanceArray);
        savedInstanceState.putDouble(Constants.AVERAGE_SPEED_DOUBLE_KEY, mAverageSpeed);
        savedInstanceState.putDouble(Constants.TRIP_DISTANCE_DOUBLE_KEY, mTotalDistance);
        savedInstanceState.putDouble(Constants.CURRENT_SPEED_DOUBLE_KEY, mCurrentSpeed);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onLocationChanged(Location location) {
        mNumberOfUpdate++;
        mLastLocation = mCurrentLocation;
        mCurrentLocation = location;
        double curDistance;
        if (mLastLocation != null) {
            curDistance = getDistance(mLastLocation.getLatitude(), mLastLocation.getLatitude(),
                    mCurrentLocation.getLatitude(), mCurrentLocation.getLatitude());
        } else {
            curDistance = 0;
        }
        //only update current location address is the moved 50m
        if (curDistance > 0.05) {
            fetchAddress();
        }
        mTotalDistance += curDistance;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Time t = new Time();
        t.setToNow();
        double duration = (t.toMillis(false) - mStartTime.toMillis(false)) / 1000D / 60D / 60D;

        mCurrentSpeed = mCurrentLocation.getSpeed();
        mAverageSpeed = (mCurrentSpeed + mAverageSpeed / mNumberOfUpdate) / 2;

        updateUI();
        Log.i("distance", mTotalDistance + "");
        Log.i("time", "in hour " + duration);
        Log.i("speed", "average speed " + mAverageSpeed);

        Log.i("location", "Lat " + mCurrentLocation.getLatitude() + ", Long " + mCurrentLocation.getLongitude() + ", " + mCurrentLocation.getSpeed());


    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i("location", "Location Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressRequested) {
                startIntentService();
            }

            if (mIsTripStarted) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("location", "Location Connection Failed");
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mIsTripStarted from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(Constants.REQUESTING_LOCATION_UPDATES_KEY)) {
                mIsTripStarted = savedInstanceState.getBoolean(
                        Constants.REQUESTING_LOCATION_UPDATES_KEY);
                checkLocation();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(Constants.LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(Constants.LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(Constants.LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        Constants.LAST_UPDATED_TIME_STRING_KEY);
            }
            if (savedInstanceState.keySet().contains(Constants.CURRENT_ACCELERATE_STRING_KEY)) {
                mCurrentAccelerate = savedInstanceState.getString(
                        Constants.CURRENT_ACCELERATE_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(Constants.AVERAGE_SPEED_DOUBLE_KEY)) {
                mAverageSpeed = savedInstanceState.getDouble(
                        Constants.AVERAGE_SPEED_DOUBLE_KEY);
            }
            if (savedInstanceState.keySet().contains(Constants.TRIP_DISTANCE_DOUBLE_KEY)) {
                mTotalDistance = savedInstanceState.getDouble(
                        Constants.TRIP_DISTANCE_DOUBLE_KEY);
            }
            if (savedInstanceState.keySet().contains(Constants.CURRENT_SPEED_DOUBLE_KEY)) {
                mCurrentSpeed = savedInstanceState.getDouble(
                        Constants.CURRENT_SPEED_DOUBLE_KEY);
            }

            updateUI();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();


    }

    public void turnLocationOnOffHandler(View v) {
        if (mIsTripStarted == false) {

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }

            this.mIsTripStarted = true;
            mTurnLocationOnOffBtn.setText("Turn OFF location");
            createLocationRequest();
            startLocationUpdates();
            this.fetchAddress();
            mStartTime.setToNow();
            mTimeElapsed = 0;
            mAverageSpeed = 0;
            mTotalDistance = 0;
            if (mCurrentLocation != null) {
                updateUI();
            }
            mStartOrStopImageView.setImageResource(R.drawable.stop_icon);
            Log.d("time", Long.toString(mStartTime.toMillis(false) / 1000));
        } else {
            mStartOrStopImageView.setImageResource(R.drawable.play_icon);

            this.mIsTripStarted = false;
            mTurnLocationOnOffBtn.setText("Turn ON location");
            stopLocationUpdates();
            mFinishTime.setToNow();
            long finsihTimeInSec = mFinishTime.toMillis(false) / 1000;
            long startTimeInSec = mStartTime.toMillis(false) / 1000;
            Log.d("time", "activity " + Long.toString(finsihTimeInSec - startTimeInSec));
        }
    }

    private void checkLocation() {
        if (mIsTripStarted == false) {
            mTurnLocationOnOffBtn.setText("Turn OFF location");
        } else {
            this.mIsTripStarted = false;
            mTurnLocationOnOffBtn.setText("Turn ON location");
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public void fetchAddressButtonHandler(View view) {
        fetchAddress();
    }

    private void fetchAddress() {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            mResultReceiver = new AddressResultReceiver(mHandler);
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUiLocationAddress();
    }

    private void updateUiLocationAddress() {

    }


    private void showToast(String string) {
        Toast.makeText(getBaseContext(), string, Toast.LENGTH_LONG).show();
    }

    private void displayAddressOutput(String str) {
        currentAddressTextView.setText(str);
    }

    class AddressResultReceiver extends ResultReceiver {
        String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            mCurrentLocationAddress = mAddressOutput;
            displayAddressOutput(mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

        }
    }
}
