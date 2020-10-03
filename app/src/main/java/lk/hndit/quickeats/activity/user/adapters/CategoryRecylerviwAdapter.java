package lk.hndit.quickeats.activity.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.activity.user.ProductViwUser;
import lk.hndit.quickeats.model.Category;

public class CategoryRecylerviwAdapter extends RecyclerView.Adapter<CategoryRecylerviwAdapter.ViewHolder> {

    private List<Category> list;
    private Context context;


    public CategoryRecylerviwAdapter(List<Category> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_row_user,parent,false);
        return new ViewHolder(view, context, new MyCategoryListner() {
            @Override
            public void OnCategoryClick(int position) {
                Intent intent = new Intent(context, ProductViwUser.class);
                intent.putExtra("categoryId",list.get(position).getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);
        holder.textView.setText(category.getCategoryName());
        Picasso.get().load(category.getImageUrl()).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;
        private MyCategoryListner categoryListner;

        public ViewHolder(@NonNull View itemView,Context ctx,MyCategoryListner categorylistner) {
            super(itemView);
            this.categoryListner=categorylistner;
            textView = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.category_thumbnail);


            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
                categoryListner.OnCategoryClick(this.getAdapterPosition());
        }
    }

    public interface MyCategoryListner{
        void OnCategoryClick(int position);
    }
}
