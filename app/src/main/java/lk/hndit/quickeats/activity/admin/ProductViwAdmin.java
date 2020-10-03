package lk.hndit.quickeats.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.adapters.AdminProductsRecycleViwAdapter;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;

public class ProductViwAdmin extends AppCompatActivity {

    private EditText edtxtSearchProduct;
    private FloatingActionButton fbtnAddprodut;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private AdminProductsRecycleViwAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_admin);

        edtxtSearchProduct = findViewById(R.id.edtxtSearchproduct);
        fbtnAddprodut = findViewById(R.id.factbtnAddnewProduct);
        recyclerView = findViewById(R.id.productActrecycleviw);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList();

        fbtnAddprodut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(ProductViwAdmin.this, AddNewProduct.class));
            }
        });


        FirebaseDb.databaseReference().child("product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                adapter = new AdminProductsRecycleViwAdapter(productList, getApplicationContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}
