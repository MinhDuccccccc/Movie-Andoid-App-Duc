package com.example.movieapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.movieapp.R;
import com.example.movieapp.activities.NotificationOpen;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Tạo một Intent để mở activity khi người dùng nhấn vào thông báo
        Intent repeatingIntent = new Intent(context, NotificationOpen.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Tạo một PendingIntent từ Intent vừa tạo
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                repeatingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        // Tạo thông báo
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.intro_pic);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        // Xây dựng thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.btn_4)
                .setLargeIcon(bitmap)
                .setContentTitle("Chào buổi sáng ngày "+day+"/"+month)
                .setContentText("Chúc Cường một ngày mới tốt lành !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); 

        // Lấy NotificationManager để quản lý việc gửi thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Gửi thông báo với ID bừa
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(200, builder.build());
    }
}
