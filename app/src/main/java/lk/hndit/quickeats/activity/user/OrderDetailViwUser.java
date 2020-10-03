package lk.hndit.quickeats.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.model.User;
import lk.hndit.quickeats.services.FirebaseDb;

public class OrderDetailViwUser extends AppCompatActivity {

    private String orderId;
    private Intent intent;
    private TextView txtProductName, txtQuentity, txtPrice, txtTotal, txtDiscount, txtMain, txtOrderID, txtUserName, txtUserContact, txtAddresss, txtDateTime;
    private ImageView imgv_main;
    private DatabaseReference ref;
    private Order order;
    private Button btnCnfrmOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_viw_user);

        intent=getIntent();
        orderId = intent.getStringExtra("orderId");


        ref = FirebaseDb.databaseReference();
        order = new Order();

        txtMain = findViewById(R.id.txt_order_del_main);
        txtProductName = findViewById(R.id.txt_order_del_productName);
        txtQuentity = findViewById(R.id.txt_order_del_quentity);
        txtPrice = findViewById(R.id.txt_order_del_price);
        txtDiscount = findViewById(R.id.txt_order_del_discount);
        txtTotal = findViewById(R.id.txt_order_del_total);
        txtOrderID = findViewById(R.id.txt_order_del_order_id);
        txtUserName = findViewById(R.id.txt_order_del_user_name);
        txtUserContact = findViewById(R.id.txt_order_del_contact_no);
        txtAddresss = findViewById(R.id.txt_order_del_address);
        txtDateTime = findViewById(R.id.txt_order_del_datetime);
        imgv_main = findViewById(R.id.Imgev_main);

        btnCnfrmOrder = findViewById(R.id.btn_order_del_cnfirmorder);




        if(!orderId.isEmpty()){
            ref.child("order").child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot!=null){
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

                        List<Cart> cartList = order.getCartList();

                        for(Cart c : cartList){
                            txtQuentity.append("  "+c.getQuantity()+"\n");
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


    btnCnfrmOrder.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(txtOrderID.getText() != ""){
                Integer i = 3;
                ref.child("order").child(txtOrderID.getText().toString()).child("status").setValue(i);
                btnCnfrmOrder.setText("Thank you For Your Order!");
                btnCnfrmOrder.setBackgroundColor(Color.GRAY);
                btnCnfrmOrder.setEnabled(false);
            }
        }
    });

    }

    private void loadOrderStatusData(int status) {

        switch (status){
            case 0:
                txtMain.setText("Your Order is Waiting!");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_watch_later_black_24dp));
                break;
            case 1:
                txtMain.setText("Your Order is Preparing!");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
                break;
            case 2:
                txtMain.setText("Your Order is Delivering!!");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_black_24dp));
                break;
            case 3:
                txtMain.setText("Your Order is Completed!");
                imgv_main.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));

                btnCnfrmOrder.setText("Thank you For Your Order!");
                btnCnfrmOrder.setBackgroundColor(Color.GRAY);
                btnCnfrmOrder.setEnabled(false);
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


        for(Cart c : cartList){

            ref.child("product").child(c.getProductId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Product p = snapshot.getValue(Product.class);
                        txtProductName.append(p.getProductName()+"\n");
                        txtPrice.append(p.getPrice()+"\n");
                    }else {
                        txtProductName.append("N/A"+"\n");
                        txtPrice.append("N/A"+"\n");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
