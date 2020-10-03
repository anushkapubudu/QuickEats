package lk.hndit.quickeats.activity.user.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.ProductDetailViw;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseAuth;
import lk.hndit.quickeats.services.FirebaseDb;

public class ProductRecyclerViwAdapter extends RecyclerView.Adapter<ProductRecyclerViwAdapter.ViwHolder> {

    private List<Product> productList;
    private Context context;

    public ProductRecyclerViwAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_row_user, parent, false);
        return new ViwHolder(view,context, new onFoodClick() {
            @Override
            public void foodClick(int p) {

                Intent intent = new Intent(context, ProductDetailViw.class);
                intent.putExtra("productId",productList.get(p).getProductId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void addToCart(final int p) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(parent.getContext());
                dialog.setTitle("Add to Cart");
                dialog.setMessage("set Quantity");
                dialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
                final ElegantNumberButton button = new ElegantNumberButton(parent.getContext());
                button.setNumber("1");
                dialog.setView(button);

                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date currentTime = Calendar.getInstance().getTime();
                        Cart cart = new Cart(FirebaseAuth.getInstance().getCurrentUser().getUid(),productList.get(p).getProductId(),Integer.parseInt(button.getNumber()),currentTime.toString());
                        FirebaseDb.databaseReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(cart.getProductId()).setValue(cart);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtProductName.setText(product.getProductName());
        holder.txtPrice.setText(String.valueOf(product.getPrice()));
        holder.txtDiscount.setText(String.valueOf(product.getDiscount()));
        holder.txtitemsremainning.setText(String.valueOf(product.getRemaining()));
        Picasso.get().load(product.getUrl1()).into(holder.productImage);



    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView productImage;
        private TextView txtProductName,txtPrice,txtDiscount,txtitemsremainning;
        private onFoodClick onFoodClick;
        private ImageButton btnAddtoCart;

        public ViwHolder(@NonNull View itemView,Context ctx,onFoodClick onFoodClick) {
            super(itemView);
            this.onFoodClick=onFoodClick;

            productImage = itemView.findViewById(R.id.product_thumbnail_user);

            txtitemsremainning = itemView.findViewById(R.id.product_Remaining_count_user);
            txtProductName = itemView.findViewById(R.id.product_item_name_user);
            txtPrice = itemView.findViewById(R.id.product_price_user);
            txtDiscount = itemView.findViewById(R.id.product_discount_user);
            btnAddtoCart = itemView.findViewById(R.id.product_btn_addTocart);

            itemView.setOnClickListener(this);
            btnAddtoCart.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.product_btn_addTocart:
                    onFoodClick.addToCart(this.getLayoutPosition());
                    break;
                default:
                    onFoodClick.foodClick(this.getAdapterPosition());
                    break;
            }

        }
    }

    private interface onFoodClick{
        void foodClick(int p);
        void addToCart(int p);
    }
}
