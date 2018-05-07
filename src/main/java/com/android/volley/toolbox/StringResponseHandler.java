package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import java.io.UnsupportedEncodingException;

public class StringResponseHandler extends ResponseHandler<String> {

    public StringResponseHandler(Response.Listener<String> listener) {
        super(listener);
    }

    @Override
    public Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            // Since minSdkVersion = 8, we can't call
            // new String(response.data, Charset.defaultCharset())
            // So suppress the warning instead.
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
