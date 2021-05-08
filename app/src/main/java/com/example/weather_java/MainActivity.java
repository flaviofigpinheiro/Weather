package com.example.weather_java;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    final String APP_ID = "96b0283e45ffad3ca5ade0e09927960e";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView NCity, wState, Temp;
    ImageView mwIcon;

    RelativeLayout mCFinder;


    LocationManager mLocManager;
    LocationListener mLocListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wState = findViewById(R.id.wCondition);
        Temp = findViewById(R.id.temp);
        mwIcon = findViewById(R.id.wIcon);

        mCFinder = findViewById(R.id.cChanger);
        NCity = findViewById(R.id.cName);



    }

    //update the information on the weather
    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        String city= mIntent.getStringExtra("City");
        if(city!=null) {
            getWForNewCity(city);
        } else {
            getWForCurrentLoc();
        }
    }


    //get the city
    private void getWForNewCity(String city) {
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        connectNetwork(params);

    }


    //get the current location
    private void getWForCurrentLoc() {

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params =new RequestParams();
                params.put("lat" ,Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                connectNetwork(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
                Toast.makeText(MainActivity.this,"not able to get location",Toast.LENGTH_SHORT).show();
            }
        };

        //permission request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocListener);

    }

    //permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Location get Succesffully",Toast.LENGTH_SHORT).show();
                getWForCurrentLoc();
            }
            else
            {
                //user denied the permission
                Toast.makeText(MainActivity.this,"User denied the permission",Toast.LENGTH_SHORT).show();
                getWForCurrentLoc();

            }
        }
    }


    //getting the weather data
    private  void connectNetwork(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(MainActivity.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                WData weatherD= WData.fromJson(response);
                updUI(weatherD);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });



    }
    // getting and inserting the weather condition.
    private  void updUI(WData weather){


        Temp.setText(weather.getmTemp());
        NCity.setText(weather.getMcity());

        wState.setText(weather.getmWType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());
        mwIcon.setImageResource(resourceID);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocManager !=null)
        {
            mLocManager.removeUpdates(mLocListener);
        }
    }
    //calling the citys after click the button.
    public void callDublin(View view) {
        getWForNewCity("Dublin");
    }

    public void callLyon(View view) {
        getWForNewCity("Lyon");
    }
}