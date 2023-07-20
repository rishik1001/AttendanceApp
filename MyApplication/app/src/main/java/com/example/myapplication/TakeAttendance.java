package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TakeAttendance extends AppCompatActivity {

    ArrayList rowdata;
    ListView listView;
    private CustomAdapter adapter;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        listView = (ListView) findViewById(R.id.listView);
        rowdata = new ArrayList();

        progressDialog = new ProgressDialog(this);

        // Set progress dialog properties
        progressDialog.setMessage("Loading Details ...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setDimAmount(0.5f);

        // Show progress dialog before fetching data
        progressDialog.show();

        String course = "IMTECH";
        String year = "2020";

        double centerlat = 0.0;
        double centerlon = 0.0;

        db = FirebaseFirestore.getInstance();

        db.collection("STUDENT DATA").document(course).collection(year).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                String name = (String) d.get("name");
                                RowData da = new RowData(name, false);
                                rowdata.add(da);
                                Toast.makeText(TakeAttendance.this, name, Toast.LENGTH_SHORT).show();

//                                double la = (double) d.get("LAT");
//                                double lo = (double) d.get("LON");

//                                boolean var = isInside(la, lo, centerlat, centerlon, 50);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                            }

                            progressDialog.dismiss();

                            adapter = new CustomAdapter(rowdata, getApplicationContext());
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView parent, View view, int position, long id) {
                                    RowData row = (RowData) rowdata.get(position);
                                    row.checked = !row.checked;
                                    adapter.notifyDataSetChanged();
                                }
                            });

                            // after that we are passing our array list to our adapter class.
                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(TakeAttendance.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(TakeAttendance.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isInside(double lat1, double lon1, double lat2, double lon2, double distance)
    {
        float[] results = new float[1];
        Location.distanceBetween(lat1 , lon1, lat2, lon2, results);
        float distanceInMeters = results[0];
        return distanceInMeters < distance;
    }
}