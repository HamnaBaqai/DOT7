package a.common;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import a.dot7.Otp_generate_read;

/**
 * Created by TUSHAR and Harneet on 11-03-18.
 */

public class Services {
    private int StatusFlag = 0;
    static Services UserCredentials;
    private Context context;
    private String StatusCode = null;

    Services(Context context)
    {
        this.context = context;
    }
    public static Services getInstance(Context context)
    {
        if(UserCredentials == null) {
            UserCredentials = new Services(context);
        }
        return UserCredentials;
    }

    public boolean Validate(final String UserName, final String Password)
    {
        boolean Status;
        String Url = "http://172.31.143.55:3000/Login";
        Status = ServiceCall(UserName,Password,null,Url);
        return Status;
    }
    public boolean Register(final String UserName, final String Password, final String Contact)
    {
        String url="http://172.31.143.55:3000/";          //harneet fill here url for registet
        boolean Status=ServiceCall(UserName,Password,Contact,url);

        if (Status)
        {
            //intent to otp activity and start otp generation and reading
        }
        return Status;
    }
    public boolean ServiceCall(final String UserName  /* Contact */, final String Password, final String Name,String url)
    {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.
                    Listener<String>() {

                @Override
                public void onResponse(String s) {
                    StatusCode = GlobalMethods.GetSubString(s);
                    Log.d("HAR",StatusCode);
                    if (StatusCode.contains("302")) {
                        GlobalMethods.print(context,"Data Found");
                        StatusFlag=1;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("HAR",volleyError.toString());
                    //Nitish and pooja, handle this error with a alert box or something
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("LoginID", UserName);
                    parameters.put("password", Password);
                    if(Name != null)
                        parameters.put("Name",Name);
                    return parameters;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(request);

            return StatusFlag == 1;


        }
        catch (Exception ex)
        {
            return false;
        }
    }
}


        //********************OLD SERVICE CALL CODE*******************************//

/*String loginUrl = "http://192.168.43.106:3000/Login";      //Harneet Fill this url for login details
        final int[] result = new int[1];
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, loginUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("HAR",response.toString());
                    Toast.makeText(context,"Internet Not Connected",Toast.LENGTH_LONG).show();
                   //result[0] = response.getInt("status");    //Harneet mark this key
                    String chk = response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Connected",Toast.LENGTH_LONG).show();
                Log.d("HAR",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("LoginID","9039216432");
                data.put("password","hello");
                return data;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        /*if(result[0]==302)
        return true;
        else
            return false;*/