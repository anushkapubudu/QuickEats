package lk.hndit.quickeats.activity.admin.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.admin.EditProduct;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;

public class AdminProductsRecycleViwAdapter extends RecyclerView.Adapter<AdminProductsRecycleViwAdapter.ViwHolder> {

    private List<Product> list = new ArrayList();
    private Context context;

    public AdminProductsRecycleViwAdapter(List<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new ViwHolder(view, context, new MyClickListener() {
            @Override
            public void onEdit(int p) {
                Toast.makeText(context, "edit  "+p, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(context, EditProduct.class);
                intent.putExtra("productId",list.get(p).getProductId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }

            @Override
            public void onDelete(int p) {
                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                Product product = list.get(p);
                FirebaseDb.getInstance().delete("product", product.getProductId());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {
            Product product = list.get(position);
            holder.txtProductName.setText(product.getProductName());
            holder.txtPrice.setText(String.valueOf(product.getPrice()));
            holder.txtDiscount.setText(String.valueOf(product.getDiscount()));
            Picasso.get().load(product.getUrl1()).into(holder.productImage);




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtProductName,txtPrice,txtDiscount;
        private ImageView productImage,itemRemainning;
        private ImageButton btnedit;
        private ImageButton btndelete;
        private MyClickListener listener;

        public ViwHolder(@NonNull View itemView,Context ctx, MyClickListener listener) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.product_item_name);
            txtPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_thumbnail);
            btnedit = itemView.findViewById(R.id.product_edit_btn);
            btndelete = itemView.findViewById(R.id.product_delete_btn);
            txtDiscount = itemView.findViewById(R.id.product_discount);
            itemRemainning = itemView.findViewById(R.id.product_Remaining_count_image);

            this.listener = listener;

            btnedit.setOnClickListener(this);
            btndelete.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.product_edit_btn:
                    listener.onEdit(this.getLayoutPosition());
                    break;
                case R.id.product_delete_btn:
                    listener.onDelete(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }

    public interface MyClickListener {
        void onEdit(int p);
        void onDelete(int p);
    }
}
