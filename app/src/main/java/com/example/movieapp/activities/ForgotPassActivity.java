package com.example.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieapp.R;
import com.example.movieapp.network.NetworkUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private Button sendEmailBtn;
    private EditText editTextEmail;
    private ProgressBar progressBarReset;
    private FirebaseAuth mAuth;
    String email;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
        //init
        sendEmailBtn = findViewById(R.id.sendEmailBtn);
        editTextEmail = findViewById(R.id.editTextEmail);
        progressBarReset = findViewById(R.id.progressBarReset);
        mAuth = FirebaseAuth.getInstance();
        //reset btn
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    ResetPass();
                }else{
                    editTextEmail.setError("Email field can't be empty !");
                }
            }
        });

    }


    private void ResetPass() {
        progressBarReset.setVisibility(View.VISIBLE);
        sendEmailBtn.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassActivity.this, "Reset passWord link has been sent to your registered email ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
                intent.putExtra("Email", email);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBarReset.setVisibility(View.VISIBLE);
                        sendEmailBtn.setVisibility(View.VISIBLE);
                    }
                });
    }
}