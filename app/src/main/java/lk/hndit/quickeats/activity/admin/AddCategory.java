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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.services.FirebaseStorage;

public class AddCategory extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private EditText edtxtCategoryName;
    private ImageButton imageButton;
    private Button btnCreateCategory;
    private Uri imageUri;
    private String url;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);



        btnCreateCategory = findViewById(R.id.btnCreatecategory);
        edtxtCategoryName = findViewById(R.id.edtxtCategoryName);
        imageButton = findViewById(R.id.categoryaddimage);

        dialog = new ProgressDialog(AddCategory.this);

        final StorageReference ref = FirebaseStorage.getstorageReference();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGalary();
            }
        });


        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (imageUri != null && edtxtCategoryName.getText().length() != 0) {

                    dialog.setMessage("Creating new Category..");
                    dialog.show();

                    UploadTask uploadTask = ref.child("category").child(edtxtCategoryName.getText().toString().trim()).putFile(imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.child("category").child(edtxtCategoryName.getText().toString().trim()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    String id = FirebaseDb.databaseReference().child("category").push().getKey();
                                    Category category = new Category(id, edtxtCategoryName.getText().toString(), url);
                                    boolean b = FirebaseDb.getInstance().create("category",category.getId(),category);

                                    if (b) {
                                        Toast.makeText(AddCategory.this, "New Category Added Successfully!", Toast.LENGTH_LONG).show();
                                        edtxtCategoryName.setText("");
                                        imageUri = null;
                                        imageButton.setImageURI(imageUri);
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }


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

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            imageUri = data.getData();
            imageButton.setImageURI(imageUri);

        }
    }


}


