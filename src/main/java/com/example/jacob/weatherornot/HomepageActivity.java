package com.example.jacob.weatherornot;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import Information.JSON_Parser;
import Information.Weather_Client;
import Structure_Model.Weather_Hub_Model;
import Utility.GPS_Utility;
import Utility.Utilities;

public class HomepageActivity extends AppCompatActivity {

    TextView temptv, citytv, degreetv, conditiontv;
    ImageView ICON;
    Weather_Hub_Model weather = new Weather_Hub_Model();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        temptv =(TextView)findViewById(R.id.temptv);
        citytv =(TextView)findViewById(R.id.citytv);
        degreetv =(TextView)findViewById(R.id.degreetv);
        conditiontv =(TextView)findViewById(R.id.conditiontv);
        ICON = (ImageView) findViewById(R.id.ICON);
        receiveGpsLocation ();

    }

    public void receiveGpsLocation (){

        ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2018);
        GPS_Utility GPS = new GPS_Utility(getApplicationContext());
        Location GPS_location = GPS.getGPS_LOCATION();
        if (GPS_location != null){

            double LATITUDE = GPS_location.getLatitude();
            double LONGITUDE = GPS_location.getLongitude();

//          Formats API URL
            String Coordinates = new String("lat=" + LATITUDE + "&lon=" + LONGITUDE);
            receiveWeatherInformation(Coordinates);

        }
    }

    public void receiveWeatherInformation(String Coordinates){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{Coordinates + "&unites=imperial"});
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadedImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ICON.setImageBitmap(bitmap);
        }

        private Bitmap downloadedImage(String Code){
            final DefaultHttpClient client = new DefaultHttpClient();
            final HttpGet getRequest = new HttpGet(Utilities.ICON_API_URL + Code + ".png");

            try {

                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK){
                    Log.e("DownloadImage", "error:" + statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();

                if (entity != null) {

                    InputStream inputStream = null;

                    inputStream = entity.getContent();

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    private class WeatherTask extends AsyncTask<String, Void, Weather_Hub_Model> {

        @Override
        protected Weather_Hub_Model doInBackground(String... params) {

            String data = ((new Weather_Client()).GetWeatherData(params[0]));
            weather.iconData = weather.currentCondition.getIcon();
            weather = JSON_Parser.getWeather(data);
            new DownloadImageAsyncTask().execute(weather.iconData);
            return weather;
        }

        @Override
        protected void onPostExecute(Weather_Hub_Model weather_hub_model) {

            super.onPostExecute(weather_hub_model);

            double KELVIN_TO_FAHRENHEIT = ((weather.currentCondition.getTemp() * 1.8) - 459.67);

            DecimalFormat decimalFormat = new DecimalFormat("#");
            String FormatTemperature = decimalFormat.format(KELVIN_TO_FAHRENHEIT);

            temptv.setText(FormatTemperature);
            citytv.setText(weather.location.getCity());
            conditiontv.setText(weather.currentCondition.getDescription());

        }
    }
}

