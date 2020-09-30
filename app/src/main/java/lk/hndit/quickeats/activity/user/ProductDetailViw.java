package lk.hndit.quickeats.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class ProductDetailViw extends AppCompatActivity {

    private TextView txtTitle, txtDesc, txtPrice;
    private ImageView ProductImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    private ElegantNumberButton numberButton;
    private Product product;
    private String productId;
    private Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_viw);


product = new Product();
        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btncart);
        txtTitle = findViewById(R.id.fooddel_title);
        txtDesc = findViewById(R.id.product_del_desc);
        txtPrice = findViewById(R.id.product_del_price);
        ProductImage = findViewById(R.id.product_del_imageviw);
        //Todo: create multiple

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleMarginBottom(20);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Cart cart = new Cart(uid,productId,2,"");
                FirebaseDb.databaseReference().child("cart").child(uid).child(productId).setValue(cart);
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        intent = getIntent();
        productId = intent.getStringExtra("productId");

        if(productId != null){

            FirebaseDb.databaseReference().child("product").child(productId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    txtTitle.setText(snapshot.child("productName").getValue(String.class));
                    txtPrice.setText(snapshot.child("price").getValue(Double.class).toString());
                    txtDesc.setText(snapshot.child("productDesc").getValue(String.class));
                    collapsingToolbarLayout.setTitle(snapshot.child("productName").getValue(String.class));
                    Picasso.get().load(snapshot.child("url1").getValue(String.class)).into(ProductImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
