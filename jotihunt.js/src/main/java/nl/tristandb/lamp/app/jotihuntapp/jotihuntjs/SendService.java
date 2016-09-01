package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Tristan on 28-7-2015.
 */
public class SendService extends WakefulBroadcastReceiver {
    private PowerManager.WakeLock wl;
    private void aquireWakeLock(Context context){
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                "wakeup");
        wl.acquire();
        return;
    }

    private void releaseWakeLock(){
        wl.release();
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        this.aquireWakeLock(context);
        final HTTPClient client = HTTPClient.getInstance(context);
        LocationListener locationListener = new LocationListener(context);

        RequestParams params = new RequestParams();
        params.put("lat", locationListener.getLatitude());
        params.put("lng", locationListener.getLongitude());
        final RequestHandle post = client.getAsyncHttpClient().post("http://" + Configuration.HOST_ADDRESS + "/api/location", params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.String errorResponse, java.lang.Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                if (statusCode == 401) {
                    // Not authenticated, go back to login again.
                    client.clearCookieStore();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    Toast.makeText(context, "Server herstart. Log opnieuw in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        this.releaseWakeLock();
        System.gc();
    }
}
