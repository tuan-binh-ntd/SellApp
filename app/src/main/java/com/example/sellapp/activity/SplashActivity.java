package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.sellapp.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        Thread thread = new Thread() {
          public void run() {
              try {
                  sleep(1500);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              } finally {
                  if(Paper.book().read("user") == null) {
                      Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                      startActivity(intent);
                      finish();
                  } else {
                      Intent intent = new Intent(getApplicationContext(), MainActivity  .class);
                      startActivity(intent);
                      finish();
                  }
              }
          }
        };
        thread.start();
    }
}