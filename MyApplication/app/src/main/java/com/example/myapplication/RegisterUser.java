package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    Button Submit;
    EditText RegNo, Name, MobNo;
    Spinner Course, year;
    String course, yearJoin;
    private boolean CheckAllFields() {

        Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                course = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RegisterUser.this, course, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                yearJoin = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RegisterUser.this, course, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (RegNo.length() == 0) {
            RegNo.setError("This field is required");
            return false;
        }

        if (Name.length() == 0) {
            Name.setError("Name is required");
            return false;
        }

        if (MobNo.length() == 0) {
            MobNo.setError("Mobile Number is required");
            return false;
        } else if (MobNo.length() < 10 || MobNo.length() > 10) {
            MobNo.setError("Mobile Number Must be of 10 Digits");
            return false;
        }

        // after all validation return true.
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        String years[] = getResources().getStringArray(R.array.YEAR);
        String courses[] = getResources().getStringArray(R.array.COURSES);

        Course = findViewById(R.id.Course);
        RegNo = findViewById(R.id.RegNo);
        Name = findViewById(R.id.Name);
        MobNo = findViewById(R.id.MobNo);
        Submit = findViewById(R.id.submit);
        year = findViewById(R.id.Year);

        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, courses);
        Course.setAdapter(adapter1);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);
        year.setAdapter(adapter);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckAllFields()){
                    uploadData();
                }
            }
        });
    }

    public void uploadData(){

        String reg = RegNo.getText().toString();
        String name = Name.getText().toString();
        String mob = MobNo.getText().toString();

        RegData data = new RegData(course, reg, name, mob);

        SharedPreferences shrdpref = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdpref.edit();

        editor.putString("ID", reg);
        editor.putString("COURSE", course);
        editor.putString("NAME", name);
        editor.putString("MOBILE", mob);
        editor.putString("YEAR", yearJoin);
        editor.putBoolean("isRegistered", true);
        editor.putBoolean("isStudent", true);
        editor.apply();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("STUDENT DATA").document(course).collection(yearJoin).document(reg)
                .set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( RegisterUser.this, "Saved", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterUser.this,StudentHome.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterUser.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}