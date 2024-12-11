package com.example.yesface.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yesface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button signIn;
    EditText email, password;
    TextView signUp;
    ImageView togglePasswordVisibility;
    CheckBox adminCheckbox; // New Checkbox for Admin Login
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        togglePasswordVisibility = findViewById(R.id.password_visibility_toggle);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        signIn = findViewById(R.id.login_button);
        password = findViewById(R.id.login_password);
        email = findViewById(R.id.login_email);
        signUp = findViewById(R.id.signupRedirectText);
        adminCheckbox = findViewById(R.id.admin_checkbox); // Initialize the Checkbox

        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = password.getSelectionStart();
                int selectionEnd = password.getSelectionEnd();

                if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
                }

                password.setSelection(selectionStart, selectionEnd);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loginUser() {
        String userPass = password.getText().toString();
        String userEmail = email.getText().toString();

        if (TextUtils.isEmpty(userPass)) {
            Toast.makeText(this, "Пароль пустой", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Почта пустая", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPass.length() < 6) {
            Toast.makeText(this, "Пароль не должен быть меньше 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adminCheckbox.isChecked()) {
            // Handle admin login
            handleAdminLogin(userEmail, userPass);
        } else {
            // Handle regular user login
            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Ошибка входа: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void handleAdminLogin(String email, String password) {
        if (email.equals("admin@yesface.com") && password.equals("yesface123")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Вход администратора успешен", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, AdminPanelActivity.class)); // Open Admin Panel
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Ошибка: Неверные данные администратора", Toast.LENGTH_SHORT).show();
        }
    }
}
