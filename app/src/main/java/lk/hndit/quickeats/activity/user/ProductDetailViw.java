package lk.hndit.quickeats.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;
import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class ProductDetailViw extends AppCompatActivity {

    private TextView txtTitle, txtDesc, txtPrice, txtRealPrice, txtDiscount, txtRemaining;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    private ElegantNumberButton numberButton;
    private Product product;
    private String productId;
    private Intent intent;
    private Slider slider;
    private List<Slide> slideList;


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
        txtRealPrice = findViewById(R.id.product_del_correct_price);
        txtDiscount = findViewById(R.id.product_del_discount);
        txtRemaining = findViewById(R.id.product_del_remainning);
        slider = findViewById(R.id.fooddel_image_slider);
        slideList = new ArrayList<>();


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleMarginBottom(20);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Cart cart = new Cart(uid, productId, Integer.parseInt(numberButton.getNumber()), "");
                if(!cart.getuId().isEmpty()){
                    FirebaseDb.databaseReference().child("cart").child(uid).child(productId).setValue(cart);
                    Toast.makeText(ProductDetailViw.this, "Product Added to Your Cart!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProductDetailViw.this, "Something went Wrong!  Please Try Again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        intent = getIntent();
        productId = intent.getStringExtra("productId");

        if (productId != null) {

            FirebaseDb.databaseReference().child("product").child(productId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    slideList.clear();

                    txtTitle.setText(snapshot.child("productName").getValue(String.class));
                    txtPrice.setText(snapshot.child("price").getValue(Double.class).toString());
                    txtDesc.setText(snapshot.child("productDesc").getValue(String.class));
                    txtDiscount.setText(snapshot.child("discount").getValue(Integer.class).toString());
                    txtRealPrice.setText(getRealPrice(Double.parseDouble(txtPrice.getText().toString()), Double.parseDouble((String) txtDiscount.getText())));

                    if(snapshot.child("remaining").getValue(Integer.class) == 0){
                        txtRemaining.setText("This item Not Available for Now!");
                        txtRemaining.setTextColor(Color.RED);
                    }else{
                        txtRemaining.setText(snapshot.child("remaining").getValue(Integer.class).toString() +" Items Available");
                    }

                    collapsingToolbarLayout.setTitle(snapshot.child("productName").getValue(String.class));

                    slideList.add(new Slide(0, snapshot.child("url1").getValue(String.class), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                    slideList.add(new Slide(1, snapshot.child("url2").getValue(String.class), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                    slideList.add(new Slide(2, snapshot.child("url3").getValue(String.class), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
                    slider.addSlides(slideList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private String getRealPrice(double price, double discount) {

        Double rp = price - price * discount/100;

        return rp.toString();
    }
}
