package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.logging.LoggingMXBean;

public class AuthActivity extends AppCompatActivity implements LoginFragment.loginScreenInterface, SignUpFragment.SignUpFragmentInterface {

    final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Log.d(TAG, "onCreate: ");
        getSupportFragmentManager().beginTransaction().replace(R.id.AuthActivityCV, new LoginFragment()).commit();

    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startSignUpFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.AuthActivityCV, new SignUpFragment()).commit();
    }

    @Override
    public void goBackToLoginFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.AuthActivityCV,new LoginFragment()).commit();
    }

    @Override
    public void callMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}