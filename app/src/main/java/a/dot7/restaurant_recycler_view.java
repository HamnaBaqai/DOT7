package a.dot7;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.security.PrivateKey;

import a.common.RestaurantView_Adapter;
import a.common.Restaurant_Each_Row_data;

public class restaurant_recycler_view extends Activity {

    private RecyclerView my_recycler_view;
    private List<Restaurant_Each_Row_data> AllRowData;
    private RecyclerView.LayoutManager  Layout;
    private RecyclerView.Adapter Adapter;

    List<Restaurant_Each_Row_data> DataAdapter;
    final private String URL = "";              // Url from where data will be extracted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_recycler_view);
        my_recycler_view=findViewById(R.id.rec_view);

        set_RecyclerView_Details();

        JSson_Data_Web_Call();

    }

    private void set_RecyclerView_Details()
    {
        DataAdapter = new ArrayList<>();
        my_recycler_view = findViewById(R.id.rec_view);
        my_recycler_view.setHasFixedSize(true);
        Layout = new LinearLayoutManager(this);
        my_recycler_view.setLayoutManager(Layout);
    }

    public void JSson_Data_Web_Call()
    {

        RequestQueue Queue;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Json_Parse_Data(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Queue = Volley.newRequestQueue(this);
        Queue.add(jsonArrayRequest);
    }

    public void Json_Parse_Data(JSONArray array)
    {
        for ( int i = 0 ; i < array.length() ; i++)
        {
            Restaurant_Each_Row_data RowData = null;
            JSONObject json = null;
            try
            {                                           // harneet fill json ids
                json = array.getJSONObject(i);
                RowData.setRestaurantCuisine(json.getString(""));
                RowData.setRestaurantFavflag(json.getBoolean(""));
                RowData.setRestaurantName(json.getString(""));
                RowData.setRestaurantRating((float) json.getDouble(""));
                RowData.setRestaurantTiming(json.getString(""));
                RowData.setRestaurantImage(json.getString(""));
            }
            catch (JSONException e) {

            }
            AllRowData.add(RowData);
        }
        Adapter = new RestaurantView_Adapter(this,AllRowData);
        my_recycler_view.setAdapter(Adapter);
    }

}
