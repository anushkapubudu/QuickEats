package lk.hndit.quickeats.activity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.adapters.ProductRecyclerViwAdapter;
import lk.hndit.quickeats.model.Product;

public class SearchProduct extends AppCompatActivity {

    private EditText txtsearch;
    private RecyclerView recyclerView;
    private ProductRecyclerViwAdapter adapter;
    private List<Product> productList;
    private TextView notfpundtext;
    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        txtsearch = findViewById(R.id.edtxtSearchviw);
        notfpundtext = findViewById(R.id.txtNotfoundtext);
        btnSearch = findViewById(R.id.btnsearchviw);

        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.searchrecyclerviw);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




    }
}
