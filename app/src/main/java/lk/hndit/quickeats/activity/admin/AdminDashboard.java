package lk.hndit.quickeats.activity.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.adapters.AdminProductsRecycleViwAdapter;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.util.GpsTracker;

public class AdminDashboard extends AppCompatActivity {

    private FloatingActionButton factbtnAdd;
    private RecyclerView productrecyclerView;
    private AdminProductsRecycleViwAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference dbref;
    private AlertDialog.Builder builder;
    private List<Product> productList;
    private RelativeLayout productlayout;
    private RelativeLayout categorylayout;
    private LinearLayout orderlayout;
    private List<Category> categoryList;
    private List<Order> orderList;
    private TextView categoryCount;
    private TextView productCount;
    private ProgressDialog dialog;

    private TextView txtOrderWaitingCount;
    private TextView txtOrderPreparingCount;
    private TextView txtOrderDeliveryCount;
    private TextView txtOrderCompletedCount;


    private GpsTracker gpsTracker;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }



        gpsTracker = new GpsTracker(AdminDashboard.this);

        if(gpsTracker.canGetLocation()){
        }else {
            gpsTracker.showSettingsAlert();
        }






        productlayout = findViewById(R.id.productReLayout);
        categorylayout = findViewById(R.id.categoryReLaout);
        orderlayout  = findViewById(R.id.orderlayout);
        factbtnAdd = findViewById(R.id.factbtnAdd);
        productrecyclerView = findViewById(R.id.productrecyclerView);
        categoryCount = findViewById(R.id.categoryCount);
        productCount = findViewById(R.id.productCount);
        txtOrderWaitingCount = findViewById(R.id.txt_order_waiting);
        txtOrderPreparingCount = findViewById(R.id.txt_order_preparing);
        txtOrderDeliveryCount = findViewById(R.id.txt_order_delivery);
        txtOrderCompletedCount = findViewById(R.id.txt_order_completed);

        categoryList= new ArrayList<>();
        productList = new ArrayList<>();
        orderList = new ArrayList<>();


        dialog = new ProgressDialog(AdminDashboard.this);

        dialog.setMessage("Analysing data");
        dialog.show();

        loadUserDatatoDashboard();

        //TODO: CHANGE HEAR
        orderlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(AdminDashboard.this, OrderViwAdmin.class));
                startActivity(new Intent(AdminDashboard.this, OrderViwAdmin.class));

            }
        });

        productlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, ProductViwAdmin.class));

            }
        });

        categorylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, CategoryInAdmin.class));

            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        final PopupMenu menu = new PopupMenu(this, factbtnAdd);
        menu.getMenu().add("Add new Category");
        menu.getMenu().add("Add new Product");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                                               switch (item.getTitle().toString()){

                                                   case "Add new Category" :
                                                       startActivity(new Intent(AdminDashboard.this, AddCategory.class));
                                                       break;

                                                   case "Add new Product":
                                                       startActivity(new Intent(AdminDashboard.this,AddNewProduct.class));
                                                       break;
                                               }

                                               return true; }
                                        }
        );

        factbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
    }

    private void loadUserDatatoDashboard() {

        DatabaseReference ref = FirebaseDb.databaseReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //category
                DataSnapshot categorySnapshot = snapshot.child("category");
                categoryList.clear();

                for(DataSnapshot post : categorySnapshot.getChildren()){
                    Category category = post.getValue(Category.class);
                    categoryList.add(category);
                }

                categoryCount.setText(String.valueOf(categoryList.size()));

                //product
                DataSnapshot productSnapshot = snapshot.child("product");
                productList.clear();

                for(DataSnapshot post : productSnapshot.getChildren()){
                    Product product = post.getValue(Product.class);
                    productList.add(product);
                }

                productCount.setText(String.valueOf(productList.size()));

                //order
                DataSnapshot orderSnapshot = snapshot.child("order");
                orderList.clear();
                int waiting=0;
                int preparing=0;
                int delivery=0;
                int complete=0;

                for (DataSnapshot post : orderSnapshot.getChildren()){
                    Order order = post.getValue(Order.class);

                    switch (order.getStatus()){
                        case 0:
                            waiting+=1;
                            break;
                        case 1:
                            preparing+=1;
                            break;
                        case 2:
                            delivery+=1;
                            break;
                        case 3:
                            complete+=1;
                            break;
                        default:
                            break;
                    }
                }

                txtOrderWaitingCount.setText(String.valueOf(waiting));
                txtOrderPreparingCount.setText(String.valueOf(preparing));
                txtOrderDeliveryCount.setText(String.valueOf(delivery));
                txtOrderCompletedCount.setText(String.valueOf(complete));

                //dismiss progress dialog
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        //load category data
//        ref.child("category").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                categoryList.clear();
//                for(DataSnapshot postSnapshot : snapshot.getChildren()){
//                    Category category = postSnapshot.getValue(Category.class);
//                    categoryList.add(category);
//                }
//                categoryCount.setText(String.valueOf(categoryList.size()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        //load product data
//        ref.child("product").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                productList.clear();
//                for(DataSnapshot postSnapshot : snapshot.getChildren()){
//                    Product product = postSnapshot.getValue(Product.class);
//                    productList.add(product);
//                }
//                productCount.setText(String.valueOf(productList.size()));
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

}
