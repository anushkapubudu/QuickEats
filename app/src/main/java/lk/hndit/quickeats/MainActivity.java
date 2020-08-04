package lk.hndit.quickeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import lk.hndit.quickeats.activity.UserDashboard;
import lk.hndit.quickeats.activity.VerifyPhone;

public class MainActivity extends AppCompatActivity {


    private EditText edtxtEmail;
    // private EditText edtxtpassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;


    /*@Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            startActivity(new Intent(MainActivity.this,UserDashboard.class));
        }
    }*/



    @Override
    protected void onStart() {
        super.onStart();
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

        edtxtEmail = findViewById(R.id.edtxtEmail);
        // edtxtpassword = findViewById(R.id.edtxtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = auth.getCurrentUser();

                if (user != null){
                    startActivity(new Intent(MainActivity.this,UserDashboard.class));
                    finish();
                    Toast.makeText(getApplicationContext(),"already Signed in !!!!!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Log in to Continue !",Toast.LENGTH_LONG).show();
                }

            }
        };



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edtxtEmail.getText().toString().trim();

                if(number.isEmpty() || number.length() < 9){
                    edtxtEmail.setError("Enter a valid mobile");
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
