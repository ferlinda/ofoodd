package com.example.ofood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

@SuppressLint("Registered")
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private EditText mEmail, mPassword;
    private TextView forgotLink, mButtonLogin, mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBarOnly(LoginActivity.this);

        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.editEmail);
        mPassword   = findViewById(R.id.editPassword);
        forgotLink  = findViewById(R.id.forgotPassword);

        mButtonLogin = findViewById(R.id.buttonLogin);
        mButtonRegister = findViewById(R.id.buttonRegister);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    mButtonLogin.setEnabled(true);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    mButtonLogin.setEnabled(true);
                    return;
                }

                if(password.length() < 8){
                    mPassword.setError("Password must be >= 8 characters");
                    mButtonLogin.setEnabled(true);
                    return;
                }

                else{
                    mButtonLogin.setEnabled(false);
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            boolean emailVerified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
                            if(emailVerified){
                                Toast.makeText(LoginActivity.this, "Log in Successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                IntroActivity.introActivity.finish();
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Verify your Email.", Toast.LENGTH_SHORT).show();
                                mButtonLogin.setEnabled(true);
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }

                        }else{
                            Toast.makeText(LoginActivity.this, "Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                });
            }
        });

        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                final View customLayout = getLayoutInflater().inflate(R.layout.forgot_layout, null);
                passwordResetDialog.setTitle("Reset password?");
                passwordResetDialog.setMessage("Enter your Email address: ");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Extract email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset link has been send to your Email.",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Failed to send reset link."+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Back to login view
                    }
                });
                passwordResetDialog.create().show();
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
