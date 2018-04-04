package a.common;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import a.dot7.R;
import a.dot7.Set_New_Password;
import a.home_screen.Restaurant_Recycler_View;

/**
 * Created by TUSHAR on 28-03-18.
*/

public class OTP_Reader extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String OTP = null;
    Intent intent = null;
    Context context = this;
    private String Name=null, Contact=null, Password=null;
    String determine_activity=null;
    String url = GlobalMethods.getURL() + "Register";
   // String url = "http://192.168.43.161:3000/Register";
    boolean Flag = false;
    String StatusCode;
    PinEntryEditText OTPTEXT;
    String OTP_Received = null;
    AppCompatButton verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_read);
        verify = findViewById(R.id.Verify_Otp);
        verify.setOnClickListener(this);
        intent = getIntent();
        Name = intent.getStringExtra("Name");
        Contact = intent.getStringExtra("Contact");
        Password = intent.getStringExtra("Password");
        OTP = intent.getStringExtra("OTP");
        OTPTEXT = findViewById(R.id.Txt_Pin_Entry);
        if(Name == null && Password == null && Contact != null && OTP != null)
            determine_activity = "ForgotPassword";
        else
            determine_activity = "Register";

         read();

    }
    public void read()
    {
        Permissions.getInstance(this).checkpermissions(this, REQUEST_ID_MULTIPLE_PERMISSIONS);
        if (true)
        {
            Log.d("HAR", "Permission granted");

            SmsReceiver.bindListener(new SmsListener() {

                @Override
                public void messageReceived(String message) {
                    Log.d("HAR", "Message is:"+message);

                    OTP_Received = message.substring(26, 30);
                    OTPTEXT.setText(OTP_Received);

                }

            });
        }

        else
        {
            MyDialog myDialog = new MyDialog(this,"Cannot access OTP without" +
                    " permissions!","Grant Permissions");
            read();
        }
    }

    @Override
    public void onClick(View v) {

        if (OTP_Received==null && OTPTEXT != null) {
            OTPTEXT.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    OTP_Received = str.toString();
                }
            });
        }


        if(OTP_Received.equals(OTP))
        {

            Log.d("HAR", "OTP verified");

            // If otp is required for forgot password
            if(determine_activity.equals("ForgotPassword"))
            {
                Intent intent = new Intent(OTP_Reader.this,Set_New_Password.class);
                intent.putExtra("Contact",Contact);
                startActivity(intent);
            }
            Flag = true;
            //calling webservice to register user
            callService();

        }
        else
        {
            //Here OTP entered is wrong, handle the case
        }

    }
    private void callService()
    {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.
                    Listener<String>() {

                @Override
                public void onResponse(String s) {
                    StatusCode = GlobalMethods.GetSubString(s);
                    Log.d("HAR", s);
                    if (StatusCode.contains("201")) {
                        SharedPreferences sharedPreferences=getSharedPreferences("logDetails", OTP_Reader.this.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Name",Name);
                        editor.putString("Password",Password);
                        editor.putString("UserName",Contact);
                        editor.commit();

                        Log.d("HAR", "Local file created");
                        // **********stop progress bar*************************
                        startActivity(new Intent(OTP_Reader.this, Restaurant_Recycler_View.class));
                    }
                    else{

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("HAR", volleyError.toString());
                    Log.d("HAR", "Error");
                    //Nitish and pooja, handle this error with a alert box or something
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("LoginID", Contact);
                    parameters.put("password", Password);
                    if (Name != null)
                        parameters.put("Name", Name);
                    return parameters;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(request);
        }
        catch (Exception ex)
        {
            //return false;
        }
    }
}