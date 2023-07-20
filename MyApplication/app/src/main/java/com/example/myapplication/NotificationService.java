package com.example.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService extends Service {

    private static final String TAG = "ScheduleNotificationService";
    private static final int NOTIFICATION_ID = 1;
    private static final long NOTIFICATION_MINUTES_BEFORE_CLASS = 30;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference coursesRef;
    private CollectionReference facultyRef;
    private List<ScheduleItem> scheduleItemList;
    private List<String> courseCodesList;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        coursesRef = db.collection("COURSES");
        facultyRef = db.collection("FACULTY");
        scheduleItemList = new ArrayList<>();
        courseCodesList = new ArrayList<>();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupScheduleQuery();
        scheduleNotifications();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            default:
                return "";
        }
    }

    private void setupScheduleQuery() {
        String dayOfWeek = getDayOfWeek(1);
        String currentUserId = mAuth.getCurrentUser().getUid();
        scheduleItemList.clear();
        courseCodesList.clear();
        db.collection("TIME TABLE").document("SCIS").collection(dayOfWeek).whereEqualTo("STUDENTS." + currentUserId, true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed: " + error);
                        return;
                    }

                    for (DocumentSnapshot doc : value) {
                        ScheduleItem scheduleItem = doc.toObject(ScheduleItem.class);
                        scheduleItemList.add(scheduleItem);
                        courseCodesList.add(scheduleItem.getCode());
                    }
                });
    }

    private void scheduleNotifications() {
        coursesRef.whereIn("Code", courseCodesList).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Course course = doc.toObject(Course.class);
                        scheduleItemList.stream().filter(scheduleItem -> scheduleItem.getCode().equals(course.getCode()))
                                .findFirst().ifPresent(scheduleItem -> {
                                    scheduleNotification(course, scheduleItem);
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting courses for notifications", e));
    }

    private void scheduleNotification(Course course, ScheduleItem scheduleItem) {
        String title = String.format("%s - %s", course.getCode(), course.getName());
        String content = String.format("Class with %s at %s, %s", scheduleItem.getFaculty(), scheduleItem.getLocation(), scheduleItem.getRoomNo());
        long notificationTime = getNotificationTime(scheduleItem.getStart());

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, course.getId());
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, title);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_CONTENT, content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            }
        }
    }

    private long getNotificationTime(LocalDateTime classStartTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime notificationTime = classStartTime.minusMinutes(NOTIFICATION_MINUTES_BEFORE_CLASS);
            return notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }

    public static void cancelNotification(Context context, int notificationId) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

