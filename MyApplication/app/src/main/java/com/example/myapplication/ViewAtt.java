package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAtt extends AppCompatActivity {

    ArrayList rowdata;
    ListView listView;
    Button bt;
    private AdapterViewAtt adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_att);

        listView = (ListView) findViewById(R.id.listView);
        bt = findViewById(R.id.back);
        rowdata = new ArrayList();

        ProgressDialog progressDialog = new ProgressDialog(this);

        // Set progress dialog properties
        progressDialog.setMessage("Loading Data ...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setDimAmount(0.5f);

        // Show progress dialog before fetching data
        progressDialog.show();

        
        String subject = "CN";

        db = FirebaseFirestore.getInstance();
        CollectionReference att = db.collection("ATTENDANCE").document("SCIS").collection(subject);
        CollectionReference co = db.collection("COURSES");
        Query attQuery = att.whereEqualTo("Course", subject);
        Query coq = co.whereEqualTo("Code", subject);

        coq.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Iterate through the list of courses
                for (QueryDocumentSnapshot courseDoc : task.getResult()) {
                    int ClsDone = Integer.valueOf((String) courseDoc.get("ClsDone"));
                    // DocumentSnapshot courseDoc =  task.getResult();
                    List<String> students = (List<String>) courseDoc.get("Students");

                    // Get the reference to the Attendance collection for the current course

                    // Toast.makeText(ViewAtt.this, "FAILED TO LOAD DATA\nKINDLY TRY AGAIN", Toast.LENGTH_SHORT).show();
                    attQuery.get().addOnCompleteListener(attendanceTask -> {
                        if (attendanceTask.isSuccessful()) {
                            // Count the number of classes and attendance for each student
                            Map<String, Integer> attendanceCount = new HashMap<>();

                            for (QueryDocumentSnapshot attendanceDoc : attendanceTask.getResult()) {
                                String studentName = attendanceDoc.getString("stdId");
                                boolean present = attendanceDoc.getBoolean("isPresent");

                                if (!attendanceCount.containsKey(studentName)) {
                                    attendanceCount.put(studentName, 0);
                                }

                                if (present) {
                                    attendanceCount.put(studentName, attendanceCount.get(studentName) + 1);
                                }
                            }

                            // Calculate the attendance percentage for each student
                            Map<String, Double> attendancePercentages = new HashMap<>();

                            for (String student : students) {
                                int attendance = attendanceCount.getOrDefault(student, 0);
                                double percentage = (double) attendance / ClsDone * 100;

                                attendancePercentages.put(student, percentage);
                                TeacherViewData da = new TeacherViewData(student,String.valueOf(percentage), String.valueOf(ClsDone), String.valueOf(attendance));
                                rowdata.add(da);
                                // Toast.makeText(ViewAtt.this, student, Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();

                            adapter = new AdapterViewAtt(rowdata, getApplicationContext());
                            listView.setAdapter(adapter);

                            // Do something with the attendance percentages for the current course
                            // For example, display them in a list
                            // Or store them in a separate collection for later retrieval
                        } else {
                            Toast.makeText(ViewAtt.this, "FAILED TO LOAD DATA\nKINDLY TRY AGAIN", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(ViewAtt.this, "FAILED TO LOAD DATA\nKINDLY TRY AGAIN", Toast.LENGTH_SHORT).show();
                // Handle the error
            }
        });
    }
}


//        db.collection("COURSES").document("SCIS").collection(subject).get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                String name = (String) d.get("Name");
//                                name = name + (String) d.get("StdId");
//                                String percent = "";
//                                String clsdone = "";
//                                String clsattend = "";
//
//                                TeacherViewData da = new TeacherViewData(name,percent, clsdone, clsattend);
//                                rowdata.add(da);
//                                Toast.makeText(ViewAtt.this, name, Toast.LENGTH_SHORT).show();
//                            }
//
//                            adapter = new AdapterViewAtt(rowdata, getApplicationContext());
//                            listView.setAdapter(adapter);
//
//                            // after that we are passing our array list to our adapter class.
//                            // after passing this array list to our adapter
//                            // class we are setting our adapter to our list view.
//                        } else {
//                            // if the snapshot is empty we are displaying a toast message.
//                            Toast.makeText(ViewAtt.this, "No data found in Database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // we are displaying a toast message
//                        // when we get any error from Firebase.
//                        Toast.makeText(ViewAtt.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
//                    }
//                });

//                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//@Override
//public void onItemClick(AdapterView parent, View view, int position, long id) {
//        RowData row = (RowData) rowdata.get(position);
//        row.checked = !row.checked;
//        adapter.notifyDataSetChanged();
//        }
//        });
