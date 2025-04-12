package com.example.smart_ei_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignupRedirect = findViewById(R.id.tvBottomSignup);

            btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) etEmail.setError("Email is Required");
                if (password.isEmpty()) etPassword.setError("Password is Required");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid Email Format");
                return;
            }

            if (password.length() < 8) {
                etPassword.setError("Password must be at least 8 characters long");
                return;
            }

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
        });

        tvSignupRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Signup Here", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

}
