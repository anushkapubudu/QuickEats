package lk.hndit.quickeats.activity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Product;

public class ProductRecyclerViwAdapter extends RecyclerView.Adapter<ProductRecyclerViwAdapter.ViwHolder> {

    private List<Product> productList;
    private Context context;

    public ProductRecyclerViwAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_row_user, parent, false);
        return new ViwHolder(view,context, new onFoodClick() {
            @Override
            public void foodClick(int p) {
                Toast.makeText(context, "= "+p, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtProductName.setText(product.getProductName());
        holder.txtPrice.setText(String.valueOf(product.getPrice()));
        holder.txtDiscount.setText(String.valueOf(product.getDiscount()));
        Picasso.get().load(product.getUrl1()).into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView productImage,itemsremainning;
        private TextView txtProductName,txtPrice,txtDiscount;
        private onFoodClick onFoodClick;
        public ViwHolder(@NonNull View itemView,Context ctx,onFoodClick onFoodClick) {
            super(itemView);
            this.onFoodClick=onFoodClick;

            productImage = itemView.findViewById(R.id.product_thumbnail_user);
            itemsremainning = itemView.findViewById(R.id.product_Remaining_count_image_user);
            txtProductName = itemView.findViewById(R.id.product_item_name_user);
            txtPrice = itemView.findViewById(R.id.product_price_user);
            txtDiscount = itemView.findViewById(R.id.product_discount_user);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFoodClick.foodClick(this.getAdapterPosition());
        }
    }

    private interface onFoodClick{
        void foodClick(int p);
    }
}
