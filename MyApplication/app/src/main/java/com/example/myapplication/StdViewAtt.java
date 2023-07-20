package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class StdViewAtt extends AppCompatActivity {

    ArrayList rowdata;
    ListView listView;
    Button bt;
    private AdapterStdViewAtt adapter;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_view_att);

        SharedPreferences getShrd = getSharedPreferences("UserData", MODE_PRIVATE);
        String stdid = getShrd.getString("ID", "20MCME29");

        listView = (ListView) findViewById(R.id.listView);
        rowdata = new ArrayList();

        progressDialog = new ProgressDialog(this);

        // Set progress dialog properties
        progressDialog.setMessage("Loading Data ...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setDimAmount(0.5f);

        // Show progress dialog before fetching data
        progressDialog.show();


        db = FirebaseFirestore.getInstance();

        db.collection("COURSES")
                .whereArrayContains("Students", stdid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot courseDoc : querySnapshot) {
                        String courseId = (String) courseDoc.get("Code");
                        String courseName = (String) courseDoc.get("Name");
                        int totalClasses = Integer.valueOf((String) courseDoc.get("ClsDone"));

                        Query attquery = db.collection("ATTENDANCE").document("SCIS").collection(courseId).whereEqualTo("stdId", stdid);
                        // Retrieve the attendance documents for the course and student.

                        attquery.get()
                                .addOnCompleteListener(attendanceSnapshot -> {
                                    if(attendanceSnapshot.isSuccessful()) {
                                        int count = 0;
                                        for (QueryDocumentSnapshot doc : attendanceSnapshot.getResult()) {
                                            boolean present = doc.getBoolean("isPresent");
                                            if (present) {
                                                count = count + 1;
                                            }
                                        }
                                        int attendedClasses = count;
                                        double Percentage = (attendedClasses / (double) totalClasses) * 100;

                                        // Display the course and attendance percentage.
                                        // System.out.println(courseDoc.get("name") + ": " + attendancePercentage + "%");

                                        StdViewAttData da = new StdViewAttData(courseName, String.valueOf(Percentage), String.valueOf(totalClasses), String.valueOf(attendedClasses));
                                        rowdata.add(da);

                                        Toast.makeText(StdViewAtt.this, courseName, Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();

                                        adapter = new AdapterStdViewAtt(rowdata, getApplicationContext());
                                        listView.setAdapter(adapter);
                                    }
                                    else {
                                        Toast.makeText(StdViewAtt.this, "COULD NOT LOAD DATA", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                });
    }
}