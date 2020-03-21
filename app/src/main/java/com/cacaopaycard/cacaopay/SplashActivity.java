package com.cacaopaycard.cacaopay;

import android.app.Application;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.cacaopaycard.cacaopay.Modelos.Usuario;
import com.cacaopaycard.cacaopay.onBoarding.OnboardingActivity;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //new Instabug.Builder((Application) getApplicationContext(),"73732d5d6325e431cbb484b936ce470c",InstabugInvocationEvent.FLOATING_BUTTON).build();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Usuario usuario = new Usuario(SplashActivity.this);

                Boolean isEnrolled = usuario.estaRegistrado();

                if (isEnrolled){
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 3000);
    }
}
