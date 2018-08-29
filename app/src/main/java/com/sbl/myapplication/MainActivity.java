package com.sbl.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CountDownView countDownView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countDownView = findViewById(R.id.cd);
        countDownView.setCountdownTime(5);
        countDownView.setAddCountDownListener(new CountDownView.CountDownFinishListener() {
            @Override
            public void countDownFinished() {
                Toast.makeText(MainActivity.this,"终于来了",Toast.LENGTH_LONG).show();
            }
        });
        countDownView.startCountDown(5000);
    }
}
