package com.example.smart_ei_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button signupBtn = findViewById(R.id.signupBtn);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);

        signupBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (name.isEmpty()) nameInput.setError("Name is Required");
                if (email.isEmpty()) emailInput.setError("Email is Required");
                if (password.isEmpty()) passwordInput.setError("Password is Required");
                if (confirmPassword.isEmpty()) confirmPasswordInput.setError("Confirm Password is Required");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid Email Format");
                return;
                }
            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters long");
                return;
            }
            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
            finish();
        });

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Login Here", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
