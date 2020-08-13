package lk.hndit.quickeats.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.hndit.quickeats.MainActivity;
import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.adapters.CartRecyclerViwAdapter;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.util.GpsTracker;

public class CartViw extends AppCompatActivity {

    private TextView txtCartTotal;
    private Button btnPlaceOrder;
    private RecyclerView recyclerView;
    private CartRecyclerViwAdapter adapter;
    private List<Cart> cartList;
    private List<Product> productList;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        txtCartTotal = findViewById(R.id.cartTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        recyclerView = findViewById(R.id.cartrecyclerviw);
        cartList = new ArrayList<>();
        productList = new ArrayList<>();
        bottomNavigationView = findViewById(R.id.bottom_navigation_cart);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView.setSelectedItemId(R.id.page_2);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.page_1:
                                startActivity(new Intent(CartViw.this, UserDashboard.class));
                                finish();
                                break;

                            case R.id.page_2:
                                break;

                            case R.id.page_3:
                                startActivity(new Intent(CartViw.this, OrderViwUser.class));
                                finish();
                                break;

                            default:
                                return false;

                        }
                        return true;
                    }
                });



        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDb.databaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot snapshot1 = snapshot.child("cart").child(user.getUid());
                cartList.clear();
                productList.clear();
                for (DataSnapshot post : snapshot1.getChildren()){
                    Cart cart = post.getValue(Cart.class);
                    cartList.add(cart);
                }

                for(Cart cart : cartList){
                    DataSnapshot snapshot2 = snapshot.child("product").child(cart.getProductId());
                    Product p = new Product(
                            snapshot2.child("productId").getValue(String.class),
                            snapshot2.child("productName").getValue(String.class),
                            snapshot2.child("productDesc").getValue(String.class),
                            snapshot2.child("price").getValue(Double.class),
                            snapshot2.child("discount").getValue(Integer.class),
                            snapshot2.child("category").getValue(String.class),
                            snapshot2.child("remaining").getValue(Integer.class),
                            snapshot2.child("url1").getValue(String.class),
                            snapshot2.child("url2").getValue(String.class),
                            snapshot2.child("url3").getValue(String.class));

                    productList.add(p);
                }
                adapter = new CartRecyclerViwAdapter(getApplicationContext(),cartList, productList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                calculateCartToatal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(cartList.size()!=0){
                   requestOrder();
               }
           }
       });

    }

    private void calculateCartToatal() {

        double total = 0.00;
        for(int i=0; i<cartList.size(); i++){
            Cart cartTemp = cartList.get(i);
            Product productTemp = productList.get(i);
            total += productTemp.getPrice()*cartTemp.getQuantity();
        }

        txtCartTotal.setText(String.valueOf(total));
    }



    private void requestOrder() {

        Order order;

        final AlertDialog dialog2;

        //create address (step 2)

        final AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(CartViw.this);
        alertdialog2.setTitle("Delivery Location");
        alertdialog2.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        String[] animals = {"My Address", "This Location"};

        alertdialog2.setSingleChoiceItems(animals, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        break;
                    case 1:
                        dialog.dismiss();
                        ProgressDialog d = new ProgressDialog(CartViw.this);
                        d.setMessage("loading GPS data...");
                        d.show();

                        GpsTracker gpsTracker = new GpsTracker(CartViw.this);

                            if(gpsTracker.canGetLocation()){
                                double latitude = gpsTracker.getLatitude();
                                double longitude = gpsTracker.getLongitude();

                                        if(latitude != 0 && longitude != 0 ) {
                                            DatabaseReference ref = FirebaseDb.databaseReference().child("order");
                                            Date currentTime = Calendar.getInstance().getTime();
                                            Order order = new Order(ref.push().getKey(), cartList, Double.parseDouble(txtCartTotal.getText().toString()), 0.0, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(), "address", FirebaseAuth.getInstance().getCurrentUser().getUid().toString(), currentTime.toString(), latitude, longitude, 0);

                                            boolean b = FirebaseDb.getInstance().create("order", order.getOrderId(), order);

                                            if (b) {
                                                FirebaseDb.getInstance().delete("cart", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                d.dismiss();
                                                Toast.makeText(CartViw.this, "Order Placed ! ThankYou ", Toast.LENGTH_SHORT).show();

                                            }
                                        }

                            }else {
                                d.dismiss();
                                gpsTracker.showSettingsAlert();
                            }
                }

            }
        });





