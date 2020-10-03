package lk.hndit.quickeats.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.services.FirebaseStorage;

public class EditCategory extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private EditText edtxtCategoryName;
    private ImageButton imageButton;
    private Button btnUpdateCategory;
    private Uri imageUri;
    private String url;
    private ProgressDialog dialog;
    private String categoryID;
    private Intent intent;
    private Category category;
    private List<Category> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        intent = getIntent();
        categoryID = intent.getStringExtra("categoryID");

        btnUpdateCategory = findViewById(R.id.btnUpdatecategory);
        edtxtCategoryName = findViewById(R.id.edtxtUpCategoryName);
        imageButton = findViewById(R.id.categoryUpdateimage);
        dialog = new ProgressDialog(EditCategory.this);
        list = new ArrayList();

        loadcategoryData();

        final StorageReference ref = FirebaseStorage.getstorageReference();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtxtCategoryName.getText().length() == 0){
                    edtxtCategoryName.setError("Required!");
                }else {
                    selectImageFromGalary();
                }
            }
        });

        btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setMessage("Creating...");
                dialog.show();

                Category c = new Category(categoryID,edtxtCategoryName.getText().toString().trim(),url != null ? url : category.getImageUrl());
                FirebaseDb.getInstance().update("category",category.getId(),category);

//                task.addOnSuccessListener(new OnSuccessListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        dialog.dismiss();
//                        Toast.makeText(EditCategory.this, "Updated Successful!", Toast.LENGTH_SHORT).show();
//                        EditCategory.this.finish();
//
//                    }
//                });

                dialog.dismiss();

            }
        });

    }

    private void loadcategoryData() {

        FirebaseDb.databaseReference().child("category").child(categoryID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category = snapshot.getValue(Category.class);
                if(category != null){
                    edtxtCategoryName.setText(category.getCategoryName());
                    Picasso.get().load(category.getImageUrl()).into(imageButton);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void selectImageFromGalary() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final String filename = edtxtCategoryName.getText().toString().trim();

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            imageUri = data.getData();
            imageButton.setImageURI(imageUri);
            dialog.setMessage("uploading...");
            dialog.show();
            UploadTask task = FirebaseStorage.getInstance().addNewPicture("category", filename, imageUri);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getstorageReference().child("category").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri u) {
                            url = u.toString();
                            dialog.dismiss();
                        }
                    });

                }
            });


        }
    }
}
