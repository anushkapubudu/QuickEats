package lk.hndit.admin_quickeats.activitys.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.hndit.admin_quickeats.activitys.OrderTrackingAdmin;
import lk.hndit.admin_quickeats.R;
import lk.hndit.admin_quickeats.model.Order;

public class AdminOrderRecyclerViwadpter extends RecyclerView.Adapter<AdminOrderRecyclerViwadpter.ViwHolder> {

    private Context context;
    private List<Order> orderList;

    public AdminOrderRecyclerViwadpter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_row, parent, false), context, new onOrderClick() {
            @Override
            public void onOrderClick(int p) {
                Intent intent = new Intent(context, OrderTrackingAdmin.class);
                intent.putExtra("latitude",orderList.get(p).getLatitude());
                intent.putExtra("longitude",orderList.get(p).getLongitude());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {

        Order order = orderList.get(position);
        holder.txtOrderId.setText(order.getOrderId());
        holder.txtOrderQuentity.setText(String.valueOf(order.getStatus())); //todo:
        holder.txtOrderPrice.setText(String.valueOf(order.getTotalPrice()));

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtOrderId,txtOrderQuentity,txtOrderPrice;
        private ImageButton btnEdit,btnDel;
        private onOrderClick onOrderClick;

        public ViwHolder(@NonNull View itemView, Context ctx, onOrderClick onOrderClick) {
            super(itemView);
            this.onOrderClick = onOrderClick;
            txtOrderId =itemView.findViewById(R.id.admin_order_id);
            txtOrderPrice =itemView.findViewById(R.id.admin_order_price);
            txtOrderQuentity =itemView.findViewById(R.id.admin_order_quentity);
            btnEdit = itemView.findViewById(R.id.admin_btn_edit_order);
            btnDel = itemView.findViewById(R.id.admin_btn_del_order);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOrderClick.onOrderClick(this.getAdapterPosition());
        }
    }

    private interface onOrderClick{
        void onOrderClick(int p);

    }
}
