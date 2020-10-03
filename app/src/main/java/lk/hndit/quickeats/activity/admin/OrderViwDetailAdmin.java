package lk.hndit.quickeats.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.model.User;
import lk.hndit.quickeats.services.FirebaseDb;

public class OrderViwDetailAdmin extends AppCompatActivity {

    private String orderId;
    private Intent intent;
    private FloatingActionButton fbtnWaiting, fbtnPreparing, fbtnDelivering, fbtnCompleted;
    private TextView txtProductName, txtQuentity, txtPrice, txtTotal, txtDiscount, txtMain, txtOrderID, txtUserName, txtUserContact, txtAddresss, txtDateTime;
    private ImageView imgv_main;
    private DatabaseReference ref;
    private Order order;
    private Button btnViwLocation;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_viw_detail_admin);


        intent = getIntent();
        orderId = intent.getStringExtra("orderId");


        ref = FirebaseDb.databaseReference();
        order = new Order();

        txtMain = findViewById(R.id.txt_order_del_main_admin);
        txtProductName = findViewById(R.id.txt_order_del_productName_admin);
        txtQuentity = findViewById(R.id.txt_order_del_quentity_admin);
        txtPrice = findViewById(R.id.txt_order_del_price_admin);
        txtDiscount = findViewById(R.id.txt_order_del_discount_admin);
        txtTotal = findViewById(R.id.txt_order_del_total_admin);
        txtOrderID = findViewById(R.id.txt_order_del_order_id_admin);
        txtUserName = findViewById(R.id.txt_order_del_user_name_admin);
        txtUserContact = findViewById(R.id.txt_order_del_contact_no_admin);
        txtAddresss = findViewById(R.id.txt_order_del_address_admin);
        txtDateTime = findViewById(R.id.txt_order_del_datetime_admin);
        imgv_main = findViewById(R.id.Imgev_main_admin);

        btnViwLocation = findViewById(R.id.btn_order_del_viwLocation_admin);

        fbtnWaiting = findViewById(R.id.fbtn_order_del_waiting);
        fbtnPreparing = findViewById(R.id.fbtn_order_del_preparing);
        fbtnDelivering = findViewById(R.id.fbtn_order_del_delivering);
        fbtnCompleted = findViewById(R.id.fbtn_order_del_complete);


        if (!orderId.isEmpty()) {
            ref.child("order").child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot != null) {
                        txtProductName.setText("");
                        txtQuentity.setText("");
                        txtPrice.setText("");
                        order = snapshot.getValue(Order.class);
                        txtOrderID.setText(order.getOrderId());
                        txtUserName.setText(order.getOrderId());
                        txtAddresss.setText(order.getAddress());
                        txtUserContact.setText(order.getContactNo());
                        txtDateTime.setText(order.getDateTime());
                        txtDiscount.setText(String.valueOf(order.getDiscount()));
                        txtTotal.setText(String.valueOf(order.getTotalPrice()));

                        latitude= order.getLatitude();
                        longitude = order.getLongitude();

                        List<Cart> cartList = order.getCartList();

                        for (Cart c : cartList) {
                            txtQuentity.append("  " + c.getQuantity() + "\n");
                        }

                        loadProductDetails(cartList);
                        loaduserName(order.getUserId());
                        loadOrderStatusData(order.getStatus());


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        btnViwLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackingOrderAdmin.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);

            }
        });

        fbtnWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = 0;
                ref.child("order").child(txtOrderID.getText().toString()).child("status").setValue(i);
            }
        });

        fbtnPreparing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = 1;
                ref.child("order").child(txtOrderID.getText().toString()).child("status").setValue(i);
            }
        });

        fbtnDelivering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = 2;
                ref.child("order").child(txtOrderID.getText().toString()).child("status").setValue(i);
            }
        });

        fbtnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = 3;
                ref.child("order").child(txtOrderID.getText().toString()).child("status").setValue(i);
            }
        });

    }

    private void loadOrderStatusData(int status) {

        switch (status) {
            case 0:
                txtMain.setText("Order is in Waiting State");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_watch_later_black_24dp));
                fbtnWaiting.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                txtMain.setText("Order is in Preparing State");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
                break;
            case 2:
                txtMain.setText("Order is in Delivering State");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_black_24dp));
                break;
            case 3:
                txtMain.setText("Order is Completed!");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                break;
            default:
                return;

        }
    }

    private void loaduserName(String userId) {

        ref.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                txtUserName.setText(u.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadProductDetails(List<Cart> cartList) {


        for (Cart c : cartList) {

            ref.child("product").child(c.getProductId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Product p = snapshot.getValue(Product.class);
                        txtProductName.append(p.getProductName() + "\n");
                        txtPrice.append(p.getPrice() + "\n");
                    } else {
                        txtProductName.append("N/A" + "\n");
                        txtPrice.append("N/A" + "\n");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
