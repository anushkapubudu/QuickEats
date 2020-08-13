package lk.hndit.quickeats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import lk.hndit.quickeats.MainActivity;
import lk.hndit.quickeats.R;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

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

                    Log.d("TAG", "+++++++===========================: " + snapshot.toString());

                    if (snapshot.exists()) {
                        USERTYPE = snapshot.child("userType").getValue(String.class);
                        Log.d("TAG", "+++++++===========================: " + snapshot.toString());


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


//        Log.d("TAG", "================================== : " + user.getPhoneNumber());


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
//                            startActivity(i);


                if (USERTYPE != null) {
                    switch (USERTYPE) {

                        case "CUSTOMER":
                            startActivity(new Intent(SplashScreenActivity.this, UserDashboard.class));
                            finish();
                            break;
                        case "ADMIN":
                            startActivity(new Intent(SplashScreenActivity.this, AdminTest.class));
                            finish();
                            break;
                        default:
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                            break;
                    }
                }else {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}


