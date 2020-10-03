package lk.hndit.quickeats.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.adapters.AdminCategoryRecycleviwAdapter;
import lk.hndit.quickeats.activity.user.adapters.CategoryRecylerviwAdapter;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class UserDashboard extends AppCompatActivity {


    private FirebaseUser user;
    private Button btnLogout;
    private TextView textView;
    private RecyclerView recyclerView;
    private CategoryRecylerviwAdapter adapter;
    private List<Category> categoryList;
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnSearch,btnMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bottomNavigationView = findViewById(R.id.user_bottom_navigation);
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.categoryrecyclerviwUserDashboard);
        categoryList = new ArrayList();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        btnSearch = findViewById(R.id.btnSearch);
        btnMenu = findViewById(R.id.btnMenu);


        int meniitemid = bottomNavigationView.getSelectedItemId();

        bottomNavigationView.setSelectedItemId(R.id.page_1);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(1);
        badge.setNumber(99);


//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(UserDashboard.this,SearchProduct.class));
//            }
//        });



        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.page_1:
                                break;

                            case R.id.page_2:
                                startActivity(new Intent(UserDashboard.this, CartViw.class));
                                finish();
                                break;

                            case R.id.page_3:
                                startActivity(new Intent(UserDashboard.this, OrderViwUser.class));
                                finish();
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


        //textView.setText(user.getPhoneNumber());

//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOutCurrentUser();
//                startActivity(new Intent(UserDashboard.this, MainActivity.class));
//
//
//
//            }
//        });

    }
}
