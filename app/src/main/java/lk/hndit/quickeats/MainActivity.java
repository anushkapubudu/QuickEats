package lk.hndit.quickeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import lk.hndit.quickeats.activity.AdminTest;
import lk.hndit.quickeats.activity.UserDashboard;
import lk.hndit.quickeats.activity.VerifyPhone;
import lk.hndit.quickeats.model.User;
import lk.hndit.quickeats.services.FirebaseDb;

public class MainActivity extends AppCompatActivity {


    private EditText edtxtEmail;
    private Button btnLogin;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String USERTYPE = null;





    @Override
    protected void onStart() {
        super.onStart();
        user = lk.hndit.quickeats.services.FirebaseAuth.getInstance().getCurrentUser();


        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtxtEmail = findViewById(R.id.edtxtMobileNo);
        btnLogin = findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {



                if (user != null){
//
//                    startActivity(new Intent(MainActivity.this,UserDashboard.class));
//                    finish();

                    Log.d("TAG", "user : "+user.getDisplayName());


                    FirebaseDb.databaseReference().child("user").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Log.d("TAG", "+++++++===========================: "+snapshot.toString());

                            if(snapshot.exists()){
                                USERTYPE = snapshot.child("userType").getValue(String.class);
                                Log.d("TAG", "+++++++===========================: "+snapshot.toString());

                                switch (USERTYPE){

                                    case "CUSTOMER":
                                        startActivity(new Intent(MainActivity.this,UserDashboard.class));
                                        finish();
                                        break;
                                    case "ADMIN":
                                        startActivity(new Intent(MainActivity.this, AdminTest.class));
                                        finish();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(USERTYPE != null){

                    }

                }

            }
        };



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edtxtEmail.getText().toString().trim();

                if(number.isEmpty() || number.length() < 9){
                    edtxtEmail.setError("Enter valid mobile No.");
                    edtxtEmail.requestFocus();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, VerifyPhone.class);
                intent.putExtra("number",number);
                startActivity(intent);

            }
        });

    }
}
