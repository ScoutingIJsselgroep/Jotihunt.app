package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class CredentialsActivity extends ActionBarActivity {
    HTTPClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        final Button button = (Button) findViewById(R.id.log_in);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HTTPClient.getInstance(v.getContext()).clearCookieStore();
                client = HTTPClient.getInstance(v.getContext());
                logIn(v);
            }


        });
    }

    private void logIn(View v) {
        // Display loading screen
        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("Logging in.")
                .content("Please wait")
                .progress(true, 0)
                .cancelable(false)
                .show();

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        // Send information to server
        RequestParams params = new RequestParams();
        params.put("username", username.getText());
        params.put("password", password.getText());
        client.getAsyncHttpClient().post("http://" + Configuration.HOST_ADDRESS + "/api/authenticate/cd", params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // called when response HTTP status is "200 OK"

                Intent myIntent = new Intent(CredentialsActivity.this, ActivatorActivity.class);
                String name = null;
                materialDialog.cancel();
                if(timeline.has("error")){
                    materialDialog.cancel();
                    username.setBackgroundColor(Color.argb(50, 255, 0, 0));
                    password.setBackgroundColor(Color.argb(50, 255, 0, 0));
                    Log.d("Login", "Failed: error" + statusCode);
                }else{
                    try {
                        Log.d("Session", timeline.getString("session"));
                        // HTTPClient.getInstance(null).addCookie("connect.sid", timeline.getString("session"));
                        name = timeline.getString("name");
                        Toast.makeText(CredentialsActivity.this, "Ingelogd als " + name, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (Header header : headers) {
                        Log.d("COOKIE", header.getName() + header.getValue() + header.getElements());
                    }
                    myIntent.putExtra("name", name); //Optional parameters
                    CredentialsActivity.this.startActivity(myIntent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                materialDialog.cancel();
                username.setBackgroundColor(Color.argb(50, 255, 0, 0));
                password.setBackgroundColor(Color.argb(50, 255, 0, 0));
                Log.d("Login", "Failed: error" + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credentials, menu);
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
