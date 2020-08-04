package lk.hndit.quickeats.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lk.hndit.quickeats.model.Category;
import lk.hndit.quickeats.model.Product;

public class FirebaseDb {

    private static FirebaseDb db;
    private static FirebaseDatabase database  = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();

    private  FirebaseDb() {
    }

    public static FirebaseDb getInstance(){
        return (db==null) ? new FirebaseDb() : db;
    }

    public static DatabaseReference databaseReference(){
        return reference;
    }
    //product
    public boolean addNewProduct(Product product){
        String key = reference.child("product").push().getKey();
        product.setProductId(key);

        Task<Void> task = reference.child("product").child(key).setValue(product);

        if(task.isCanceled()){
            return false;
        }else {
            return true;
        }
    }

    public void updateProduct(Product product){
        reference.child("product").child(product.getProductId()).setValue(product);
    }

    public void deleteProduct(String productID){
        reference.child("product").child(productID).removeValue();
    }


    //category
    public boolean addNewCategory(Category category){
        String key = reference.child("category").push().getKey();
        category.setId(key);


        final Task<Void> task = reference.child("category").child(key).setValue(category);

        if(task.isCanceled()){
            return false;
        }else {
            return true;
        }


    }

    public Task updateCategory(Category category){
        final boolean re;
        Task task = reference.child("category").child(category.getId()).setValue(category);

        return task;

    }

    public void deleteCategory(Category category){
        reference.child("category").child(category.getId()).removeValue();
    }

}
