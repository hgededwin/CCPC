package com.cacaopaycard.cacaopay.onBoarding;

import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cacaopaycard.cacaopay.Adapters.OnboardingAdapter;
import com.cacaopaycard.cacaopay.R;
import com.cacaopaycard.cacaopay.Registro.RegistroActivity;

import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager pagerOnboarding;
    private CircleIndicator circleIndicator;
    private Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pagerOnboarding = findViewById(R.id.pager_onboarding);
        circleIndicator = findViewById(R.id.indicator_onboarding);
        btnRegistro = findViewById(R.id.btn_registro);

        pagerOnboarding.setAdapter(new OnboardingAdapter(getSupportFragmentManager()));


        circleIndicator.setViewPager(pagerOnboarding);

        if(pagerOnboarding.getCurrentItem() < 2){
            btnRegistro.setText(R.string.str_siguiente);
            btnRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pagerOnboarding.setCurrentItem(pagerOnboarding.getCurrentItem() + 1);
                }
            });
        }

        pagerOnboarding.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(final int i) {
                if (i == 2){
                    btnRegistro.setText(R.string.str_registrate);
                    btnRegistro.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(OnboardingActivity.this, RegistroActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }


            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



    }

}


