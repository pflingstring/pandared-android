package red.panda.utils;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import android.content.Context;

public class RequestQueueSingleton
{
    private static RequestQueueSingleton queueSingleton;
    private static Context appContext;
    private RequestQueue requestQueue;

    // constructor
    private RequestQueueSingleton(Context context)
    {
        appContext = context;
        requestQueue = getRequestQueue();
    }

    // returns an instance of  RequestQueueSingleton
    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (queueSingleton == null)
            queueSingleton = new RequestQueueSingleton(context);

        return queueSingleton;
    }

    // get the actual requestQueue
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(appContext.getApplicationContext());

        return requestQueue;
    }

    public static RequestQueue getQueue(Context context)
    {
        return RequestQueueSingleton.getInstance(context).getRequestQueue();
    }

    public <T> void addRequestToQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static <T> void addToQueue(Request<T> req, Context context)
    {
        RequestQueueSingleton.getInstance(context).addRequestToQueue(req);
    }




}
