package yangsun.me.cyclingguardian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class LandingActivity extends ActionBarActivity {
    public final static String PHONE_NUMBER_MESSAGE = "me.yangsun.cyclingguardian.PHONE_NUMBER_MESSAGE";
    EditText mPhoneNumberEditText;
    EditText mUserNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String phoneNumber = sharedPref.getString(Constants.PREFERENCE_PHONE_NUMBER_KEY,"");
        String username = sharedPref.getString(Constants.PREFERENCE_USER_NAME_KEY, "");
        Log.i("phone_number", "from last session "+phoneNumber);
        Log.i("user_name", "from last session "+username);

        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number_editText);
        mPhoneNumberEditText.setText(phoneNumber);
        mUserNameEditText = (EditText) findViewById(R.id.name_editText);
        mPhoneNumberEditText.setText(username);
    }

    public void updatePhoneNumber(String phoneNumberStr)
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREFERENCE_PHONE_NUMBER_KEY, phoneNumberStr);
        Log.i("phone_number","commit "+ phoneNumberStr);
        editor.commit();
    }

    public void updateName(String nameStr)
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREFERENCE_USER_NAME_KEY, nameStr);
        Log.i("user_name","commit "+ nameStr);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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

    public void startMainActivity(View view)
    {

        Intent intent = new Intent(this, DisplayAccelerometerData.class);

        String phoneNumber = mPhoneNumberEditText.getText().toString();
        String username = mUserNameEditText.getText().toString();
        Log.i("phone_number", phoneNumber);
        Log.i("user_name", username);
        intent.putExtra(Constants.PHONE_NUMBER_MESSAGE, phoneNumber);
        intent.putExtra(Constants.USER_NAME_MESSAGE, username);
        updatePhoneNumber(phoneNumber);
        updateName(username);

        startActivity(intent);

    }
    //for testing gps
    public void startGpsBasics(View v)
    {
        Intent intent = new Intent(this, GpsBasics.class);
        startActivity(intent);
    }

}
