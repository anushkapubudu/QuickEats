package lk.hndit.quickeats.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.adapters.ProductRecyclerViwAdapter;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;

public class ProductViwUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product> list;
    private ProductRecyclerViwAdapter adapter;
    private String categoryId;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_viw_user);

        intent=getIntent();
        categoryId = intent.getStringExtra("categoryId");


        recyclerView = findViewById(R.id.procuctrecyclerviwuser);
        list = new ArrayList();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseDb.databaseReference().child("product").orderByChild("category").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot postsnapshot : snapshot.getChildren()){
                    Product product  = postsnapshot.getValue(Product.class);
                    list.add(product);
                }

                adapter = new ProductRecyclerViwAdapter(list,getApplicationContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
