package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Tristan on 28-7-2015.
 */
public class SendService extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        HTTPClient client = HTTPClient.getInstance(context);
        LocationListener locationListener = new LocationListener(context);

        RequestParams params = new RequestParams();
        params.put("lat", locationListener.getLatitude());
        params.put("lng", locationListener.getLongitude());
        client.getAsyncHttpClient().post("http://" + Configuration.HOST_ADDRESS + "/api/location", params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // called when response HTTP status is "200 OK"
            }

            public void onFailure(int statusCode, Header[] headers, JSONArray errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(context, "Login niet correct. Log alsjeblieft opnieuw in!", Toast.LENGTH_LONG);
                Intent myIntent = new Intent(context, MainActivity.class);
                Log.d("Login", "Failed: error" + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        System.gc();
    }
}
