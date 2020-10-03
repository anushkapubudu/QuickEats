package lk.hndit.quickeats.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.TrackingOrderAdmin;
import lk.hndit.quickeats.activity.user.adapters.CartRecyclerViwAdapter;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.util.Common;
import lk.hndit.quickeats.util.GpsTracker;

public class CartViw extends AppCompatActivity {

    private TextView txtCartTotal, txtCartDiscount;
    private Button btnPlaceOrder;
    private RecyclerView recyclerView;
    private CartRecyclerViwAdapter adapter;
    private List<Cart> cartList;
    private List<Product> productList;
    private ProgressDialog progressDialog;
    private boolean isFirst = true;
    private Order order;
    private double discount = 0.00;




    private BottomNavigationView bottomNavigationView;
    private LatLng myLatLng;
    private String myAddress;

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



        order = new Order();
        txtCartTotal = findViewById(R.id.cartTotal);
        txtCartDiscount = findViewById(R.id.cartDiscount);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        recyclerView = findViewById(R.id.cartrecyclerviw);
        cartList = new ArrayList<>();
        productList = new ArrayList<>();
        bottomNavigationView = findViewById(R.id.bottom_navigation_cart);

        progressDialog = new ProgressDialog(this);

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
                discount =0.00;
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
                   Toast.makeText(CartViw.this, "Loading GPS data..Please wait...", Toast.LENGTH_LONG).show();
                   progressDialog.setMessage("Loading GPS data...");
                   progressDialog.show();
                   loadLocationData();
               }
           }
       });

    }

    private void calculateCartToatal() {

        double total = 0.00;

        for(int i=0; i<cartList.size(); i++){
            Cart cartTemp = cartList.get(i);
            Product productTemp = productList.get(i);
            total += (productTemp.getPrice()-productTemp.getPrice()*productTemp.getDiscount()/100) *cartTemp.getQuantity();
            discount+=productTemp.getPrice()*productTemp.getDiscount()/100;
        }

        txtCartTotal.setText(String.valueOf(total));
        txtCartDiscount.setText(String.valueOf(discount));
    }



    private void requestOrder() {


        //alert dialog get contact number (dialog 3)

        final EditText contactDialogView = new EditText(this);
        AlertDialog.Builder cndialog = new AlertDialog.Builder(this);
        contactDialogView.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        cndialog.setView(contactDialogView);
        cndialog.setTitle("Contact details..");
        cndialog.setIcon(R.drawable.ic_call_black_24dp);




        cndialog.setPositiveButton("Place Order !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order.setContactNo(contactDialogView.getText().toString());

                Date currentTime = Calendar.getInstance().getTime();
                DatabaseReference ref = FirebaseDb.databaseReference().child("order");
                String orderId = ref.push().getKey();

                order.setDateTime(currentTime.toString());
                order.setCartList(cartList);
                order.setDiscount(discount);
                order.setLatitude(myLatLng.latitude);
                order.setLongitude(myLatLng.longitude);
                order.setTotalPrice(Double.parseDouble(txtCartTotal.getText().toString()));
                order.setStatus(0);
                order.setOrderId(orderId);
                order.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());


                if(order.getOrderId() != null && order.getCartList() != null && order.getUserId() != null){
                    ref.child(orderId).setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDb.databaseReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                            Toast.makeText(CartViw.this, "Your Order Received, Thank you!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CartViw.this,OrderViwUser.class));
                            finish();
                        }
                    });
                }

            }
        });


        final AlertDialog contactDialog = cndialog.create();



        //alert dialog get address (dialog 2)

        final EditText addressDialogView = new EditText(this);
        AlertDialog.Builder addialog = new AlertDialog.Builder(this);
        addressDialogView.setText(myAddress);
        addialog.setView(addressDialogView);
        addialog.setTitle("Delivery Address..");
        addialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        addialog.setNeutralButton("reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                order.setAddress(addressDialogView.getText().toString());
                contactDialog.show();
                contactDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#f1c40f"));

            }
        });


        final AlertDialog addressDialog = addialog.create();


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
                progressDialog.dismiss();
                addressDialog.show();
                addressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#f1c40f"));

            }
        });

        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog.dismiss();
            }
        });

        AlertDialog dialog = alertdialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#f1c40f"));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#2ecc71"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#e74c3c"));


    }



    private void loadLocationData() {


        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }




        android.location.LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                myLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                /*------- To get city name from coordinates -------- */

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        System.out.println(addresses.get(0).getLocality());

                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String postalCode = addresses.get(0).getPostalCode();

                        myAddress = address+", "+city+". ";
                        if(isFirst){
                            requestOrder();
                            isFirst = false;
                        }

                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


        GpsTracker gpsTracker = new GpsTracker(CartViw.this);

        if(gpsTracker.canGetLocation()){

        }else {
            gpsTracker.showSettingsAlert();
        }






    }



}
