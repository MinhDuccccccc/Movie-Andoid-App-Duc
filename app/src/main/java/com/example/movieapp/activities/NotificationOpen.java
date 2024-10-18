package com.example.movieapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;

import java.util.Calendar;

public class NotificationOpen extends AppCompatActivity {
    private TextView txt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_open);
        txt = findViewById(R.id.textViewOpen);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        txt.setText("Chào mừng ngày mới " +day+"/"+month+", chúc Cường có một ngày làm việc hiệu quả ❤\uFE0F❤\uFE0F❤\uFE0F");
    }
}