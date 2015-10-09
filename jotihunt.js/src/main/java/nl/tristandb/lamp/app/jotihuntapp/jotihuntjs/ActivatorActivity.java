package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Calendar;


public class ActivatorActivity extends ActionBarActivity {
    public static final String LISTEN_LOCATION = "listenLocation";
    private AlarmManager alarmManager;
    private int FIRST_TIME = 3000;
    private static int REPEATING_TIME = 60 * 1000;
    private PendingIntent pendingIntent;
    public static final String PREFS_NAME = "LocationListening";
    Switch locationSwitch;

    protected void onResume(){
        super.onResume();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isChecked = settings.getBoolean(LISTEN_LOCATION, false);
        setLocationSwitch(isChecked);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activator);

        locationSwitch = (Switch) findViewById(R.id.locationswitch);
        locationSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLocationSwitch(isChecked);
                if (isChecked) {
                    // Turn on location listening service
                    registerAlarm(buttonView);
                } else {
                    // Turn off location listening service
                    cancelAlarm(buttonView);
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isChecked = settings.getBoolean(LISTEN_LOCATION, false);
        setLocationSwitch(isChecked);
    }

    /**
     * Turns off location listening service
     *
     * @param buttonView For getting context
     */
    private void cancelAlarm(CompoundButton buttonView) {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Turn on location listening service
     *
     * @param buttonView For getting context
     */
    private void registerAlarm(CompoundButton buttonView) {
        //this.cancelAlarm(buttonView);
        Intent intent = new Intent(this, SendService.class);
        pendingIntent = PendingIntent.getBroadcast(buttonView.getContext(), 0, intent, 0);
        FIRST_TIME += SystemClock.elapsedRealtime();
        alarmManager = (AlarmManager) buttonView.getContext().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), REPEATING_TIME, pendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activator, menu);
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

    @SuppressLint("NewApi")
    public void setLocationSwitch(boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LISTEN_LOCATION, isChecked);
        editor.commit();
        locationSwitch.setChecked(isChecked);
    }
}
