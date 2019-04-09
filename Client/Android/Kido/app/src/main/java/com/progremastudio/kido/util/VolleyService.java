/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.util;

import android.content.Context;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.VolleyLog;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.progremastudio.kido.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class VolleyService {
/*
    private Context context;
    private String restApi;

    public VolleyService(Context context) {
        this.context = context;
        this.restApi = "/post/activity";
    }

    public void volleyRequest(Map<Object, Object> param) {
        String url =
                context.getString(R.string.server_dns_url) +
                        restApi +
                        "?access_token=" + param.get("access_token") +
                        "&uid=" + ((Map<Object, Object>)param.get("user")).get("id");
        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        request.setTag("VolleyPatterns");
        Volley.newRequestQueue(context).add(request);
    }
*/
}
