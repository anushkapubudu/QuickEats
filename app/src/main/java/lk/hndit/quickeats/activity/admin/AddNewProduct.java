package lk.hndit.quickeats.activity.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.quickeats.R;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.model.Product;
import lk.hndit.quickeats.services.FirebaseDb;
import lk.hndit.quickeats.services.FirebaseStorage;

public class AddNewProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_1 = 100;
    private static final int PICK_IMAGE_2 = 200;
    private static final int PICK_IMAGE_3 = 300;
    private List<String> categoryNamelist;
    private List<String> categoryIDlist;
    private EditText edtxtproductName;
    private EditText edtxtDescription;
    private EditText edtxtPrice;
    private EditText edtxtDiscount;
    private EditText edtxtRemaning;
    private ImageButton imageviw1;
    private ImageButton imageviw2;
    private ImageButton imageviw3;
    private Button addProductbtn;
    private Uri uri1;
    private Uri uri2;
    private Uri uri3;
    private Button btnUploadImages;
    private String downloadUrl1;
    private String downloadUrl2;
    private String downloadUrl3;
    private ProgressDialog dialog;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        edtxtproductName = findViewById(R.id.edtxtPrdctName);
        edtxtDescription = findViewById(R.id.edtxtDscrptn);
        edtxtPrice = findViewById(R.id.edtxtPrice);
        edtxtDiscount = findViewById(R.id.edtxtDiscount);
        edtxtRemaning = findViewById(R.id.edtxtRemaining);
        imageviw1 = findViewById(R.id.imageView1);
        imageviw2 = findViewById(R.id.imageView2);
        imageviw3 = findViewById(R.id.imageView3);
        addProductbtn = findViewById(R.id.button);
        spinner = findViewById(R.id.spinner);
        dialog = new ProgressDialog(AddNewProduct.this);
        categoryNamelist = new ArrayList<>();
        categoryIDlist = new ArrayList<>();
        loadCategoryList();


        dialog.setMessage("Uploading Images...");

        imageviw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtxtproductName.getText().length() == 0){
                    edtxtproductName.setError("Required!");
                }else{
                    selectImageFromGalary(PICK_IMAGE_1);
                }
            }
        });

        imageviw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtxtproductName.getText().length() == 0){
                    edtxtproductName.setError("Required!");
                }else{
                    selectImageFromGalary(PICK_IMAGE_2);
                }
            }
        });

        imageviw3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtxtproductName.getText().length() == 0){
                    edtxtproductName.setError("Required!");
                }else{
                    selectImageFromGalary(PICK_IMAGE_3);
                }
            }
        });





        addProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtxtproductName.getText().length() != 0 && edtxtDescription.getText().length() != 0 && edtxtPrice.getText().length() != 0 && edtxtDiscount.getText().length() != 0 && edtxtRemaning.getText().length() != 0 && uri1 != null && uri2 != null && uri3 != null){

                    dialog.show();
                    uploadPicture(uri1,1);
                    uploadPicture(uri2,2);
                    uploadPicture(uri3,3);

                    if(downloadUrl1 != null && downloadUrl2 != null && downloadUrl3 != null){


                    }
                }

            }
        });

    }


    private void loadCategoryList() {

        FirebaseDb.databaseReference().child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryNamelist.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categoryNamelist.add(category.getCategoryName());
                    categoryIDlist.add(category.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddNewProduct.this, android.R.layout.simple_spinner_dropdown_item, categoryNamelist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void uploadPicture(Uri urito, final int index) {
        final String filename = edtxtproductName.getText().toString() + "" + index;
        UploadTask task = FirebaseStorage.getInstance().addNewPicture("product", filename, urito);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseStorage.getstorageReference().child("product").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if (index == 1) {
                            downloadUrl1 = uri.toString();
                            dialog.dismiss();
                        } else if (index == 2) {
                            downloadUrl2 = uri.toString();
                            dialog.dismiss();
                        } else if (index == 3) {
                            downloadUrl3 = uri.toString();
                            dialog.dismiss();
                            dialog.dismiss();
                            dialog.setMessage("Creating...");
                            dialog.show();
                            int i = spinner.getSelectedItemPosition();
                            String categoryID = categoryIDlist.get(i);
                            String id = FirebaseDb.databaseReference().child("product").push().getKey();
                            Product product = new Product(id, edtxtproductName.getText().toString(), edtxtDescription.getText().toString(), Double.valueOf(edtxtPrice.getText().toString()), Integer.valueOf(edtxtDiscount.getText().toString()), categoryID, Integer.valueOf(edtxtRemaning.getText().toString()), downloadUrl1, downloadUrl2, downloadUrl3);
                            boolean b = FirebaseDb.getInstance().create("product",product.getProductId(),product);

                            if (b) {
                                dialog.dismiss();
                                Toast.makeText(AddNewProduct.this, "New Product Added Susscessfull", Toast.LENGTH_SHORT).show();
                                edtxtproductName.setText("");
                                edtxtDescription.setText("");
                                edtxtPrice.setText("");
                                edtxtDiscount.setText("");
                                edtxtRemaning.setText("");
                                uri1 = null;
                                uri2 = null;
                                uri3 = null;
                                imageviw1.setImageURI(uri1);
                                imageviw2.setImageURI(uri2);
                                imageviw3.setImageURI(uri3);
                            }

                        }


                    }
                });

            }
        });
    }


    private void selectImageFromGalary(int pickImage) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, pickImage);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_1:
                    uri1 = data.getData();
                    imageviw1.setImageURI(uri1);
                    //dialog.show();
                    //uploadPicture(uri1, 1);
                    break;

                case PICK_IMAGE_2:
                    uri2 = data.getData();
                    imageviw2.setImageURI(uri2);
                   // dialog.show();
                   // uploadPicture(uri2, 2);
                    break;
                case PICK_IMAGE_3:
                    uri3 = data.getData();
                    imageviw3.setImageURI(uri3);
//                    uploadPicture(uri3, 3);

            }
        }
    }

}
