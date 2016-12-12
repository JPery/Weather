package com.jpery.weather;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements GPSTracker.OnLocationChangedCallback{

    private GPSTracker gps;
    private TextView textLocation;
    private String weather;
    private TextView temperature;
    private ProgressBar pb;
    private GifImageView background;
    private static final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textLocation = (TextView) findViewById(R.id.city);
        temperature = (TextView) findViewById(R.id.temperature);
        background = (GifImageView) findViewById(R.id.backgroundImage);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        final String[] LOCATION_PERMS={
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        final int INITIAL_REQUEST=1337;
        if(!(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)))
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, INITIAL_REQUEST);
        GPSTracker gps = new GPSTracker(this);

        // Check if GPS enabled
        if(!gps.canGetLocation()) {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }else {
            requestWeather(gps.getLatitude(), gps.getLongitude());
        }
    }

    private void setBackgroungImage(){
        if (getResources().getConfiguration().orientation == 1) {
            if (weather.contains("rain")) {
                background.setImageResource(R.drawable.rain);
            } else if (weather.contains("clouds")) {
                background.setImageResource(R.drawable.clouds);
            } else if (weather.contains("snow")) {
                background.setImageResource(R.drawable.snow);
            } else if (weather.contains("thunderstorm")) {
                background.setImageResource(R.drawable.thunderstorm);
            } else if (weather.contains("mist")) {
                background.setImageResource(R.drawable.mist);
            } else if (weather.contains("clear")) {
                background.setImageResource(R.drawable.clear);
            }
        } else if (getResources().getConfiguration().orientation == 2) {
            if (weather.contains("rain")) {
                background.setImageResource(R.drawable.rain_landscape);
            } else if (weather.contains("clouds")) {
                background.setImageResource(R.drawable.clouds_landscape);
            } else if (weather.contains("snow")) {
                background.setImageResource(R.drawable.snow_landscape);
            } else if (weather.contains("thunderstorm")) {
                background.setImageResource(R.drawable.thunderstorm_landscape);
            } else if (weather.contains("mist")) {
                background.setImageResource(R.drawable.mist_landscape);
            } else if (weather.contains("clear")) {
                background.setImageResource(R.drawable.clear_landscape);
            }
        }
    }

    private void requestWeather(double latitude, double longitude){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Map<String, String> data = new HashMap<>();
        data.put("lat", "" + latitude);
        data.put("lon", "" + longitude);
        data.put("appid", "9467b31c92dd72ceb496e488cc73bc17");
        service.getWeatherByLocation(data).enqueue(new Callback<WeatherObject>() {
            @Override
            public void onResponse(Call<WeatherObject> call, Response<WeatherObject> response) {
                textLocation.setText(response.body().getName());
                String temp = "" + (response.body().getMain().getTemp().intValue() - 273) + (" ºC");
                temperature.setText(temp);
                weather = response.body().getWeather().get(0).getDescription();
                setBackgroungImage();
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<WeatherObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0]==0){
            requestWeather(gps.getLatitude(), gps.getLongitude());
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(Location location){
        requestWeather(location.getLatitude(), location.getLongitude());
    }

}
