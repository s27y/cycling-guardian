package yangsun.me.cyclingguardian;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity2Activity extends ActionBarActivity {
    public final static String PHONE_NUMBER_MESSAGE = "me.yangsun.cyclingguardian.PHONE_NUMBER_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
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

    public void startSmsNofity(View view)
    {
        Intent intent = new Intent(this, DisplayAccelerometerData.class);
        EditText editText = (EditText) findViewById(R.id.phone_number_editText);
        String message = editText.getText().toString();
        intent.putExtra(PHONE_NUMBER_MESSAGE, message);
        startActivity(intent);
    }

    public void startGpsBasics(View v)
    {
        Intent intent = new Intent(this, GpsBasics.class);
        startActivity(intent);
    }

}
