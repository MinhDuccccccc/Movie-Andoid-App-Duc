//trang đăng kí
package com.example.movieapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieapp.R;
import com.example.movieapp.network.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPass, editTextConfirm;
    TextView textViewToLogin;
    ProgressBar progressBar;
    Button buttonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            // Hiển thị thông báo không có kết nối internet
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }


        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        editTextConfirm = findViewById(R.id.editTextConfirm);
        buttonReg = findViewById(R.id.button2);
        textViewToLogin = findViewById(R.id.textView8);

        textViewToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString();
            String password = editTextPass.getText().toString();
            String confirmPass = editTextConfirm.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(RegisterActivity.this, "Confirm password is required", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!password.equals(confirmPass)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
