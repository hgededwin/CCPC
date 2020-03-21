package com.cacaopaycard.cacaopay;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.cacaopaycard.cacaopay.AdminCuenta.AyudaFragment;
import com.cacaopaycard.cacaopay.AdminCuenta.PerfilFragment;
import com.cacaopaycard.cacaopay.AdminCuenta.TarjetasFragment;
import com.cacaopaycard.cacaopay.Modelos.Singleton;
import com.cacaopaycard.cacaopay.mvp.cardInfo.ConsultasFragment;

public class MainContainer extends AppCompatActivity {

    private Singleton singleton;
    private RequestQueue requestQueue;
    private TextView txtTitle;
    private Fragment ayudaFragment = new AyudaFragment(), perfilfragment = new PerfilFragment();
    private Fragment misTarjetas = new TarjetasFragment();
    private Fragment consultas;
    private Toolbar toolbar;
    private BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            txtTitle.setText(item.getTitle());
            switch (item.getItemId()) {
                case R.id.nav_perfil_bottom:
                    //getSupportActionBar().setElevation(10);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_menu,perfilfragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();

                    return true;
                case R.id.nav_tarjetas_bottom:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_menu,misTarjetas)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                    return true;
                case R.id.nav_transacciones_bottom:

                    // callback que observa cuando el reciclerView de movimientos realiza un scroll up

                    consultas = ConsultasFragment.newInstance(new ConsultasFragment.OnScrollBottomListener() {
                        @Override
                        public void onViewScrolled(int dx, int dy) {
                            if(dy > 0) {
                                navView.animate().translationY(navView.getHeight());

                            } else
                                navView.animate().translationY(0);

                        }
                    });





                    //consultas = new ConsultasFragment();

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_menu, consultas)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();

                    return true;

                case R.id.nav_ayuda_bottom:

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_menu,ayudaFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                    return true;

                case R.id.nav_cerrar_bottom:
                    Intent intent = new Intent(MainContainer.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                default:

                    return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
        navView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbar_menu_bottom);
        setSupportActionBar(toolbar);
        txtTitle = findViewById(R.id.txt_title_menu);

        singleton = Singleton.getInstance(this);
        requestQueue = singleton.getmRequestQueue();
        //mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_transacciones_bottom);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
