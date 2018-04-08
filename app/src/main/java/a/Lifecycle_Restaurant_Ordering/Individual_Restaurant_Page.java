package a.Lifecycle_Restaurant_Ordering;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import a.common.CheckConnection;
import a.common.GlobalMethods;
import a.common.MySingleton;
import a.dot7.R;
import a.getter_setter.Dishes;
import a.getter_setter.Restaurants;

public class Individual_Restaurant_Page extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private boolean appBarExpanded = true;
    private DishesAdapter dishesAdapter;
    private RecyclerView.LayoutManager  Layout;
    private List<Dishes> data;
    private Intent intent;
    private String URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_restaurant_page);
        intent = getIntent();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Restaurant View");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.dot7_banner);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(Individual_Restaurant_Page.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        set_RecyclerView_Details();
        addRowData();
        GetProducts();
    }

    private void set_RecyclerView_Details()
    {
        data = new ArrayList<>();
        recyclerView =  findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        Layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(Layout);
    }

    private void addRowData()
    {
       // data = new ArrayList<>();
       // Restaurants RowData;
        /*for(int i=0;i<5;i++) {

            RowData = new Restaurants();
            RowData.setRestaurantCuisine("Cuisines List");
            RowData.setRestaurantFavflag("0");
            RowData.setRestaurantName("Restaurant Name");
            RowData.setRestaurantRating("4.5");
            RowData.setRestaurantTiming("Timing");
            RowData.setRestaurantImage("https://drive.google.com/TRlN7QAxhU7SMzdq37XDvc&export=download");
            RowData.setShowShimmer(true);
            AllRowData.add(RowData);
        }*/
        dishesAdapter = new
                DishesAdapter(Individual_Restaurant_Page.this,data);
        recyclerView.setAdapter(dishesAdapter);
    }

    public void GetProducts()
    {
        URL = GlobalMethods.getURL() + "Product/" + intent.getStringExtra("RestaurantKey");
        //AllRowData = new ArrayList<>();
        // RequestQueue Queue;
        //determineService = 2;
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
                Dishes RowData;
                if (response != null) {
                    for ( int i = 0 ; i < response.length() ; i++)
                    {
                        RowData = new Dishes();
                        JSONObject json ;
                        try
                        {
                            json = response.getJSONObject(i);
                            //RowData.setShowShimmer(true);
                            RowData.setDishName(json.getString("productName"));
                            RowData.setDishImageUrl(json.getString("productImage"));
                            RowData.setDishAvailability(json.getString("availabilityFlag"));
                            RowData.setDishPrice(json.getString("price"));
                            RowData.setDishVFlag(json.getString("veg_nVeg_Flag"));

                        }
                        catch (Exception e) {
                            Log.e("Error: " , String.valueOf(e));
                        }
                       /* if(i<5)
                            data.set(i, RowData);
                        else
                            data.add(RowData);
                            */
                       data.add(i,RowData);
                    }
                }


                dishesAdapter = new
                        DishesAdapter(Individual_Restaurant_Page.this,data);
                recyclerView.setAdapter(dishesAdapter);
               /* Adapter.setOnItemClickListener(new RestaurantsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Snackbar.make(view, "Card "+position+" clicked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        Intent intent = new Intent(Restaurant_Recycler_View.this,Individual_Restaurant_Page.class);
                        startActivity(intent);
                    }
                });
                Restaurant_recycler_view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for(Restaurants restaurant_each_row_data : AllRowData){
                            restaurant_each_row_data.setShowShimmer(false);
                        }
                        Adapter.notifyDataSetChanged();
                    }
                },3000);*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               /* if(CheckConnection.getInstance(Restaurant_Recycler_View.this).getNetworkStatus())
                {
                    Log.e("VolleyError: ",error.toString());
                }
                else {
                    Restaurant_recycler_view.setVisibility(View.GONE);
                    Error_Image.setVisibility(View.VISIBLE);
                    Error_Button.setVisibility(View.VISIBLE);
                    //******************************************set error internet connection image*********************************
                }*/
               Log.d("HAR",error.toString());
            }
        });

        //  Queue = Volley.newRequestQueue(this);
        // Queue.add(jsonArrayRequest);
        MySingleton.getInstance(this).addToJsonRequestQueue(jsonArrayRequest);
    }
}
