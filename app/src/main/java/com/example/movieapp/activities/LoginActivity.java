//login
package com.example.movieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieapp.R;
import com.example.movieapp.network.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPass;
    TextView textViewToRegister, forgotPass;
    ProgressBar progressBar;
    Button buttonLogin;
    private String email;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Kiểm tra thời gian hết hạn phiên đăng nhập
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            long loginTime = preferences.getLong("login_time", 0);
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - loginTime;


            long sessionExpiration = 3600 * 60 * 1000;
            if (timeElapsed > sessionExpiration) {
                mAuth.signOut();
                Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            // Hiển thị thông báo không có kết nối internet
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
        mAuth = FirebaseAuth.getInstance();


        editTextEmail = findViewById(R.id.editTextText);
        editTextPass = findViewById(R.id.editTextPass);
        textViewToRegister = findViewById(R.id.textView8);
        progressBar = findViewById(R.id.progressBar);
        buttonLogin = findViewById(R.id.button2);
        forgotPass = findViewById(R.id.forgotPass);


        email = getIntent().getStringExtra("Email");
        editTextEmail.setText(!TextUtils.isEmpty(email) ? email : null);

        textViewToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPass.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                    // Lưu thời gian đăng nhập
                                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("login_time", System.currentTimeMillis());
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
