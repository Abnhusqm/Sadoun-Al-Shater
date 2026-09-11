package com.sadoun.guardian;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

public class GuardianService extends Service implements SensorEventListener {
    public static final String ACTION_STOP = "com.sadoun.guardian.STOP";
    public static final String ACTION_STATE = "com.sadoun.guardian.STATE";
    private static final String CHANNEL = "guardian_channel";
    private SensorManager sensors;
    private float last = 0f;
    private long lastBurst = 0L;
    private int burstCount = 0;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(1001, notification("الحارس نشط — المراقبة المحلية تعمل"));
        sensors = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor s = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (s != null) sensors.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) stopSelf();
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (sensors != null) sensors.unregisterListener(this);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public void onSensorChanged(SensorEvent e) {
        float x=e.values[0], y=e.values[1], z=e.values[2];
        float magnitude=(float)Math.sqrt(x*x+y*y+z*z);
        float delta=Math.abs(magnitude-last);
        last=magnitude;
        long now=SystemClock.elapsedRealtime();
        if (delta > 4.5f) {
            if (now-lastBurst > 15000) burstCount=0;
            burstCount++;
            lastBurst=now;
            if (burstCount >= 3) {
                setState("ATTENTION", "نشاط حركي غير معتاد — تحقق من الوضع");
                burstCount=0;
            } else {
                setState("NORMAL", "المراقبة المحلية نشطة");
            }
        }
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void setState(String state, String text) {
        getSharedPreferences("guardian", MODE_PRIVATE).edit().putString("state",state).putString("state_text",text).apply();
        sendBroadcast(new Intent(ACTION_STATE).putExtra("state",state).putExtra("text",text));
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1001, notification("الحارس: "+state+" — "+text));
    }

    private Notification notification(String text) {
        Notification.Builder b=Build.VERSION.SDK_INT>=26 ? new Notification.Builder(this,CHANNEL) : new Notification.Builder(this);
        return b.setContentTitle("سعدون الشاطر — الحارس").setContentText(text).setSmallIcon(android.R.drawable.ic_lock_idle_lock).setOngoing(true).build();
    }
    private void createChannel() {
        if(Build.VERSION.SDK_INT>=26) {
            NotificationManager nm=getSystemService(NotificationManager.class);
            nm.createNotificationChannel(new NotificationChannel(CHANNEL,"حارس سعدون",NotificationManager.IMPORTANCE_LOW));
        }
    }
}