//        alertdialog2.setMessage("set Delivery Location to Deliver");
//        alertdialog2.setIcon(R.drawable.ic_shopping_cart_black_24dp);
//
//        //final EditText address = new EditText(CartViw.this);
//        final Button address = new Button(CartViw.this);
//        address.setText("Deliver to This Location");
//        address.setBackgroundColor(Color.parseColor("#27ae60"));
//        address.setTextColor(Color.parseColor("#ecf0f1"));
//        address.setMaxWidth(20);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        address.setLayoutParams(layoutParams);
//        alertdialog2.setView(address);
//
//        address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(CartViw.this, "presses", Toast.LENGTH_SHORT).show();
//                GpsTracker gpsTracker = new GpsTracker(CartViw.this);
//
//                if(gpsTracker.canGetLocation()){
//                    double latitude = gpsTracker.getLatitude();
//                    double longitude = gpsTracker.getLongitude();
//
//
//                    Log.d("TAG", "=============== L : "+latitude+"  Lo: "+longitude);
//                }else {
//                    gpsTracker.showSettingsAlert();
//                }
//
//
//            }
//        });
//        alertdialog2.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                DatabaseReference ref = FirebaseDb.databaseReference().child("order");
//                Date currentTime = Calendar.getInstance().getTime();
//                Order order = new Order(ref.push().getKey(),cartList,Double.parseDouble(txtCartTotal.getText().toString()),0.0,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(),"address",FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),currentTime.toString(),0);
//
//
//                boolean b = FirebaseDb.getInstance().create("order", order.getOrderId(), order);
//
//                if(b){
//                    FirebaseDb.getInstance().delete("cart",FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    Toast.makeText(CartViw.this, "Order Placed ! ThankYou ", Toast.LENGTH_SHORT).show();
//
//                }
//
//
//            }
//        });


        dialog2 = alertdialog2.create();





        //confirm billing details (step 1)

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(CartViw.this);
        alertdialog.setTitle("Confirm Bill Details..");
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        TableLayout layout = new TableLayout(this);
        TableRow tableRow = new TableRow(getBaseContext());
        tableRow.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        TableRow.LayoutParams capParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        capParams.setMargins(0, 20, 10, 10);

        TextView textView1 = new TextView(getBaseContext());
        textView1.setText("PRODUCT");
        tableRow.addView(textView1,capParams);
        TextView textView3 = new TextView(getBaseContext());
        textView3.setText("QUANTITY");
        tableRow.addView(textView3,capParams);
        TextView textView4 = new TextView(getBaseContext());
        textView4.setText("PRICE");
        tableRow.addView(textView4,capParams);
        layout.addView(tableRow);


        for(int i=0; i<cartList.size(); i++) {

            Cart c = cartList.get(i);
            Product pro = productList.get(i);

            TableRow row = new TableRow(this);
            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
            p.setMargins(7, 5, 10, 0);

            TextView t1 = new TextView(getApplicationContext());
            t1.setText(pro.getProductName());
            row.addView(t1, p);

            TextView t2 = new TextView(getApplicationContext());
            t2.setText(c.getQuantity()+"");
            row.addView(t2, p);

            TextView t3 = new TextView(getApplicationContext());
            double t = pro.getPrice()*c.getQuantity();
            t3.setText(t+"");
            row.addView(t3, p);


            layout.addView(row);
        }


        alertdialog.setView(layout);

        alertdialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                dialog2.show();
                //finish();
            }
        });

        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertdialog.show();

    }

}
