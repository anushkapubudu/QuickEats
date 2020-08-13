package lk.hndit.quickeats.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.User;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class CreateUser extends AppCompatActivity {

    private EditText edtxtUsername;
    private Button btnCreateAccount;
    private FirebaseUser firebaseUser;
    private User exitingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        edtxtUsername = findViewById(R.id.edtxtUsername);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDb.databaseReference().child("user").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    exitingUser = snapshot.getValue(User.class);
                    Log.d("TAG", "onDataChange: "+exitingUser.toString());
                    edtxtUsername.setText(exitingUser.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtxtUsername.getText().length() == 0){
                    edtxtUsername.setError("Username Can't be Empty!");
                }

                boolean b = false;

                if(exitingUser != null){
                    exitingUser.setName(edtxtUsername.getText().toString());
                    b = FirebaseDb.getInstance().create("user", exitingUser.getId(), exitingUser);
                }else{
                    User user = new User(firebaseUser.getUid(),edtxtUsername.getText().toString(), firebaseUser.getPhoneNumber(),"","","CUSTOMER");
                    b = FirebaseDb.getInstance().create("user", user.getId(), user);
                }
                if(b){
                    startActivity(new Intent(CreateUser.this,UserDashboard.class));
                    finish();
                }
            }
        });
    }
}
