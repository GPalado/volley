package com.android.volley;

import android.support.annotation.GuardedBy;

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

    /**
     * Parses the raw network response and return an appropriate response type. This method will be
     * called by a request to be parsed. The response will not be delivered if you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * Performs the delivery of the parsed response to their listeners. The given response is
     * guaranteed to be non-null; responses that fail to parse are not delivered.
     *
     * @param response The parsed response returned by {@link
     *     #parseNetworkResponse(NetworkResponse)}
     */
    protected void deliverResponse(T response) {
        Response.Listener<T> listener;
        synchronized (mLock){
            listener = mListener;
        }
        if(listener != null){
            listener.onResponse(response);
        }
    }

    /**
     * Cancels the handling of the response by ensuring the listener provided in the constructor
     * is not invoked.
     */
    protected void cancel() {
        synchronized (mLock) {
            mListener = null;
        }
    }
}
