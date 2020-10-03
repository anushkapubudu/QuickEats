package lk.hndit.quickeats.activity.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.OrderDetailViwUser;
import lk.hndit.quickeats.activity.user.ProductViwUser;
import lk.hndit.quickeats.model.Cart;
import lk.hndit.quickeats.model.Order;
import lk.hndit.quickeats.util.Common;

public class OrderRecyclerViwAdapter extends RecyclerView.Adapter<OrderRecyclerViwAdapter.ViwHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderRecyclerViwAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_user, parent, false), context, new MyOrderListner() {
            @Override
            public void OnOrderClick(int position) {


                Intent intent = new Intent(context, OrderDetailViwUser.class);
                intent.putExtra("orderId",orderList.get(position).getOrderId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {

        Order order = orderList.get(position);
        List<Cart> carts = order.getCartList();
        holder.txtOrderID.setText(order.getOrderId());
        holder.txtOrderPrice.setText(String.valueOf(order.getTotalPrice()));
        holder.txtOrderQuentity.setText(String.valueOf(carts.size()));
        holder.txtOrderDatetime.setText(order.getDateTime());
        String status = Common.getOrderStatus(order.getStatus());
        holder.txtOrderStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtOrderID,txtOrderPrice,txtOrderQuentity,txtOrderDatetime, txtOrderStatus;
        private MyOrderListner myOrderListner;


        public ViwHolder(@NonNull View itemView, Context context, MyOrderListner myOrderListner) {
            super(itemView);
            txtOrderID = itemView.findViewById(R.id.user_order_id);
            txtOrderPrice = itemView.findViewById(R.id.user_order_price);
            txtOrderQuentity = itemView.findViewById(R.id.user_order_quentity);
            txtOrderDatetime = itemView.findViewById(R.id.user_order_datetime);
            txtOrderStatus = itemView.findViewById(R.id.user_order_status);

            this.myOrderListner=myOrderListner;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myOrderListner.OnOrderClick(this.getAdapterPosition());
        }
    }


    public interface MyOrderListner{
        void OnOrderClick(int position);
    }
}
