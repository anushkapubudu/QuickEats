package lk.hndit.quickeats.activity.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.CartViw;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;

public class CartRecyclerViwAdapter extends RecyclerView.Adapter<CartRecyclerViwAdapter.ViwHolder> {

    private Context context;
    private List<Cart> cartList;
    private List<Product> productList;

    public CartRecyclerViwAdapter(Context context, List<Cart> cartList, List<Product> productList) {
        this.context = context;
        this.cartList = cartList;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        return new ViwHolder(LayoutInflater.from(context).inflate(R.layout.cart_row_user, parent, false), context, new MyClickListener() {
            @Override
            public void onDelete(final int p) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure remove this product from your cart?");
                builder.setIcon(R.drawable.ic_remove_shopping_cart_black_24dp);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDb.databaseReference().child("cart").child(cartList.get(p).getuId()).child(cartList.get(p).getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(parent.getContext());
                                dialog.setMessage("Done!");
                                dialog.show();
                            }
                        });

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {

        Cart cart = cartList.get(position);
        Product product = productList.get(position);
        holder.txtProductName.setText(product.getProductName());
        holder.txtPrice.setText(String.valueOf(product.getPrice() * cart.getQuantity()));
        holder.txtQueantity.setText(String.valueOf(cart.getQuantity()));
        Picasso.get().load(product.getUrl1()).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtProductName,txtPrice,txtQueantity;
        private ImageView imageView;
        private ImageButton btndel;
        private MyClickListener listener;
        public ViwHolder(@NonNull View itemView, Context context, MyClickListener myClickListener) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.cart_product_item_name_user);
            txtPrice = itemView.findViewById(R.id.cart_product_price_user);
            txtQueantity = itemView.findViewById(R.id.cart_product_quantity_user);
            imageView = itemView.findViewById(R.id.cart_thumbnail_user);
            btndel = itemView.findViewById(R.id.cart_del_product_btn);
            listener = myClickListener;

            btndel.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.cart_del_product_btn:
                    listener.onDelete(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }

    public interface MyClickListener {
        void onDelete(int p);
    }
}