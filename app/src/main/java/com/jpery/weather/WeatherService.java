package com.jpery.weather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface WeatherService {
    @GET("weather")
    Call<WeatherObject> getWeatherByLocation(@QueryMap Map<String, String> options);
}





