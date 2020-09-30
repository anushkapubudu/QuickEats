package lk.hndit.admin_quickeats.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.hndit.admin_quickeats.R;
import lk.hndit.admin_quickeats.model.Category;
import lk.hndit.admin_quickeats.model.Product;
import lk.hndit.admin_quickeats.services.FirebaseDb;
import lk.hndit.admin_quickeats.services.FirebaseStorage;

public class EditProduct extends AppCompatActivity {

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
    private Intent intent;
    private Product product;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        intent = getIntent();
        productId = intent.getStringExtra("productId");

        edtxtproductName = findViewById(R.id.edit_product_name);
        edtxtDescription = findViewById(R.id.edit_product_discription);
        edtxtPrice = findViewById(R.id.edit_product_price);
        edtxtDiscount = findViewById(R.id.edit_product_discount);
        edtxtRemaning = findViewById(R.id.edit_product_remainningitems);
        imageviw1 = findViewById(R.id.edit_product_imageviw1);
        imageviw2 = findViewById(R.id.edit_product_imageviw2);
        imageviw3 = findViewById(R.id.edit_product_imageviw3);
        addProductbtn = findViewById(R.id.btnUpdatePrduct);
        spinner = findViewById(R.id.edit_product_spinner);
        dialog = new ProgressDialog(EditProduct.this);
        categoryNamelist = new ArrayList<>();
        categoryIDlist = new ArrayList<>();
        loadCategoryList();
        loadProductdata();

        dialog.setMessage("Uploading Images...");


        imageviw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGalary(PICK_IMAGE_1);
            }
        });

        imageviw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGalary(PICK_IMAGE_2);
            }
        });

        imageviw3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGalary(PICK_IMAGE_3);
            }
        });


//
//        btnUploadImages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (uri1 != null && uri2 != null && uri3 != null && edtxtproductName.getText() != null) {
//                    dialog.show();
//                    uploadPicture(uri1, 1);
//                    uploadPicture(uri2, 2);
//                    uploadPicture(uri3, 3);
//                }
//            }
//        });



        addProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtxtproductName.getText() != null && edtxtDescription.getText() != null && edtxtPrice.getText() != null && edtxtDiscount.getText() != null && edtxtRemaning.getText() != null ){
                    dialog.show();
                    int i = spinner.getSelectedItemPosition();
                    String categoryID = categoryIDlist.get(i);
                    Product p = new Product(productId, edtxtproductName.getText().toString(), edtxtDescription.getText().toString(), Double.valueOf(edtxtPrice.getText().toString()), Integer.valueOf(edtxtDiscount.getText().toString()), categoryID, Integer.valueOf(edtxtRemaning.getText().toString()), downloadUrl1==null? product.getUrl1() : downloadUrl1 , downloadUrl2==null? product.getUrl2() : downloadUrl2, downloadUrl3==null? product.getUrl3() : downloadUrl3);
                    Task<Void> update = FirebaseDb.databaseReference().child("product").child(productId).setValue(p);

                    update.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProduct.this, "Updated!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            EditProduct.this.finish();

                        }
                    });


                }
            }
        });
    }

    private void loadProductdata() {

        FirebaseDb.databaseReference().child("product").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                if(product != null){
                    edtxtproductName.setText(product.getProductName());
                    edtxtDescription.setText(product.getProductDesc());
                    edtxtPrice.setText(String.valueOf(product.getPrice()));
                    edtxtDiscount.setText(Integer.toString(product.getDiscount()));
                    edtxtRemaning.setText(Integer.toString(product.getRemaining()));

                    for(int i=0; i<categoryIDlist.size(); i++){
                        if(categoryIDlist.get(i).equals(product.getCategory())){
                            spinner.setSelection(i);
                        }
                    }

                    Picasso.get().load(product.getUrl1()).into(imageviw1);
                    Picasso.get().load(product.getUrl2()).into(imageviw2);
                    Picasso.get().load(product.getUrl3()).into(imageviw3);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProduct.this, android.R.layout.simple_spinner_dropdown_item, categoryNamelist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadPicture(Uri uri1, final int index) {
        final String filename = edtxtproductName.getText().toString() + "" + index;
        UploadTask task = FirebaseStorage.getInstance().addNewPicture("product", filename, uri1);
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
                    dialog.show();
                    uploadPicture(uri1, 1);
                    break;

                case PICK_IMAGE_2:
                    uri2 = data.getData();
                    imageviw2.setImageURI(uri2);
                    dialog.show();
                    uploadPicture(uri2, 2);
                    break;
                case PICK_IMAGE_3:
                    uri3 = data.getData();
                    imageviw3.setImageURI(uri3);
                    dialog.show();
                    uploadPicture(uri3, 3);
            }
        }
    }
}
