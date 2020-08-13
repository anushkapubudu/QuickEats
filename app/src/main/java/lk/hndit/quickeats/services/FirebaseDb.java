package lk.hndit.quickeats.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    public boolean create(String child,String key,Object object){

        Task<Void> task = reference.child(child).child(key).setValue(object);

        if(task.isCanceled()){
            return false;
        }else {
            return true;
        }
    }

    public List<Object> getAll(String child){

        final List<Object> list = new ArrayList<>();
        reference.child(child).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for(DataSnapshot post : snapshot.getChildren()){
                    Object o = snapshot.getValue(Object.class);
                    list.add(o);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return list;
    }

    public void update(String child,String key,Object object){
        reference.child(child).child(key).setValue(object);
    }

    public void delete(String child,String key){
        reference.child(child).child(key).removeValue();
    }


    public String getKey(){
        return reference.push().getKey();
    }

}
