package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class LocationService extends Service {

    static final int LOCATION_SERVICE_ID = 175;
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    // static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latit = locationResult.getLastLocation().getLatitude();
                double longit = locationResult.getLastLocation().getLongitude();
                String s = "LAT : " + Double.toString(latit) + "   LONG : " + Double.toString(longit);
                // Update Location to DB in the Document Created with Student REG NO
                Toast.makeText(LocationService.this, s, Toast.LENGTH_SHORT).show();
                updateLatLongData(latit, longit);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Need to be Implemented");
    }

    public void updateLatLongData(double lat, double lon){

        SharedPreferences getShrd = getSharedPreferences("UserData", MODE_PRIVATE);

        String course = getShrd.getString("COURSE", "IMTECH");
        String year = getShrd.getString("YEAR", "2020 ");;
        String Roll = getShrd.getString("ID", "20MCME29");;

        // Using FireStore

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("STUDENT DATA").document(course).collection(year).document(Roll).update("LAT", lat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( LocationService.this, "Saved", Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(Loca.this,StudentHome.class);
//                            startActivity(i);
//                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LocationService.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("STUDENT DATA").document(course).collection(year).document(Roll).update("LON", lon)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( LocationService.this, "Saved", Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(Loca.this,StudentHome.class);
//                            startActivity(i);
//                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LocationService.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void startlocationservice() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000*10)
                .setMaxUpdateDelayMillis(1000*20)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();
            if(action.equals(ACTION_START_LOCATION_SERVICE)) {
                startlocationservice();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
