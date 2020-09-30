package lk.hndit.admin_quickeats.activitys.adapters;

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

import java.util.List;

import lk.hndit.admin_quickeats.R;
import lk.hndit.admin_quickeats.activitys.EditCategory;
import lk.hndit.admin_quickeats.model.Category;
import lk.hndit.admin_quickeats.services.FirebaseDb;

public class AdminCategoryRecycleviwAdapter extends RecyclerView.Adapter<AdminCategoryRecycleviwAdapter.ViwHolder> {

    private List<Category> list;
    private Context context;

    public AdminCategoryRecycleviwAdapter(List<Category> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false);
        return new ViwHolder(view, context, new MyClickListener() {
            @Override
            public void onEdit(int p) {
                Toast.makeText(context, "edit pressed", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, EditCategory.class);
                intent.putExtra("categoryID",list.get(p).getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onDelete(int p) {

                Toast.makeText(context, "delete pressed", Toast.LENGTH_SHORT).show();
                FirebaseDb.getInstance().deleteCategory(list.get(p));
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViwHolder holder, int position) {
        Category category = list.get(position);
        holder.txtcategorytName.setText(category.getCategoryName());
        Picasso.get().load(category.getImageUrl()).into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViwHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MyClickListener listener;
        private TextView txtcategorytName;
        private ImageView categoryImage;
        private ImageButton btnedit;
        private ImageButton btndelete;

        public ViwHolder(@NonNull View itemView,Context cntxt, MyClickListener listener){
            super(itemView);
            this.listener=listener;
            txtcategorytName = itemView.findViewById(R.id.category_item_name);
            categoryImage = itemView.findViewById(R.id.category_thumbnail);
            btnedit = itemView.findViewById(R.id.category_edit_btn);
            btndelete = itemView.findViewById(R.id.category_delete_btn);

            btndelete.setOnClickListener(this);
            btnedit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.category_edit_btn:
                    listener.onEdit(this.getLayoutPosition());
                    break;
                case R.id.category_delete_btn:
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
