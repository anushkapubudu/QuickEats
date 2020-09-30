package lk.hndit.admin_quickeats.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.admin_quickeats.R;
import lk.hndit.admin_quickeats.activitys.adapters.AdminCategoryRecycleviwAdapter;
import lk.hndit.admin_quickeats.model.Category;
import lk.hndit.admin_quickeats.services.FirebaseDb;

public class CategoryInAdmin extends AppCompatActivity {

    private EditText edtxtSearchCategory;
    private FloatingActionButton fbtnAddCtgry;
    private RecyclerView recyclerView;
    private List<Category> categoryList;
    private AdminCategoryRecycleviwAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_in_admin);

        edtxtSearchCategory = findViewById(R.id.edtxtSearchCategory);
        fbtnAddCtgry = findViewById(R.id.factbtnAddnewCategory);
        recyclerView = findViewById(R.id.categoryActrecycleviw);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList();

        fbtnAddCtgry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryInAdmin.this, AddCategory.class));
            }
        });

        FirebaseDb.databaseReference().child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                adapter = new AdminCategoryRecycleviwAdapter(categoryList, getApplicationContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
