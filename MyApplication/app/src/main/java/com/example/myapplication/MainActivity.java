package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView country,city,address,longitude,latitude;
    Button getLocation;
    private  final  static int REQUEST_CODE=100;
    private  final  static int REQUEST_CODE1=99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        country=findViewById(R.id.country);
        city=findViewById(R.id.city);
        address=findViewById(R.id.address);
        longitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.latitude);
        getLocation= findViewById(R.id.get_location_btn);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        // getLocation.setOnClickListener(view -> getLastLocation());

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });
    }

    private boolean islocationservicerunning(){
        ActivityManager activityManager =  (ActivityManager)  getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
        {
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE))
            {
                if(LocationService.class.getName().equals((service.service.getClassName()))){
                    if(service.foreground)
                    {
                        return true;
                    }
                }
            }
            return  false;
        }
        return false;
    }
    private boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean b = (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) ;
        return b;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE1);
    }

    private void getLastLocation() {

        if (!checkPermission()) {
            requestPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                try {
                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    latitude.setText("Latitude : " + addresses.get(0).getLatitude());
                                    longitude.setText("Longitude : "+addresses.get(0).getLongitude());
                                    address.setText("Address : "+addresses.get(0).getAddressLine(0));
                                    city.setText("City : "+addresses.get(0).getLocality());
                                    country.setText("Country : "+addresses.get(0).getCountryName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            if(!islocationservicerunning())
            {
                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                String ACTION_START_LOCATION_SERVICE = "startLocationService";
                intent.setAction(ACTION_START_LOCATION_SERVICE);
                startService(intent);
                Toast.makeText(this, "LOCATION SERVICE RUNNING", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            requestPermission();
        }
    }

//    private void askPermission() {
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]
//                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]
//                {Manifest.permission.ACCESS_BACKGROUND_LOCATION},REQUEST_CODE1);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE)
        {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else
            {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}