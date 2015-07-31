package nl.tristandb.lamp.app.jotihuntapp.jotihuntjs;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Created by Tristan on 27-7-2015.
 */
public class HTTPClient {

    public static HTTPClient instance;

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private PersistentCookieStore cookieStore;

    /**
     * A private Constructor prevens any other class from instantiating.
     */
    private HTTPClient(){

    }

    /**
     * Static 'instance' method
     * @return HTTPClient
     */
    public static HTTPClient getInstance(Context context){
        if(instance == null){
            instance = new HTTPClient();
            instance.setCookieStore(context);
        }
        return instance;
    }

    public AsyncHttpClient getAsyncHttpClient(){
        return asyncHttpClient;
    }

    /**
     * Sets the Cookiestore so that an persistent Cookie Store can be used
     * @param context
     */
    public void setCookieStore(Context context){
        cookieStore = new PersistentCookieStore(context);
        cookieStore.clear();
        asyncHttpClient.setCookieStore(cookieStore);
    }

    /**
     * Adds a Cookie to the Cookiestore
     */
    public void addCookie(String name, String value){
        BasicClientCookie newCookie = new BasicClientCookie(name, value);
        newCookie.setVersion(1);
        newCookie.setDomain(Configuration.HOST_ADDRESS);
        newCookie.setPath("/");
        cookieStore.addCookie(newCookie);
    }

    public void clearCookieStore() {
        cookieStore.clear();
    }
}
