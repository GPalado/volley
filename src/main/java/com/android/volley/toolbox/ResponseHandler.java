package com.android.volley.toolbox;

import android.support.annotation.GuardedBy;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

/**
 * Base class for handling response parsing and delivery.
 *
 * @param <T> Parsed type for this response
 */
public abstract class ResponseHandler<T> {
    /** Lock to guard mListener as it is cleared on cancel() and read on delivery. */
    private final Object mLock = new Object();

    @GuardedBy("mLock")
    private Response.Listener<T> mListener;

    /**
     * Creates a new ResponseHandler.
     *
     * @param listener Listener to receive the response
     */
    public ResponseHandler(Response.Listener<T> listener){
        mListener = listener;
    }

    public abstract Response<T> parseNetworkResponse(NetworkResponse response);

    public void deliverResponse(T response) {
        Response.Listener<T> listener;
        synchronized (mLock){
            listener = mListener;
        }
        if(listener != null){
            listener.onResponse(response);
        }
    }
}
