package lk.hndit.quickeats.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.MainActivity;
import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.adapters.CategoryRecylerviwAdapter;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.services.FirebaseDb;

public class UserDashboard extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button btnLogout;
    private TextView textView;
    private RecyclerView recyclerView;
    private CategoryRecylerviwAdapter adapter;
    private List<Category> categoryList;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        textView = findViewById(R.id.textView);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerView = findViewById(R.id.categoryrecyclerviwUserDashboard);
        categoryList = new ArrayList();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        int meniitemid = bottomNavigationView.getSelectedItemId();
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(meniitemid);
        badge.setNumber(99);



        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.page_1:
                                Toast.makeText(UserDashboard.this, "page 1", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.page_2:
                                Toast.makeText(UserDashboard.this, "page 2", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                return false;

                        }
                        return true;
                    }
                });




        FirebaseDb.databaseReference().child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for(DataSnapshot postsnapshot : snapshot.getChildren()){
                    Category category = postsnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

                adapter = new CategoryRecylerviwAdapter(categoryList,getApplicationContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        textView.setText(user.getPhoneNumber());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(UserDashboard.this, MainActivity.class));
            }
        });

    }
}
