package lk.hndit.quickeats.activity.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.adapters.AdminOrderRecyclerViwadpter;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.services.FirebaseDb;

public class OrderViwAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Order> orderList;
    private AdminOrderRecyclerViwadpter adpter;
    //private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_viw_admin);


//        bottomNavigationView = findViewById(R.id.admin_order_bottom_navigation);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.order_waiting:
//                                break;
//
//                            case R.id.order_preparing:
//                                break;
//
//                            case R.id.order_delivery:
//                                break;
//
//                            case R.id.order_complete:
//                                break;
//
//                            default:
//                                return false;
//
//                        }
//                        return true;
//                    }
//                });




        orderList = new ArrayList<>();
        recyclerView = findViewById(R.id.admin_order_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseDb.databaseReference().child("order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot post : snapshot.getChildren()){
                    Order order = post.getValue(Order.class);
                    orderList.add(order);
                }
                adpter = new AdminOrderRecyclerViwadpter(getApplicationContext(),orderList);
                adpter.notifyDataSetChanged();
                recyclerView.setAdapter(adpter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
