package lk.hndit.quickeats.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.adapters.OrderRecyclerViwAdapter;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class OrderViwUser extends AppCompatActivity {

    private BottomNavigationView topNavigationView;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private OrderRecyclerViwAdapter adapter;
    private List<Order> activeOrderList;
    private List<Order> recentOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);

        topNavigationView = findViewById(R.id.user_order_top_navigation);
        bottomNavigationView = findViewById(R.id.bottom_navigation_order);


        int meniitemid = bottomNavigationView.getSelectedItemId();


        activeOrderList = new ArrayList<>();
        recentOrderList = new ArrayList<>();

        recyclerView = findViewById(R.id.user_order_recyclerviw);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDb.databaseReference().child("order").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activeOrderList.clear();
                recentOrderList.clear();
                for(DataSnapshot post : snapshot.getChildren()){
                    Order order = post.getValue(Order.class);

                    if(order.getStatus() == 0 || order.getStatus() == 1 || order.getStatus() == 2){
                        activeOrderList.add(order);
                    }else if(order.getStatus() == 3){
                        recentOrderList.add(order);
                    }

                }

                adapter = new OrderRecyclerViwAdapter(getApplicationContext(),activeOrderList);
                adapter.notifyDataSetChanged();;
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        bottomNavigationView.setSelectedItemId(R.id.page_3);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.page_1:
                                startActivity(new Intent(OrderViwUser.this, UserDashboard.class));
                                finish();
                                break;

                            case R.id.page_2:
                                startActivity(new Intent(OrderViwUser.this, CartViw.class));
                                finish();
                                break;

                            case R.id.page_3:
                                break;

                            default:
                                return false;

                        }
                        return true;
                    }
                });





        //top bar

        topNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.order_active:
                                setViwtoAdapter(0);
                                break;

                            case R.id.order_recent:
                                setViwtoAdapter(1);
                                break;

                                default:
                                return false;

                        }
                        return true;
                    }
                });
    }


    public void setViwtoAdapter(int meniitemid){
        if(meniitemid == 0){
            adapter = new OrderRecyclerViwAdapter(getApplicationContext(),activeOrderList);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }

        if(meniitemid == 1){
            adapter = new OrderRecyclerViwAdapter(getApplicationContext(),recentOrderList);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }

    }
}
