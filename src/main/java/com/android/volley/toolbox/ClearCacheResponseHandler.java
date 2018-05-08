package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

public class ClearCacheResponseHandler extends ResponseHandler<Object> {
    public ClearCacheResponseHandler(Response.Listener<Object> listener) {
        super(listener);
    }

    @Override
    public Response parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    public void deliverResponse(Object response) {}
}
