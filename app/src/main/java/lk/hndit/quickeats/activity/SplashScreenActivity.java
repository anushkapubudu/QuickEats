package lk.hndit.quickeats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.AdminDashboard;
import lk.hndit.quickeats.activity.user.UserDashboard;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;

    private com.google.firebase.auth.FirebaseAuth auth;
    private FirebaseUser user;
    private com.google.firebase.auth.FirebaseAuth.AuthStateListener authStateListener;
    private String USERTYPE = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDb.databaseReference().child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.exists()) {
                        USERTYPE = snapshot.child("userType").getValue(String.class);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (USERTYPE != null) {
                    switch (USERTYPE) {

                        case "CUSTOMER":
                            startActivity(new Intent(SplashScreenActivity.this, UserDashboard.class));
                            finish();
                            break;
                        case "ADMIN":
                            startActivity(new Intent(SplashScreenActivity.this, AdminDashboard.class));
                            finish();
                            break;
                        default:
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                            break;
                    }
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }


                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}


