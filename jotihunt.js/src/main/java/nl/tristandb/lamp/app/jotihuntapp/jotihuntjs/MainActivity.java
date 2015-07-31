package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    HTTPClient client;
    /**
     * Create a new activity, set a OnClickListener to R.id.qrcode that starts a IntentIntegrator
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.qrcode);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HTTPClient.getInstance(v.getContext()).clearCookieStore();
                client  = HTTPClient.getInstance(v.getContext());
                IntentIntegrator.initiateScan((Activity) v.getContext());
            }

        });
        final Button buttonCredential = (Button)findViewById(R.id.credentials);
        buttonCredential.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CredentialsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    /**
     * Process the QR-code, send it to the server.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Display loading screen
                final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                        .title("Logging in.")
                        .content("Please wait")
                        .progress(true, 0)
                        .cancelable(false)
                        .show();

                // Send information to server
                RequestParams params = new RequestParams();
                params.put("authentication", result.getContents());
                client.getAsyncHttpClient().post("http://" + Configuration.HOST_ADDRESS + "/api/authenticate/qr", params, new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                        // called when response HTTP status is "200 OK"
                        Intent myIntent = new Intent(MainActivity.this, ActivatorActivity.class);
                        String name = null;
                        materialDialog.cancel();
                        try {
                            Log.d("Session", timeline.getString("session"));
                           // HTTPClient.getInstance(null).addCookie("connect.sid", timeline.getString("session"));
                            name = timeline.getString("name");
                            Toast.makeText(MainActivity.this, "Ingelogd als " + name, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(Header header: headers){
                            Log.d("COOKIE", header.getName() + header.getValue() + header.getElements());
                        }
                        myIntent.putExtra("name", name); //Optional parameters
                        MainActivity.this.startActivity(myIntent);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.d("Login", "Failed: error" + statusCode);
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });
            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
