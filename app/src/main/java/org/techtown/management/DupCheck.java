package org.techtown.management;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsl on 2018. 3. 26..
 */

public class DupCheck extends StringRequest{

    public DupCheck(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    private Map<String, String> parameters;

    public void setUserID(String userID) {

        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    public Map<String, String> getParams() {
        return parameters;
    }
}
