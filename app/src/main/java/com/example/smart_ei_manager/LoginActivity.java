package com.example.smart_ei_manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;

    private static final String PREFS_NAME = "theme_prefs";
    private static final String PREF_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadTheme();
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignupRedirect = findViewById(R.id.tvBottomSignup);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch themeSwitch = findViewById(R.id.themeSwitch);

        themeSwitch.setChecked(isDarkMode());

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

            startActivity(new Intent(this, DashboardActivity.class));
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
        });

        tvSignupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            Toast.makeText(this, "Signup Here", Toast.LENGTH_SHORT).show();
            finish();
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked);
            recreate();
        });
    }

    private void setDarkMode(boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_MODE, enabled);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private boolean isDarkMode() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(PREF_DARK_MODE, false);
    }

    private void loadTheme() {
        if (isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
