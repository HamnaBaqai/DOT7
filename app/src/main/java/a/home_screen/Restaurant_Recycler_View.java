package a.home_screen;

/**
 * Created by TUSHAR on 02-04-18.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import a.common.CheckConnection;
import a.common.GlobalMethods;
import a.common.MySingleton;
import a.dot7.Login;
import a.dot7.R;
import a.getter_setter.Restaurant_Each_Row_data;


public class Restaurant_Recycler_View extends AppCompatActivity implements View.OnClickListener {

    String Contact;
    private RecyclerView Restaurant_recycler_view;
    private List<Restaurant_Each_Row_data> AllRowData;
    private RecyclerView.LayoutManager  Layout;
    private RestaurantView_Adapter Adapter;
    private String URL ;
    private DrawerLayout mDrawerLayout;
    View view;
    ImageView Error_Image;
    TextView Error_Message;
    Button Error_Button;
    String url = GlobalMethods.getURL() + "Login";
    AlertDialog CustomDialog;
    int determineService = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_recycler_view);
        Toolbar toolbar = findViewById(R.id.Restaurant_Page_Toolbar);
        mDrawerLayout = findViewById(R.id.Drawer_Layout);
        Error_Image = findViewById(R.id.Restaurant_View_Error);
        Error_Message = findViewById(R.id.Restaurant_Error_Message);
        Error_Button = findViewById(R.id.Restaurant_View_RetryButton);
        Error_Button.setOnClickListener(Restaurant_Recycler_View.this);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        view = fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //Restaurant_recycler_view = findViewById(R.id.rec_view);
        Log.d("HAR","Recycler view me aaya");
        set_RecyclerView_Details();
        Log.d("HAR","details set ho gyi");
        addRowData();
        getContact();
        URL = GlobalMethods.getURL()+ "Restaurant_Main/" + Contact;
        if(CheckConnection.getInstance(this).getNetworkStatus())
            checkUserLogin();
        else
        {
            Restaurant_recycler_view.setVisibility(View.GONE);
            Error_Image.setVisibility(View.VISIBLE);
            Error_Message.setVisibility(View.VISIBLE);
            Error_Button.setVisibility(View.VISIBLE);
            //******************************************set error internet connection image*********************************
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.res_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.Drawer_Layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void getContact()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("logDetails",
                Context.MODE_PRIVATE);
        Contact = sharedPreferences.getString("UserName",null);
    }

    private void addRowData()
    {
        //AllRowData = new ArrayList<>();
        Restaurant_Each_Row_data RowData;
        for(int i=0;i<5;i++) {

            RowData = new Restaurant_Each_Row_data();
            RowData.setRestaurantCuisine("Cuisines List");
            RowData.setRestaurantFavflag("0");
            RowData.setRestaurantName("Restaurant Name");
            RowData.setRestaurantRating("4.5");
            RowData.setRestaurantTiming("Timing");
            RowData.setRestaurantImage("https://drive.google.com/TRlN7QAxhU7SMzdq37XDvc&export=download");
            RowData.setShowShimmer(true);
            AllRowData.add(RowData);
        }
        Adapter = new
                RestaurantView_Adapter(Restaurant_Recycler_View.this,AllRowData);
        Restaurant_recycler_view.setAdapter(Adapter);
    }


    private void set_RecyclerView_Details()
    {
        AllRowData = new ArrayList<>();
        Restaurant_recycler_view = findViewById(R.id.Recycler_View);
        Restaurant_recycler_view.setHasFixedSize(true);
        Layout = new LinearLayoutManager(this);
        Restaurant_recycler_view.setLayoutManager(Layout);
    }

    public void Json_Data_Web_Call()
    {

        //AllRowData = new ArrayList<>();
       // RequestQueue Queue;
        determineService = 2;
        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {

                //Json_Parse_Data(response);
                Log.d("HAR","Response aaya");
                Log.d("HAR",response1.toString());
                String str = response1.toString();
                JSONArray response = null;
                try {
                    response = new JSONArray(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Restaurant_Each_Row_data RowData;
                if (response != null) {
                    for ( int i = 0 ; i < response.length() ; i++)
                    {
                        RowData = new Restaurant_Each_Row_data();
                        JSONObject json ;
                        try
                        {
                            json = response.getJSONObject(i);
                            RowData.setRestaurantCuisine(json.getString("cuisines"));
                            RowData.setRestaurantFavflag(json.getString("isFavourite"));
                            RowData.setRestaurantName(json.getString("restaurantName"));
                            RowData.setRestaurantRating(json.getString("rating"));
                            RowData.setRestaurantTiming(json.getString("time"));
                            RowData.setRestaurantImage(json.getString("imageURL"));
                            RowData.setShowShimmer(true);
                            Log.d("HAR","Restaurant Name: "+ json.getString("restaurantName"));

                        }
                        catch (Exception e) {
                            Log.e("Error: " , String.valueOf(e));
                        }
                        if(i<5)
                            AllRowData.set(i, RowData);
                        else
                            AllRowData.add(RowData);
                    }
                }


                Adapter = new
                        RestaurantView_Adapter(Restaurant_Recycler_View.this,AllRowData);
                Restaurant_recycler_view.setAdapter(Adapter);
                Adapter.setOnItemClickListener(new RestaurantView_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Snackbar.make(view, "Card "+position+" clicked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                });
                Restaurant_recycler_view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for(Restaurant_Each_Row_data restaurant_each_row_data : AllRowData){
                                restaurant_each_row_data.setShowShimmer(false);
                        }
                        Adapter.notifyDataSetChanged();
                    }
                },3000);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(CheckConnection.getInstance(Restaurant_Recycler_View.this).getNetworkStatus())
                {
                    Log.e("VolleyError: ",error.toString());
                }
                else {
                    Restaurant_recycler_view.setVisibility(View.GONE);
                    Error_Image.setVisibility(View.VISIBLE);
                    Error_Button.setVisibility(View.VISIBLE);
                    //******************************************set error internet connection image*********************************
                }
            }
        });

      //  Queue = Volley.newRequestQueue(this);
       // Queue.add(jsonArrayRequest);
        MySingleton.getInstance(this).addToJsonRequestQueue(jsonArrayRequest);
    }

   private void checkUserLogin()
   {
       SharedPreferences sharedPreferences = getSharedPreferences("logDetails",
               Context.MODE_PRIVATE);
       final String  UserName = sharedPreferences.getString("UserName",null);
       final String Password = sharedPreferences.getString("Password",null);
       try {
           StringRequest request = new StringRequest(Request.Method.POST, url, new Response.
                   Listener<String>() {

               @Override
               public void onResponse(String s) {
                   String StatusCode = GlobalMethods.GetSubString(s);
                   Log.d("HAR", s);
                   Log.d("HAR", "Satus code:" + StatusCode);
                   //Log.d("HAR",StatusCode);

                   // **********stop progress bar*************************


                   if (StatusCode.contains("302")) {
                       // GlobalMethods.print(SplashActivity.this, "Data Found");
                       // Intent intent = new Intent(SplashActivity.this, TempActivity.class);
                       Json_Data_Web_Call();


                   } else {

                       AlertDialog.Builder builder=new AlertDialog.Builder(Restaurant_Recycler_View.this);
                       builder.setMessage("You have logged out");
                       builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.cancel();
                               startActivity(new Intent(Restaurant_Recycler_View.this, Login.class));
                           }
                       });
                       CustomDialog=builder.create();
                       CustomDialog.show();


                   }
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError volleyError) {
                   if(!(CheckConnection.getInstance(Restaurant_Recycler_View.this).getNetworkStatus()))

                   {
                       Restaurant_recycler_view.setVisibility(View.GONE);
                       Error_Image.setVisibility(View.VISIBLE);
                       Error_Button.setVisibility(View.VISIBLE);
                       //******************************************set error internet connection image*********************************
                   }
                   Log.d("HAR", volleyError.toString());
                   Log.d("HAR", "Error");
                   //Nitish and pooja, handle this error with a alert box or something
               }
           }) {
               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   Map<String, String> parameters = new HashMap<>();
                   parameters.put("LoginID", UserName);
                   parameters.put("password", Password);
                   // if(Name != null)
                   //   parameters.put("Name",Name);
                   return parameters;
               }
           };
           MySingleton.getInstance(this).addToRequestQueue(request);

           Log.d("HAR", "Service ab return kr ri hai");

       } catch (Exception ex) {

       }

   }

    @Override
    public void onClick(View v) {
        if(CheckConnection.getInstance(this).getNetworkStatus()) {
            Error_Image.setVisibility(View.GONE);
            Error_Button.setVisibility(View.GONE);
            Restaurant_recycler_view.setVisibility(View.VISIBLE);
            if(determineService == 1)
            checkUserLogin();
            else
                Json_Data_Web_Call();
        }

    }
}
